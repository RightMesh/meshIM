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
     * Constructor
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

    /**
     * set whether its your message or others
     * @param myMessage boolean
     */
    public void setMyMessage(boolean myMessage) {
        this.isMyMessage = myMessage;
    }

    /**
     * checks whether its your message or other users.
     * @return a boolean.
     */
    public boolean isMyMessage() {
        return isMyMessage;
    }

    /**
     * get the User data of the user who sent the message.
     * @return a object of type User.
     */
    public User getUser() {
        return user;
    }

    /**
     * returns the date of the message.
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * returns the message.
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * sets user who sent the message.
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * sets the date of the message
     * @param date
     */
    public void setDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a dd-MMM-yyyy");
        this.date = dateFormat.format(date).toString();
    }

    /**
     * sets the message
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}

