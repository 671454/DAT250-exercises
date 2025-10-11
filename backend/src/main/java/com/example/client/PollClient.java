package com.example.client;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class PollClient {
    public static void main(String[] args) throws Exception {
        ConnectionFactory f = new ConnectionFactory();
        f.setHost("localhost");
        f.setPort(5672);
        f.setUsername("guest");
        f.setPassword("guest");

        long pollId = 1L;
        String routingKey = "poll." + pollId + ".*";

        try(Connection conn = f.newConnection(); Channel ch = conn.createChannel()) {

            //Sjekker om det finnes en Exchange med navn "polls"
            ch.exchangeDeclare("polls", BuiltinExchangeType.TOPIC, true);

            //Lager en queue
            String queue = ch.queueDeclare("", false, true, true, null).getQueue();
            ch.queueBind(queue, "polls", routingKey);

            System.out.println("Listening on routing key: " + routingKey);

            // En callback som kjøres hver gang en melding havner i vår private kø
            DeliverCallback dc = (tag, msg) -> {
                String rk = msg.getEnvelope().getRoutingKey();
                String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                System.out.println("[RECV] rk=" + rk + " body=" + body);
            };

            //Konsumer msg
            ch.basicConsume(queue, true, dc, tag -> {});

            String json = """
                {"pollId": %d, "optionId": 1, "userIdOrNull": null, "when": "%s"}
                """.formatted(pollId, Instant.now().toString());

            ch.basicPublish("polls", "poll." + pollId + ".vote", null, json.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent anonymous vote" );

            System.in.read();
        }

    }
}
