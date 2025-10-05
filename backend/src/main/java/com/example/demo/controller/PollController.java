package com.example.demo.controller;

import com.example.demo.domain.Poll;
import com.example.demo.service.PollManagerV2;
import com.example.demo.service.VoteCacheCount;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/polls")
public class PollController {

    private final PollManagerV2 manager;
    private final VoteCacheCount cache;

    public PollController(PollManagerV2 manager, VoteCacheCount cache) {

        this.manager = manager;
        this.cache = cache;
    }

    /**
     * To remove the clients concern of creating a complete Poll object, which contains a lot of dependencies,
     * we are using a Map, which also removes risks of inconsistencies. It is the managers job to create this obj
     * and not the clients.
     * @return f.eks. { "pollid" : 1234}
     */
    @PostMapping
    public Map<String, Object> createPoll(@RequestBody Map<String, Object> body) {
        try {
            Long creatorId = ((Number) body.get("creatorId")).longValue();   // <--- Kan ikke bruke int (primitiv) da objekt krever en Integer (referansetype). Integer tillater også null verdi.
            String question = (String) body.get("question");
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) body.get("options");
            String validUntilStr = (String) body.get("validUntil");
            Instant validUntil = null;
            if(validUntilStr != null && !validUntilStr.isBlank())
                validUntil = Instant.parse(validUntilStr);

            Long pollId = manager.createPoll(creatorId, question, options, validUntil);
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
    public Poll getPoll(@PathVariable("id") Long pollId) {
        return manager.listPolls().stream().
                filter(p -> p.getId().equals(pollId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No poll with id " + pollId));
    }

    @PostMapping("/{pollId}/vote")
    public Map<String, Object> vote(@PathVariable("pollId") Long pollId, @RequestBody Map<String, Object> body) {
        try {
            Long userId = ((Number) body.get("userId")).longValue();
            Long optionId = ((Number) body.get("optionId")).longValue();
            String whenStr = (String) body.get("when");
            Instant when = (whenStr != null && !whenStr.isBlank()) ? Instant.parse(whenStr) : null;

            Long voteId = manager.vote(userId, pollId, optionId, when);
            cache.invalidate(pollId);
            return Map.of("voteId", voteId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}/results")
    public List<Map<String, Object>> results(@PathVariable Long id) {
        Poll p = manager.listPolls().stream()
                .filter(pp -> pp.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No poll with id " + id));

//        var counts = p.getVotes().stream()
//                .collect(java.util.stream.Collectors.groupingBy(
//                        v -> v.getOption().getId(),
//                        java.util.stream.Collectors.counting()
//                ));

        Map<Integer, Long> countsByOrder = cache.getCountsCached(id);

        return p.getOptions().stream()
                .map(opt -> {
                    long c = countsByOrder.getOrDefault(opt.getPresentationOrder(), 0L);
                    Map<String, Object> m = new java.util.HashMap<>();
                    m.put("optionId", opt.getId());
                    m.put("caption", opt.getCaption());
                    m.put("count", c);
                    return m;
                })
                .toList();
    }


}
