package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Poll {
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private int id;

    //Relasjoner
    @JsonIdentityReference(alwaysAsId = true)
    private User creator;
    @JsonIdentityReference(alwaysAsId = true)
    private List<VoteOption> options;
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vote> votes;

    public Poll() {
        this.options = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public void addVote(Vote v) {
        v.setPoll(this);
        votes.add(v);
    }

    public void addOption(VoteOption opt) {
        opt.setPoll(this);
        this.options.add(opt);
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public User getCreator() {
        return creator;
    }

    public void setCreator(User user) {
        this.creator = user;
        if (user != null && !user.getPollsCreated().contains(this)) {
            user.getPollsCreated().add(this); // hold retur-koblingen i sync
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIdentityReference(alwaysAsId = false)
    public List<VoteOption> getVoteOptions() {
        return options;
    }

    public void setVoteOptions(List<VoteOption> voteOptions) {
        this.options = voteOptions;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }
}
