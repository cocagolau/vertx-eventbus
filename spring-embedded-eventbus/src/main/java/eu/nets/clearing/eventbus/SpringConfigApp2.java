package eu.nets.clearing.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.EventBus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
public class SpringConfigApp2 {

    private static Logger log = LoggerFactory.getLogger(SpringConfigApp2.class);

    private Vertx vertx;
    private int vertxPort = RandomInt.randInt(2000, 6000);

    @Bean
    public PongService pongService() {
        return new PongService();
    }

    @Bean PongServicePublish pongServicePublish() {
        return new PongServicePublish();
    }

    @Bean
    public EventBus eventBus() {
        if(vertx != null && vertx.eventBus() != null) return vertx.eventBus();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        log.info("Starting Vertx Eventbus on: localhost:" + vertxPort);
        VertxFactory.newVertx(vertxPort, "localhost", new AsyncResultHandler<Vertx>() {
            @Override
            public void handle(AsyncResult<Vertx> event) {
                vertx = event.result();
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Failed to start vertx event bus.", e);
        }
        return vertx.eventBus();
    }

    @Bean
    public VertxMessageBus vertxMessageBus() {
        return new VertxMessageBus(eventBus());
    }
}
