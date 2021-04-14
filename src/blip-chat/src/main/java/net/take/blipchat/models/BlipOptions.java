package net.take.blipchat.models;

import java.io.Serializable;

public class BlipOptions implements Serializable {

    private AuthConfig authConfig;
    private ConnectionDataConfig connectionDataConfig;
    private Account account;
    private String customCommonUrl;
    private String customWidgetUrl;
    private boolean hideMenu;

    public BlipOptions() {
    }

    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Deprecated
    public boolean isHideMenu() {
        return hideMenu;
    }

    @Deprecated
    public void setHideMenu(boolean hideMenu) {
    }

    public ConnectionDataConfig getConnectionDataConfig() {
        return connectionDataConfig;
    }

    public void setConnectionDataConfig(ConnectionDataConfig connectionDataConfig) {
        this.connectionDataConfig = connectionDataConfig;
    }

    public String getCustomCommonUrl() {
        return customCommonUrl;
    }

    public void setCustomCommonUrl(String customCommonUrl) {
        this.customCommonUrl = customCommonUrl;
    }

    public String getCustomWidgetUrl() {
        return customWidgetUrl;
    }

    public void setCustomWidgetUrl(String customWidgetUrl) {
        this.customWidgetUrl = customWidgetUrl;
    }
}

