package com.example.demo.service;

import com.example.demo.domain.Poll;
import com.example.demo.domain.User;
import com.example.demo.domain.Vote;
import com.example.demo.domain.VoteOption;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PollManager {
    //ConcurrentHashMap brukes da de er tråd-sikre
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Poll> polls = new ConcurrentHashMap<>();
    private final Map<Integer, Vote> votes = new ConcurrentHashMap<>();
    private final Map<Integer, VoteOption> options = new ConcurrentHashMap<>();

    private final Map<Integer, List<Integer>> userPolls = new ConcurrentHashMap<>();
    private final Map<Integer, List<Integer>> userVotes = new ConcurrentHashMap<>();
    private final Map<Integer, List<Integer>> pollOptions = new ConcurrentHashMap<>();
    private final Map<Integer, List<Integer>> votesForOption = new ConcurrentHashMap<>();

    //VoteID --> optionID
    private final Map<Integer, Integer> optionOfVote = new ConcurrentHashMap<>();

    //UserID --> (pollID --> voteID) connect a User to a vote of a given poll
    private final Map<Integer, Map<Integer, Integer>> voteIdByUserAndPoll = new ConcurrentHashMap<>();

    //Kan oppdateres atomisk, altså tråd-sikre.
    private final AtomicInteger userSeq = new AtomicInteger(0);
    private final AtomicInteger pollSeq = new AtomicInteger(0);
    private final AtomicInteger voteSeq = new AtomicInteger(0);
    private final AtomicInteger optionSeq = new AtomicInteger(0);


    /*---------------------------Vote-----------------------------*/
    public int createVote(Instant publishedAt, int userId, int pollId, int optionId){
        if(!users.containsKey(userId))
            throw new NoSuchElementException("No user with ID: " + userId);

        Poll p = polls.get(pollId);
        if(p == null)
            throw new NoSuchElementException("No poll with ID: " + pollId);

        if (pollOptions.get(pollId) == null || pollOptions.get(pollId).size() < 2)
           throw new IllegalStateException("Poll " + pollId + " must contain at least two options");

        if(p.getPublishedAt() == null)
            throw new IllegalStateException("Poll not published yet");

        Instant now = (publishedAt != null ? publishedAt : Instant.now());

        //Check if now is after polls deadline
        if(p.getValidUntil() != null && !now.isBefore(p.getValidUntil()))
            throw new IllegalStateException("Polls deadline is met, unable to vote");

        //Check if optionID is not connected to pollId
        if(!pollOptions.getOrDefault(pollId, List.of()).contains(optionId))
            throw new IllegalArgumentException("Option " + optionId + " does not belong to poll " + pollId);

        //Check if user already has voted in this poll, if thats the case delete old vote from option and create new
        Integer existingVoteId = voteIdByUserAndPoll.getOrDefault(userId, Map.of()).get(pollId);
        if(existingVoteId != null) {
            Integer existingOptionId = optionOfVote.get(existingVoteId); //Gives optionID for users vote

            if(existingOptionId != null && !existingOptionId.equals(optionId)) {
               if(votesForOption.get(existingOptionId) != null) {
                   votesForOption.get(existingOptionId).remove((Integer) existingVoteId);
               }
               if(votesForOption.get(optionId) == null) {
                   votesForOption.put(optionId, new ArrayList<>());
               }
               votesForOption.get(optionId).add(existingVoteId);
               optionOfVote.put(existingVoteId, optionId);
            }
            Vote v = votes.get(existingVoteId);
            if(v != null) v.setPublishedAt(now);

            return existingVoteId;
        }
        //No previous uservote for this poll
        int voteId = voteSeq.getAndIncrement();
        Vote v = new Vote();
        v.setPublishedAt(now);
        votes.put(voteId, v);

        if(userVotes.get(userId) == null) {
            userVotes.put(userId, new ArrayList<>());
        }
            userVotes.get(userId).add(voteId);

        if(votesForOption.get(optionId) == null) {
            votesForOption.put(optionId, new ArrayList<>());
        }
        votesForOption.get(optionId).add(voteId);

        optionOfVote.put(voteId, optionId);

        if(voteIdByUserAndPoll.get(userId) == null) {
            voteIdByUserAndPoll.put(userId, new HashMap<Integer, Integer>());
        }
        voteIdByUserAndPoll.get(userId).put(pollId, voteId);

        return voteId;
    }



    /*---------------------------User-----------------------------*/
    public int createUser(String username, String email) {
        int id = userSeq.getAndIncrement();
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        users.put(id, user);
        userPolls.putIfAbsent(id, new ArrayList<>());
        userVotes.putIfAbsent(id, new ArrayList<>());
        return id;
    }

    public User getUser(int id) {
        if(users.get(id) != null) {
            return users.get(id);
        }
        return null;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public Map<Integer, User> getUsersById() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(users));
    }

    /*---------------------------Poll-----------------------------*/
    //Creates a poll with at least two options included as domain model requires
    public int createPoll(String question, Instant publishedAt, Instant validUntil, int creator, List<String> options) {
        if(options == null || options.size() < 2) {
            throw new IllegalArgumentException("Poll must contain at least two options");
        }
        int pollId = createPoll(question, publishedAt, validUntil, creator);
        int order = 1;
        for (String s : options) {
            pollAddOption(pollId, s, order++);
        }
        if (publishedAt != null) {
            publishPoll(pollId, publishedAt);
        }

        return pollId;
    }

    public int createPoll(String question, Instant publishedAt, Instant validUntil, int creator) {
        if(users.get(creator) == null) {
            throw new NoSuchElementException("No user with given id: " + creator);
        }
        int id = pollSeq.getAndIncrement();

        Poll poll = new Poll();
        poll.setPublishedAt(publishedAt);
        poll.setValidUntil(validUntil);
        poll.setQuestion(question);
        polls.put(id, poll);

        if(!userPolls.containsKey(creator)) {
            userPolls.put(creator, new ArrayList<>());
        }
        userPolls.get(creator).add(id);
        pollOptions.putIfAbsent(id, new ArrayList<>());

        return id;
    }


    private void publishPoll(int pollId, Instant publishedAt) {
        Poll p = pollExist(pollId);
        p.setPublishedAt(publishedAt != null ? publishedAt : Instant.now());
    }


    private int pollAddOption(int pollId, String option, int order){
        pollExist(pollId); //Poll need to exist in order to add options

        int optionId = optionSeq.getAndIncrement();
        VoteOption voteOption = new VoteOption();
        voteOption.setCaption(option);
        voteOption.setPresentationOrder(order);

        options.put(optionId, voteOption);
        if(!pollOptions.containsKey(pollId)) {
            pollOptions.put(pollId, new ArrayList<>());
        }
        pollOptions.get(pollId).add(optionId);

        return optionId;
    }

    private Poll pollExist(int pollId) {
        Poll p = polls.get(pollId);
        if(p == null) throw new NoSuchElementException("No poll with pollID: " + pollId);
        return p;
    }

}
