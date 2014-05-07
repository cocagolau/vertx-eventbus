package eu.nets.clearing.eventbus;

public class JsonPing {
    public String ping;

    public JsonPing(String ping) {
        this.ping = ping;
    }

    @Override
    public String toString() {
        return ping;
    }
}
