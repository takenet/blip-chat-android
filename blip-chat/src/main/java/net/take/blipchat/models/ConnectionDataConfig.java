package net.take.blipchat.models;


public class ConnectionDataConfig implements java.io.Serializable {

    public ConnectionDataConfig(String domain, String hostName, String port){
        this.domain = domain;
        this.hostName = hostName;
        this.port = port;
    }

    public ConnectionDataConfig(){}

    private String domain;
    private String hostName;
    private String port;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
