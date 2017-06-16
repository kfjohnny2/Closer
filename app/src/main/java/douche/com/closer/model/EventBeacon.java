package douche.com.closer.model;

/**
 * Created by Johnny on 16/06/2017.
 */

public class EventBeacon {
    private String eventName;
    private String address;

    public EventBeacon(String eventName, String address) {
        this.eventName = eventName;
        this.address = address;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
