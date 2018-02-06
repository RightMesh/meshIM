package io.left.meshim.services;

import io.left.meshim.activities.IActivity;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.Message;
import io.left.meshim.models.User;

interface IMeshIMService {
    User fetchUserById(in int id);

    void sendTextMessage(in User recipient, in String message);

    void setForeground(in boolean value);

    void registerMainActivityCallback(in IActivity callback);

    List<User> getOnlineUsers();

    List<Message> getMessagesForUser(in User user);

    List<ConversationSummary> getConversationSummaries();

    void showRightMeshSettings();
}