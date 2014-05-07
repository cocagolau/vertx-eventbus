package eu.nets.clearing.eventbus;

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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class PingService {
    private static Logger log = LoggerFactory.getLogger(PingService.class);

    @Autowired
    EventBus eventBus;

    @Scheduled(fixedDelay = 5000)
    public void pingJson() {
        log.info("Sending ping...");
        JsonObject jsonPing = new JsonObject().putString("ping", "ping");
        eventBus.send("vertx.ping.json", jsonPing);
        log.info("Done!");

        // no handling of response
    }

    //    @Scheduled(fixedDelay = 5000)
    public void pingJsonWithReply() {
        log.info("Sending ping...");
        JsonObject jsonPing = new JsonObject().putString("ping", "ping");
        eventBus.send("vertx.ping.json", jsonPing, new Handler<Message<JsonObject>>() {
            @Override
            public void handle(Message<JsonObject> message) {
                log.info("Got response:" + message.body().getString("ping"));
            }
        });

        log.info("Done !");
    }

    //    @Scheduled(fixedDelay = 5000)
    public void pingJsonPublish() {
        log.info("Sending ping...");
        JsonObject jsonPing = new JsonObject().putString("ping", "ping");
        eventBus.publish("vertx.ping.publish", jsonPing);
        log.info("Done!");
    }

    //    @Scheduled(fixedDelay = 5000)
    public void pingJsonWithTimeout() {
        JsonObject jsonPing = new JsonObject().putString("ping", "ping");
        eventBus.sendWithTimeout("vertx.ping.json", jsonPing, 1000, new Handler<AsyncResult<Message<JsonObject>>>() {
            @Override
            public void handle(AsyncResult<Message<JsonObject>> message) {
                if (message.succeeded()) {
                    log.info("Got response:" + message.result().body().getString("ping"));
                } else {
                    log.info("Timeout on vertx bus");
                }
            }
        });

        log.info("Done!");
    }

//    @Scheduled(fixedDelay = 5000)
    public void pingJsonWithTimeoutAndBlocking() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JsonObject jsonPing = new JsonObject().putString("ping", "ping");
        final JsonObject response = new JsonObject();
        log.info("Sending:" + jsonPing);

        eventBus.sendWithTimeout("vertx.ping.json", jsonPing, 4000, new Handler<AsyncResult<Message<JsonObject>>>() {
            @Override
            public void handle(AsyncResult<Message<JsonObject>> message) {
                if (message.succeeded()) {
                    response.mergeIn(message.result().body());
                    countDownLatch.countDown();
                }
            }
        });

        try {
            log.info("Waiting for response...");
            if (!countDownLatch.await(4, TimeUnit.SECONDS)) {
                response.mergeIn(new JsonObject().putString("status", "error"));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("Final result:" + response);
    }

}
