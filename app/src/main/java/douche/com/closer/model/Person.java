package douche.com.closer.model;

import java.util.List;

/**
 * Created by Johnny on 25/06/2017.
 */

class Person {
    String name;
    Integer role;
    List<Device> devices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
