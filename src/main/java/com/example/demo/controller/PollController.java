package com.example.demo.controller;

import com.example.demo.domain.Vote;
import com.example.demo.domain.Poll;
import com.example.demo.domain.VoteOption;
import com.example.demo.service.PollManagerV2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/polls")
public class PollController {

    private final PollManagerV2 manager;

    public PollController(PollManagerV2 manager) {
        this.manager = manager;
    }


    /**
     * To remove the clients concern of creating a complete Poll object, which consists of a lot of dependencies,
     * we are using a Map, which also removes risks of inconsistencies. It is the managers job to create this obj
     * and not the clients.
     * @return f.eks. { "pollid" : 1234}
     */
    @PostMapping
    public Map<String, Object> createPoll(@RequestBody Map<String, Object> body) {
        try {
            Integer creatorId = (Integer) body.get("creatorId");   // <--- Kan ikke bruke int (primitiv) da objekt krever en Integer (referansetype). Integer tillater også null verdi.
            String question = (String) body.get("question");
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) body.get("options");
            String validUntilStr = (String) body.get("validUntil");
            Instant validUntil = null;
            if(validUntilStr != null && !validUntilStr.isBlank())
                validUntil = Instant.parse(validUntilStr);

            int pollId = manager.createPoll(creatorId, question, options, validUntil);
            return Map.of("pollId", pollId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public Collection<Poll> getPolls(){
        return manager.listPolls();
    }

    @GetMapping("/{id}")
    public Poll getPoll(@PathVariable("id") int pollId) {
        return manager.listPolls().stream().
                filter(p -> p.getId() == pollId).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No poll with id " + pollId));
    }

    @PostMapping("/{pollId}/vote")
    public Map<String, Object> vote(@PathVariable("pollId") int pollId, @RequestBody Map<String, Object> body) {
        try {
            Integer userId = (Integer) body.get("userId");
            Integer optionId = (Integer) body.get("optionId");
            String whenStr = (String) body.get("when");
            Instant when = (whenStr != null && !whenStr.isBlank()) ? Instant.parse(whenStr) : null;

            int voteId = manager.vote(userId, pollId, optionId, when);
            return Map.of("voteId", voteId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.MULTI_STATUS, e.getMessage());
        }
    }

}
