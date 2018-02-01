package io.left.meshim.services;

import io.left.meshim.activities.IActivity;
import io.left.meshim.models.Message;
import io.left.meshim.models.User;

interface IMeshIMService {
    void sendTextMessage(in User recipient, in String message);

    void setForeground(in boolean value);

    void registerMainActivityCallback(in IActivity callback);

    List<User> getOnlineUsers();

    List<Message> getMessagesForUser(in User user);

    void showRightMeshSettings();
}