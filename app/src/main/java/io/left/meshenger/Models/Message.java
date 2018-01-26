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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = dateFormat.format(date);
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public boolean isMyMessage() {
        return isMyMessage;
    }

    public void setIsMyMessage(boolean isMyMessage) {
        this.isMyMessage = isMyMessage;
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
}