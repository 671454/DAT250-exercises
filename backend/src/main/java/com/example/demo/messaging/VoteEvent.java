package com.example.demo.messaging;

import java.time.Instant;

public record VoteEvent(Long pollId, Long optionId, Long userIdOrNull, Instant when) {
}
