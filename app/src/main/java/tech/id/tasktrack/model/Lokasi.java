package tech.id.tasktrack.model;

public class Lokasi {
    public int id;
    public String building;
    public String floor;
    public String ssid;
    public String ip_wifi;
    public String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getIp_wifi() {
        return ip_wifi;
    }

    public void setIp_wifi(String ip_wifi) {
        this.ip_wifi = ip_wifi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
