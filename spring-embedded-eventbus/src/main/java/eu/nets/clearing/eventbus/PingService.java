package eu.nets.clearing.eventbus;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

@Service
public class PingService {


    @Autowired
    EventBus eventBus;

    public void ping() {

        JsonPing ping = new JsonPing("ping");
        String payload = new Gson().toJson(ping);

        eventBus.sendWithTimeout("vertx.ping", payload, 1000,  new Handler<AsyncResult<Message<String>>>() {
            @Override
            public void handle(AsyncResult<Message<String>> event) {
                if (event.succeeded()) {
                    String response = event.result().body();
                    System.out.println("We got response:" + response);
                }
            }
        });
    }


    private class JsonPing {
        public String ping;

        public JsonPing(String ping) {
            this.ping = ping;
        }
    }

}
