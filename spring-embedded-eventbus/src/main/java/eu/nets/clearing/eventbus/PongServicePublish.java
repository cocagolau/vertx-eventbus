package eu.nets.clearing.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import javax.annotation.PostConstruct;

@Component
public class PongServicePublish implements Handler<Message<JsonObject>>{

    private static Logger log = LoggerFactory.getLogger(PongServicePublish.class);

    @Autowired
    EventBus eventBus;

    @PostConstruct
    public void registerVertxAddress() {
        eventBus.registerHandler("vertx.ping.publish", this);
    }

    @Override
    public void handle(Message<JsonObject> message) {
        log.info("Got publish:" + message.body().getString("ping"));
    }
}
