package com.example.demo;

import com.example.demo.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // fresh state per test

/*
* Siden payload inneholder fulle objekter og ID-referanser til relasjoner, må domeneklassene markeres
*  med @JsonIdentityReference(alwaysAsId = true). Når denne er satt til true forteller vi Jackson at
*  at man bare skal bruke ID-en i stedet for å serialisere hele objektet. Dette fikser tidligere feil med
*  "Already had POJO for id..." fordi Jackson slipper å bygge samme objekt to ganger.
*/

public class E2ETest {

    @LocalServerPort int port;
    RestClient http(){
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    private int createUser(String username, String email) {
        User user = http().post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "username", username,
                        "email", email
                ))
                .retrieve().body(User.class);
        assertThat(user).isNotNull();
        return user.getId();
    }
    private int createPoll(int creatorId, String question, List<String> options, String validUntil){
        Map<?, ?> pollResp = http().post().uri("/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "creatorId", creatorId,
                        "question", question,
                        "options", options,
                        "validUntil", validUntil
                ))
                .retrieve().body(Map.class);
        assertThat(pollResp).isNotNull();
        return ((Number) pollResp.get("pollId")).intValue();
    }

    private Map<?, ?> createVote(int pollId, int userId, int optionId, Instant when){
        return http().post().uri("/polls/"+pollId+"/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "userId", userId,
                        "optionId", optionId,
                        "when", when != null ? when.toString() : null
                ))
                .retrieve().body(Map.class);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPoll(int pollId) {
        return http().get().uri("/polls/"+pollId).retrieve().body(Map.class);
    }

    @Test //An end-to-end test. create user -> create poll -> vote on poll
    void E2EPath() {
        String question = "Har du stått opp på rett ben i dag?";

        int userId = createUser("Ola Nordman", "ola@gmail.com");
        int pollId = createPoll(userId, question, List.of("Ja", "Nei"), "2025-12-31T23:59:59Z");

        Map<?, ?> voteResp = createVote(pollId, userId, 1, Instant.now());
        assertThat(voteResp.get("voteId")).isNotNull();

        var poll = getPoll(pollId);
        assertThat(poll).isNotNull();
        assertThat(poll.get("id")).isEqualTo(pollId);
        assertThat((List<?>) poll.get("voteOptions")).hasSize(2);
        assertThat((List<?>) poll.get("votes")).isNotEmpty();
    }

}
