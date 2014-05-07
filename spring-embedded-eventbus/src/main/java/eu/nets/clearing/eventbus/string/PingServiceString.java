package eu.nets.clearing.eventbus.string;

import com.google.gson.Gson;
import eu.nets.clearing.eventbus.JsonPing;
import eu.nets.clearing.eventbus.VertxMessageBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

@Service
public class PingServiceString {
    private static Logger log = LoggerFactory.getLogger(PingServiceString.class);

    @Autowired
    EventBus eventBus;

    @Autowired
    VertxMessageBus vertxMessageBus;

//    @Scheduled(fixedDelay = 5000)
    public void ping() {
        log.info("Sending ping...");
        JsonPing ping = new JsonPing("ping");
        String payload = new Gson().toJson(ping);

        eventBus.sendWithTimeout("vertx.ping", payload, 1000, new Handler<AsyncResult<Message<String>>>() {
            @Override
            public void handle(AsyncResult<Message<String>> message) {
                if (message.succeeded()) {
                    String response = message.result().body();
                    JsonPing pong = new Gson().fromJson(response, JsonPing.class);
                    log.info("We got response:" + pong);
                }
            }
        });

        log.info("Done sending...");
    }

//    @Scheduled(fixedDelay = 5000)
    public void blockingPing() {
        log.info("Sending ping...");
        JsonPing ping = new JsonPing("ping");
        String payload = new Gson().toJson(ping);
        String response = vertxMessageBus.requestReplyBlocking("vertx.ping", payload);
        JsonPing pong = new Gson().fromJson(response, JsonPing.class);
        log.info("We got response:" + pong);


    }





    @Scheduled(fixedDelay = 5000)
    public void pingJson() {
        log.info("Sending ping...");
        JsonObject jsonPing = new JsonObject().putString("ping", "ping");
        eventBus.send("vertx.ping.json", jsonPing);
    }

}
