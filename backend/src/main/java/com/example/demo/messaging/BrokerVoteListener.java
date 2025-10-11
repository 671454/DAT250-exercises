package com.example.demo.messaging;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.service.PollManagerV2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BrokerVoteListener {
    private final PollManagerV2 manager;

    public BrokerVoteListener(PollManagerV2 manager){
        this.manager = manager;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_APP_VOTES)
    public void onVote(VoteEvent event) {
        if(event.pollId() != null && event.userIdOrNull() != null) {
            manager.vote(event.userIdOrNull(), event.pollId(), event.optionId(), event.when());
        } else {
            //Anonym stemme
            manager.voteAnonymous(event.pollId(), event.optionId(), event.when());
        }
    }

}
