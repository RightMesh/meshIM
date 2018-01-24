package io.left.meshenger.Models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sachin on 2018-01-13.
 */

public class Message {
    private String message;
    private String date;
    private String userName;
    private boolean isMyMessage;

    public Message() {
        message = "Hello";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a yyyy-M-dd");
        this.date = dateFormat.format(date).toString();
        userName = "myUserName";
        isMyMessage = true;
    }

    public Message(String message, String userName, boolean isMyMessage) {
        this.message = message;
        Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd-MMM-yyyy");
        this.date = dateFormat.format(date).toString();
        this.userName = userName;
        this.isMyMessage = isMyMessage;
    }

    public void setMyMessage(boolean myMessage) {
        this.isMyMessage = myMessage;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

