package eu.nets.clearing.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.EventBus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Configuration
public class SpringConfig {

    private Vertx vertx;

    private static Logger log = LoggerFactory.getLogger(SpringConfig.class);

    @Bean
    public EventBus eventBus() {
        if(vertx != null && vertx.eventBus() != null) return vertx.eventBus();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        VertxFactory.newVertx(4545, "localhost", new AsyncResultHandler<Vertx>() {
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
