package eu.nets.clearing.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.eventbus.ReplyException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class VertxMessageBus {
    private static Logger log = LoggerFactory.getLogger(VertxMessageBus.class);
    private EventBus eventBus;
    private Integer timeoutInSeconds = 5;

    public VertxMessageBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void requestReply(final String address, final String json) {
        eventBus.send(address, json);
    }

    public String requestReplyBlocking(final String address, final String json) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final StringBuilder reply = new StringBuilder();

        eventBus.sendWithTimeout(address, json, timeoutInSeconds * 1000, new Handler<AsyncResult<Message<String>>>() {
            @Override
            public void handle(AsyncResult<Message<String>> result) {
                if (result.succeeded()) {
                    reply.append(result.result().body());
                    countDownLatch.countDown();
                } else {
                    log.info("Waiting for vertx reply failed on address: '" + address + "' with timeout in seconds: " + timeoutInSeconds);
                    ReplyException ex = (ReplyException) result.cause();
                    log.info("Failure type: " + ex.failureType() + " Failure code:" + ex.failureCode() + " Failure message:" + ex.getMessage());
                    reply.append(VertxJsonStatus.JSON_ERROR);
                    countDownLatch.countDown();
                }
            }
        });

        try {
            if (!countDownLatch.await(timeoutInSeconds, TimeUnit.SECONDS)) {
                log.info("Waiting for vertx reply failed on address: '" + address + "' with timeout in seconds: " + timeoutInSeconds);
                reply.append(VertxJsonStatus.JSON_ERROR);
            }
        } catch (InterruptedException e) {
            log.info("Waiting for vertx reply failed.", e);
            reply.append(VertxJsonStatus.JSON_ERROR);
        }
        return reply.toString();
    }

    public void publish(String address, String json) {
        log.info("Publish json request to " + address + " :" + json);
        eventBus.publish(address, json);
    }

}
