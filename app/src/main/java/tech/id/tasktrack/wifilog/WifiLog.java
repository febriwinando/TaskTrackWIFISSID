package tech.id.tasktrack.wifilog;

public class WifiLog {
    public int id;
    public String timestamp;
    public String ssid;
    public String ipAddress;

    public WifiLog(int id, String timestamp, String ssid, String ipAddress) {
        this.id = id;
        this.timestamp = timestamp;
        this.ssid = ssid;
        this.ipAddress = ipAddress;
    }
}
