package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant publishedAt;

    //Relasjoner
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private User voter;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private Poll poll;

    @ManyToOne(optional = false, fetch = FetchType.LAZY) //Mulig denne ikke fungerer med en nye createPoll-metoden...
    @JsonIdentityReference(alwaysAsId = true)
    private VoteOption option;

    public Vote() {}

    public Long getId() {return id;}

    public void setId(Long id) {
        this.id = id;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public Poll getPoll() {
        return poll;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
