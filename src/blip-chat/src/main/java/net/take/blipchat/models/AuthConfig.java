package net.take.blipchat.models;


public class AuthConfig implements java.io.Serializable {

    public AuthConfig(String authType, String userIdentity, String userPassword) {
        this.authType = authType;
        this.userIdentity = userIdentity;
        this.userPassword = userPassword;
    }

    public AuthConfig(String authType) {
        this.authType = authType;
    }

    public AuthConfig() {
    }

    private String authType;
    private String userIdentity;
    private String userPassword;

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
