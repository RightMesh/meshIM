package io.left.meshenger.Models;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Message {
    private String message;
    private String date;
    private User sender;
    private User recipient;
    private boolean isMyMessage;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a yyyy-M-dd");

    /**
     * rDefault constructor.
     */
    public Message() {
        message = "Hello";
        Date date = new Date();
        this.date = dateFormat.format(date);
        sender = new User();
        isMyMessage = true;
    }

    /**
     * Constructor.
     * @param message message sender want to send
     * @param user User data of the sender
     * @param isMyMessage boolean
     */
    public Message(String message, User user, boolean isMyMessage) {
        this.message = message;

        Date date = new Date();
        this.date = dateFormat.format(date);

        this.sender = user;
        this.isMyMessage = isMyMessage;
    }

    public void setMyMessage(boolean myMessage) {
        this.isMyMessage = myMessage;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public void setDate(Date date) {
        this.date = dateFormat.format(date);
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

