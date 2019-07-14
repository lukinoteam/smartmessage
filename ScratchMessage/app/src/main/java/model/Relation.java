package model;

public class Relation {

    private String avaUri;
    private String nickName;
    private String userName;
    private int status;


    public Relation() {
    }

    public Relation(String avaUri, String userName, String nickName, int status) {
        this.avaUri = avaUri;
        this.userName = userName;
        this.nickName = nickName;
        this.status = status;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getAvaUri() {
        return avaUri;
    }

    public void setAvaUri(String avaUri) {
        this.avaUri = avaUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
