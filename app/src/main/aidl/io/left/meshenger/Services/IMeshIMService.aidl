package io.left.meshenger.Services;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Models.Message;
import io.left.meshenger.Models.User;

interface IMeshIMService {
    void sendTextMessage(in User recipient, in String message);

    void setForeground(in boolean value);

    void registerMainActivityCallback(in IActivity callback);

    List<User> getOnlineUsers();

    List<Message> getMessagesForUser(in User user);
}