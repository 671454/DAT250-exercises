package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {
    private String username;
    private String email;
    private int id;

    //relasjoner
    @JsonIdentityReference(alwaysAsId = true)
    private List<Poll> pollsCreated;
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vote> votes;

    public User() {
        this.pollsCreated = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public List<Poll> getPollsCreated() {
        return pollsCreated;
    }

    public void setPollsCreated(List<Poll> pollsCreated) {
        this.pollsCreated = pollsCreated;
    }

    @JsonIdentityReference(alwaysAsId = true)
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
        v.setVoter(this);
        votes.add(v);
    }
}
