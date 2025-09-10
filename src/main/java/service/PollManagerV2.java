package service;

import com.example.demo.domain.User;
import com.example.demo.domain.Poll;
import com.example.demo.domain.Vote;
import com.example.demo.domain.VoteOption;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PollManagerV2 {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, Poll> polls = new ConcurrentHashMap<>();
    private final Map<Integer, Vote> votes = new ConcurrentHashMap<>();
    private final Map<Integer, VoteOption> options = new ConcurrentHashMap<>();

    private final AtomicInteger userSeq = new AtomicInteger(1);
    private final AtomicInteger pollSeq = new AtomicInteger(1);
    private final AtomicInteger voteSeq = new AtomicInteger(1);
    private final AtomicInteger optionSeq = new AtomicInteger(1);



    /*--------------------- User ---------------------*/
    public int createUser(String username, String email) {
        if (username == null || email == null) {
            throw new IllegalArgumentException("Username and email must be provided");
        }

        int id = userSeq.getAndIncrement();
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setUsername(username);
        users.put(id, u);
        return id;
    }

    public User getUser(int id) {
        if(users.get(id) == null)
            throw new NoSuchElementException("No user with given id " + id);
        return users.get (id);
    }

    public Collection<User> getAllUsers(){
        return users.values();
    }



    /*--------------------- Poll ---------------------*/
    public int createPoll(int creatorId, String question, List<String> options, Instant validUntil) {
        if(users.get(creatorId) == null)
            throw new NoSuchElementException("No user with given id " + creatorId);
        if(question == null)
            throw new NoSuchElementException("Question cannot be empty");
        if(options.size() < 2)
            throw new IllegalArgumentException("Poll needs at least 2 options");

        int id = pollSeq.getAndIncrement();
        Poll p = new Poll();
        p.setCreator(users.get(creatorId));
        p.setId(id);
        p.setPublishedAt(Instant.now());
        p.setValidUntil(validUntil);
        p.setQuestion(question);

        int order = 1;
        for(String s : options) {
            int optId = optionSeq.getAndIncrement();
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


    private Poll getPoll(int pollId) {
        if(polls.get(pollId) == null)
            throw new NoSuchElementException("No poll with id of " + pollId);
        return polls.get(pollId);
    }

    public Collection<Poll> listPolls(){
        return polls.values();
    }



    /*--------------------- Vote ---------------------*/
    public int vote(int userId, int pollId, int optionId, Instant when) {
        User u = getUser(userId);
        Poll p = getPoll(pollId);
        VoteOption option = getVoteOption(optionId);

        if(option.getPoll() == null || option.getPoll().getId() != pollId)
            throw new IllegalArgumentException("Either provided option is non existent or does not belong to given poll with id " + pollId);

        Instant now = when != null ? when : Instant.now();
        if(p.getValidUntil() != null && now.isAfter(p.getValidUntil()))
            throw new IllegalStateException("Poll is not open for voting. Closed at " + p.getValidUntil() + ". Attempt to vote at " + when);

        //Check if user already voted for this poll, if so remove old.
        Vote existing = p.getVotes().stream().filter(v -> v.getVoter().getId() == userId).findFirst().orElse(null);

        //remapping the vote
        if(existing != null) {
            detachVoteFromOption(existing);
            existing.setOption(option);
            existing.setPublishedAt(Instant.now());
            option.addVote(existing);
            return existing.getId();
        }

        int voteId = voteSeq.getAndIncrement();
        Vote v = new Vote();
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
    private VoteOption getVoteOption(int optionId) {
        if(options.get(optionId) == null)
            throw new NoSuchElementException(optionId + "is not an option for any poll");
        return options.get(optionId);
    }



    /*--------------------- Helpers ---------------------*/
    private void detachVoteFromOption(Vote existing) {
        VoteOption opt = existing.getOption();
        if(opt != null)
            opt.getVotes().remove(existing);
    }

}
