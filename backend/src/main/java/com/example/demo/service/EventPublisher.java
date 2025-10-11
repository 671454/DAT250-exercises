package com.example.demo.service;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.messaging.PollCreatedEvent;
import com.example.demo.messaging.VoteEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EventPublisher {
    private final RabbitTemplate rabbit;

    public EventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void publishPollCreated(Long pollId, String question) {
        var event = new PollCreatedEvent(pollId, question, Instant.now());
        rabbit.convertAndSend(RabbitMQConfig.EXCHANGE, "poll."+pollId+".created", event);
    }

    public void publishVote(VoteEvent event) {
        rabbit.convertAndSend(RabbitMQConfig.EXCHANGE, "poll."+event.pollId()+".vote", event);
    }
}
