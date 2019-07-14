package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

public class Message implements Parcelable, Comparable<Message> {
    private String id;
    private String content;
    private long time;
    private int status;
    private String friend;
    private int type;

    public Message() {
    }

    public Message(String id, String content, long time, int status, String friend, int type) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.status = status;
        this.friend = friend;
        this.type = type;
    }

    public Message(String content, int status, String friend, int type) {
        Date now = new Date();
        long currentTime = now.getTime();

        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.time = currentTime;
        this.status = status;
        this.friend = friend;
        this.type = type;
    }

    private Message(Parcel in) {
        id = in.readString();
        content = in.readString();
        time = in.readLong();
        status = in.readInt();
        friend = in.readString();
        type = in.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString(){
        return "ID: " + this.getId() + " | Content: " + content + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(content);
        parcel.writeLong(time);
        parcel.writeInt(status);
        parcel.writeString(friend);
        parcel.writeInt(type);
    }


    @Override
    public int compareTo(Message message) {
        if (this.getTime() > message.getTime())
            return 1;
        else if (this.getTime() < message.getTime())
            return -1;
        else
            return 0;
    }
}
