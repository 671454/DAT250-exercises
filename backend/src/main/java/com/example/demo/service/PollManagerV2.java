package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.Poll;
import com.example.demo.domain.Vote;
import com.example.demo.domain.VoteOption;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Primary
@Component
public class PollManagerV2 {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Poll> polls = new ConcurrentHashMap<>();
    private final Map<Long, Vote> votes = new ConcurrentHashMap<>();
    private final Map<Long, VoteOption> options = new ConcurrentHashMap<>();

    private final AtomicLong userSeq = new AtomicLong(1);
    private final AtomicLong pollSeq = new AtomicLong(1);
    private final AtomicLong voteSeq = new AtomicLong(1);
    private final AtomicLong optionSeq = new AtomicLong(1);



    /*--------------------- User ---------------------*/
    public Long createUser(String username, String email) {
        if (username == null || email == null) {
            throw new IllegalArgumentException("Username and email must be provided");
        }

        Long id = userSeq.getAndIncrement();
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setUsername(username);
        users.put(id, u);
        return id;
    }

    public User getUser(Long id) {
        if(users.get(id) == null)
            throw new NoSuchElementException("No user with given id " + id);
        return users.get (id);
    }

    public Collection<User> getAllUsers(){
        return users.values();
    }



    /*--------------------- Poll ---------------------*/
    public Long createPoll(Long creatorId, String question, List<String> options, Instant validUntil) {
        if(users.get(creatorId) == null)
            throw new NoSuchElementException("No user with given id " + creatorId);
        if(question == null)
            throw new IllegalArgumentException("Question cannot be empty");
        if(options.size() < 2)
            throw new IllegalArgumentException("Poll needs at least 2 options");

        Long id = pollSeq.getAndIncrement();
        Poll p = new Poll();
        p.setCreatedBy(users.get(creatorId));
        p.setId(id);
        p.setPublishedAt(Instant.now());
        p.setValidUntil(validUntil);
        p.setQuestion(question);

        int order = 0;
        for(String s : options) {
            Long optId = optionSeq.getAndIncrement();
            VoteOption opt = new VoteOption();
            opt.setId(optId);
            opt.setCaption(s);
            opt.setPresentationOrder(order++);
            p.addOption(opt);
            this.options.put(optId, opt);
        }
        polls.put(id, p);
        return id;
    }


    private Poll getPoll(Long pollId) {
        if(polls.get(pollId) == null)
            throw new NoSuchElementException("No poll with id of " + pollId);
        return polls.get(pollId);
    }

    public Collection<Poll> listPolls(){
        return polls.values();
    }



    /*--------------------- Vote ---------------------*/
    public Long vote(Long userId, Long pollId, Long optionId, Instant when) {
        User u = getUser(userId);
        Poll p = getPoll(pollId);
        VoteOption option = getVoteOption(optionId);

        if(option.getPoll() == null || !option.getPoll().getId().equals(pollId))
            throw new IllegalArgumentException("Either provided option is non existent or does not belong to given poll with id " + pollId);

        Instant now = when != null ? when : Instant.now();
        if(p.getValidUntil() != null && now.isAfter(p.getValidUntil()))
            throw new IllegalStateException("Poll is not open for voting. Closed at " + p.getValidUntil() + ". Attempt to vote at " + when);

        //Check if user already voted for this poll, if so remove old.
        Vote existing = p.getVotes().stream().filter(v -> v.getVoter().getId().equals(userId)).findFirst().orElse(null);

        //remapping the vote
        if(existing != null) {
            detachVoteFromOption(existing);
            existing.setOption(option);
            existing.setPublishedAt(now);
            existing.setPoll(p);
            option.addVote(existing);
            return existing.getId();
        }

        Long voteId = voteSeq.getAndIncrement();
        Vote v = new Vote();
        v.setPublishedAt(now);
        v.setId(voteId);
        v.setPoll(p);
        v.setOption(option);
        v.setVoter(u);
        u.addVote(v);
        option.addVote(v);
        p.addVote(v);
        votes.put(voteId, v);
        return voteId;
    }




    /*--------------------- VoteOption ---------------------*/
    private VoteOption getVoteOption(Long optionId) {
        if(options.get(optionId) == null)
            throw new NoSuchElementException(optionId + " is not an option for any poll");
        return options.get(optionId);
    }



    /*--------------------- Helpers ---------------------*/
    private void detachVoteFromOption(Vote existing) {
        VoteOption opt = existing.getOption();
        if(opt != null)
            opt.getVotes().remove(existing);
    }

}
