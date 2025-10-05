package com.example.demo.service;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class VoteCacheCount {

    private static final int TTL_sec = 20;
    private final UnifiedJedis jedis;
    private final PollManagerV2 manager;

    public VoteCacheCount(UnifiedJedis jedis, PollManagerV2 manager){
        this.jedis = jedis;
        this.manager = manager;
    }

    private String countsKey(Long pollId) {
        return "poll:" + pollId + ":counts";
    }

    public Map<Integer, Long> getCountsCached(Long pollId) {
        String key = countsKey(pollId);

        if (jedis.exists(key)) {
            Map<String,String> raw = jedis.hgetAll(key);
            // sortér etter order og konverter til ints/longs
            Map<Integer,Long> out = new LinkedHashMap<>();
            raw.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> out.put(Integer.parseInt(e.getKey()), Long.parseLong(e.getValue())));
            System.out.println("[cache] hit " + key);
            return out;
        }

        //cache-miss
        System.out.println("[cache] miss " + key);
        Map<Integer, Long> fromMem = computeFromMem(pollId);

        if(!fromMem.isEmpty()) {
            Map<String, String> toRedis = new LinkedHashMap<>();
            fromMem.forEach((k, v) -> toRedis.put(String.valueOf(k), String.valueOf(v)));
            jedis.hset(key, toRedis);
            jedis.expire(key, TTL_sec);
        }
        return fromMem;
    }

    public void invalidate(Long pollId) {
        jedis.del(countsKey(pollId));
    }


    private Map<Integer, Long> computeFromMem(Long pollId) {
        var poll = manager.listPolls().stream()
                .filter(p -> p.getId().equals(pollId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No poll found with ID: " + pollId));

        Map<Integer, Long> counts = new LinkedHashMap<>();
        poll.getOptions().forEach(opt -> {
                    int order = opt.getPresentationOrder();
                    Long count = (opt.getVotes() == null) ? 0L : opt.getVotes().size();
                    counts.put(order, count);
                });
        return counts;
    }

}
