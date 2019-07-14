package model;

import java.util.Date;
import java.util.UUID;

public class Contact {
    private String avaUri;
    private String id;
    private String userName;
    private String nickName;
    private boolean online;

    public Contact() {
    }

    public Contact(String avaUri, String userName, String nickName, boolean online) {
        this.id = UUID.randomUUID().toString();
        this.avaUri = avaUri;
        this.userName = userName;
        this.nickName = nickName;
        this.online = online;
    }

    public Contact(String id, String avaUri, String userName, String nickName, boolean online) {
        this.id = id;
        this.avaUri = avaUri;
        this.userName = userName;
        this.nickName = nickName;
        this.online = online;

    }

    public String getAvaUri() {
        return avaUri;
    }

    public void setAvaUri(String avaUri) {
        this.avaUri = avaUri;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
