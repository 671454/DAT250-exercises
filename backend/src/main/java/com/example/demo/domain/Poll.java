package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "polls")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private Instant publishedAt;
    private Instant validUntil;

    //Relasjoner
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private User createdBy;

    @OrderBy("presentationOrder ASC")
    @OneToMany(mappedBy="poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = false)
    private List<VoteOption> options;

    @OrderBy("publishedAt DESC")
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Vote> votes;


    public Poll() {
        this.options = new ArrayList<>();
        this.votes = new ArrayList<>();
    }

    public void addVote(Vote v) {
        if(v == null) return;
        v.setPoll(this);
        if(!this.votes.contains(v))
            this.votes.add(v);
    }

    public void addOption(VoteOption opt) {
        if(opt == null) return;
        opt.setPoll(this);
        if(!this.options.contains(opt))
            this.options.add(opt);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User user) {
        this.createdBy = user;
        if (user != null && !user.getPollsCreated().contains(this)) {
            user.getPollsCreated().add(this); // hold retur-koblingen i sync
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<VoteOption> getOptions() {
        return options;
    }

    public void setOptions(List<VoteOption> voteOptions) {
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

    /**
     *
     * Adds a new option to this Poll and returns the respective
     * VoteOption object with the given caption.
     * The value of the presentationOrder field gets determined
     * by the size of the currently existing VoteOptions for this Poll.
     * I.e. the first added VoteOption has presentationOrder=0, the secondly
     * registered VoteOption has presentationOrder=1 and so on.
     */
    public VoteOption addVoteOption(String caption) {
        if(caption == null || caption.isBlank())
            throw new IllegalArgumentException("No provided caption for the polls option");

        for(VoteOption opt : this.options){
            if(opt.getCaption().equals(caption))
                return opt;
        }

        VoteOption opt = new VoteOption();
        opt.setPoll(this);
        opt.setCaption(caption);
        opt.setPresentationOrder(this.options.size());
        this.options.add(opt);
        return opt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return Objects.equals(id, poll.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
