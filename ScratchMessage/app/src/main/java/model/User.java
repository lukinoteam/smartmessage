package model;

public class User {
    private String id;
    private String userName;
    private String avaUri;
    private String email;

    public User() {

    }

    public User(String id, String userName, String email, String avaUri) {
        this.id = id;
        this.userName = userName;
        this.avaUri = avaUri;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvaUri() {
        return avaUri;
    }

    public void setAvaUri(String avaUri) {
        this.avaUri = avaUri;
    }
}
