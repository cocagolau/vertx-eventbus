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
public class PongService implements Handler<Message<JsonObject>> {

    private static Logger log = LoggerFactory.getLogger(PongService.class);

    @Autowired
    EventBus eventBus;

    @PostConstruct
    public void registerVertxHandler() {
        log.info("Registering handler on ping json");
        eventBus.registerHandler("vertx.ping.json", this);
    }

    @Override
    public void handle(Message<JsonObject> message) {
        log.info("Received ping:" + message.body().getString("ping"));
        JsonObject pong = new JsonObject().putString("ping", "pong");
        message.reply(pong);
    }
}
