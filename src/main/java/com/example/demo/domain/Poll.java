package com.example.demo.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Poll {
    private String question;
    private Instant publishedAt;
    private Instant validUntil;
    private int id;

    //Relasjoner
    private User creator;
    private List<VoteOption> options;
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
        this.options.add(opt);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User user) {
        this.creator = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
