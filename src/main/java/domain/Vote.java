package domain;

import java.time.Instant;

public class Vote {
    private Instant publishedAt;

    public Vote() {

    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setPoll(Poll poll) {
    }
}
