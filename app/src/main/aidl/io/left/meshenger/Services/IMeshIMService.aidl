package io.left.meshenger.Services;

import io.left.meshenger.Activities.IActivity;
import io.left.meshenger.Models.User;

interface IMeshIMService {
    void send(in String message);
    void setForeground(in boolean value);
    void registerMainActivityCallback(in IActivity callback);

    List<User> getOnlineUsers();
}