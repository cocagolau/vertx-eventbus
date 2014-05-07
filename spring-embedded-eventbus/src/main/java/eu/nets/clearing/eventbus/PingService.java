package eu.nets.clearing.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    //    @Scheduled(fixedDelay = 5000)
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
                }
            }
        });

        log.info("Done!");
    }

    //    @Scheduled(fixedDelay = 5000)
    public void pingJsonWithTimeoutAndBlocking() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JsonObject jsonPing = new JsonObject().putString("ping", "ping");
        String reply = "";

        eventBus.sendWithTimeout("vertx.ping.json", jsonPing, 1000, new Handler<AsyncResult<Message<JsonObject>>>() {
            @Override
            public void handle(AsyncResult<Message<JsonObject>> message) {
                if (message.succeeded()) {
                    log.info("Got response:" + message.result().body().getString("ping"));
                    countDownLatch.countDown();
                } else {
                    log.error("Timeout on eventbus!");
                    countDownLatch.countDown();
                }
            }
        });

        try {
            if (!countDownLatch.await(1, TimeUnit.SECONDS)) {
                reply = VertxJsonStatus.JSON_ERROR;
            }
        } catch (InterruptedException e) {
            log.info("Waiting for vertx reply failed.", e);
            reply = VertxJsonStatus.JSON_ERROR;
        }

        log.info("Final result:" + reply);
    }

}
