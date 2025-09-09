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
            throw new IllegalArgumentException("Username og email is empty");
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
        return users.get(id);
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
            opt.setCaption(question);
            opt.setPresentationOrder(order++);
            p.addOption(opt);
            this.options.put(optId, opt);
        }
        polls.put(id, p);
        return id;
    }




    /*--------------------- Vote ---------------------*/




    /*--------------------- Helpers ---------------------*/
    private List<VoteOption> createVoteOptions(int pollId, List<String> options) {
        int order = 1;
        for (String s : options) {
            int id = optionSeq.getAndIncrement();
            VoteOption opt = new VoteOption();
            opt.setId(id);
            opt.setPresentationOrder(order);
            order++;
            opt.setPoll(pollId);
        }
    }

}
