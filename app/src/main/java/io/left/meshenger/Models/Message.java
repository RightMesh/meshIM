package io.left.meshenger.Models;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Message {
    private String message;
    private String date;
    private User user;
    private boolean isMyMessage;

    /**
     * rDefault constructor.
     */
    public Message() {
        message = "Hello";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a yyyy-M-dd");
        this.date = dateFormat.format(date).toString();
        user = new User();
        isMyMessage = true;
    }

    /**
     * Constructor.
     * @param message message user want to send
     * @param user User data of the user
     * @param isMyMessage boolean
     */
    public Message(String message, User user, boolean isMyMessage) {
        this.message = message;

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd-MMM-yyyy");
        this.date = dateFormat.format(date).toString();

        this.user = user;
        this.isMyMessage = isMyMessage;
    }

    public void setMyMessage(boolean myMessage) {
        this.isMyMessage = myMessage;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd-MMM-yyyy");
        this.date = dateFormat.format(date).toString();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

