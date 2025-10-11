package com.example.demo.messaging;

import java.time.Instant;

public record PollCreatedEvent(Long pollId, String question, Instant when) {
}
