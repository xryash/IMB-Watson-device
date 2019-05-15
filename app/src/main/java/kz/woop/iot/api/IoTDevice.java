package kz.woop.iot.api;

public class IoTDevice {

    private String organizationId;
    private String deviceType;
    private String deviceId;
    private String authenticationMethod;
    private String token;

    public IoTDevice(String organizationId, String deviceType, String deviceId, String authenticationMethod, String token) {
        this.organizationId = organizationId;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.authenticationMethod = authenticationMethod;
        this.token = token;
    }

    public IoTDevice() {
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "IoTDevice{" +
                "organizationId='" + organizationId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", authenticationMethod='" + authenticationMethod + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
