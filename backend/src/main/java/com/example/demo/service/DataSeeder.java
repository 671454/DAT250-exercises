package com.example.demo.service;

import com.example.demo.domain.Poll;
import com.example.demo.domain.VoteOption;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * This class i just for the purpose of simplifying the testing between frontend and backend with som mock data.
 */

@Component
@Profile("!test")
public class DataSeeder {
    private final PollManagerV2 manager;

    public DataSeeder(PollManagerV2 manager) {
        this.manager = manager;
    }

    @PostConstruct
    void seed() {
        if(!manager.listPolls().isEmpty())
            return;

        Long olaId, kariId;
        try {
            olaId = manager.createUser("Ola Nordmann", "ola@gmail.com");
            kariId = manager.createUser("Kari Nordmann", "kari@gmail.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Long pollId = manager
                .createPoll(olaId, "Gogo or Gaga?", List.of("Gogo", "Gaga"), Instant.now().plus(1, ChronoUnit.DAYS));
        Poll poll = manager.listPolls().stream().filter(p -> p.getId() == pollId).findFirst().orElseThrow();

        Long gogoId = poll.getOptions().stream()
                .filter(opt -> "Gogo".equals(opt.getCaption())).findFirst().map(VoteOption::getId)
                .orElse(poll.getOptions().get(0).getId());

        Long gagaId = poll.getOptions().stream()
                .filter(opt -> "Gaga".equals(opt.getCaption())).findFirst().map(VoteOption::getId)
                .orElse(poll.getOptions().get(1).getId());

        manager.vote(olaId, pollId, gogoId, Instant.now());
        manager.vote(kariId, pollId, gagaId, Instant.now());
    }
}
