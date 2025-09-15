package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.Instant;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Vote {
    private Instant publishedAt;
    private int id;

    //Relasjoner
    @JsonIdentityReference(alwaysAsId = true)
    private User voter;
    @JsonIdentityReference(alwaysAsId = true)
    private Poll poll;
    @JsonIdentityReference(alwaysAsId = true)
    private VoteOption option;

    public Vote() {}

    public int getId() {return id;}

    public void setId(int id) {
        this.id = id;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Poll getPoll() {
        return poll;
    }

    @JsonIdentityReference(alwaysAsId = false)
    public VoteOption getOption() {
        return option;
    }

    public void setOption(VoteOption option) {
        this.option = option;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}
