package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name="users") //This one needs to be mapped since "USER" is a reserved word in H2
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;

    //relasjoner
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Poll> pollsCreated;

    @OneToMany(mappedBy = "voter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vote> votes;


    public User() {
        this.pollsCreated = new ArrayList<>();
        this.votes = new ArrayList<>();
    }
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.pollsCreated = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Poll> getPollsCreated() {
        return pollsCreated;
    }

    public void setPollsCreated(List<Poll> pollsCreated) {
        this.pollsCreated = pollsCreated;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addVote(Vote v) {
        if(v==null) return;
        v.setVoter(this);
        if(!this.votes.contains(v))
            votes.add(v);
    }

    public void addPoll(Poll p) {
        if (p == null) return;
        if (!pollsCreated.contains(p)) {
            pollsCreated.add(p);
        }
        if (p.getCreatedBy() != this) {
            p.setCreatedBy(this); // hold retur-koblingen i sync
        }
    }

    /**
     * Creates a new Poll object for this user
     * with the given poll question
     * and returns it.
     */
    public Poll createPoll(String question) {
        Poll poll = new Poll();
        if(question == null || question.isEmpty())
            throw new IllegalArgumentException("No question provided!");
        poll.setQuestion(question);
        poll.setCreatedBy(this);
        poll.setPublishedAt(Instant.now());

        if(!this.pollsCreated.contains(poll)){
            this.pollsCreated.add(poll);
        }
        return poll;
    }

    /**
     * Creates a new Vote for a given VoteOption in a Poll
     * and returns the Vote as an object.
     */
    public Vote voteFor(VoteOption option) {
        if(option == null || option.getPoll() == null)
            throw new IllegalArgumentException("The provided vote-option is either null or is not connected to a poll");

        Poll poll = option.getPoll();

        //Check if user already has voted on this poll. If so, and the option is the same, do nothing, but if different option delete old and create a new one.
        Vote existing = null;
        for(Vote v : this.votes) {
            if(v.getPoll() == poll){
                existing = v;
                break;
            }
        }

        // Do nothing just return the original
        if(existing != null && existing.getOption() == option) {
            return existing;
        }

        //Since user already voted in this poll update the vote to reflect changes
        if(existing != null) {
            VoteOption oldOpt = existing.getOption();

            if(oldOpt != null){
                oldOpt.getVotes().remove(existing);
            }
            existing.setOption(option);
            existing.setPublishedAt(Instant.now());
            option.addVote(existing);
            return existing;
        }

        //No existing vote from this user therefor create a new one
        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setVoter(this);
        vote.setOption(option);
        vote.setPublishedAt(Instant.now());

        if(!this.votes.contains(vote))
            this.addVote(vote);
        option.addVote(vote);
        poll.addVote(vote);
        return vote;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
