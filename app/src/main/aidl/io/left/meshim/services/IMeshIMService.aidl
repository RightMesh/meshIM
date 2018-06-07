package io.left.meshim.services;

import io.left.meshim.activities.IActivity;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.Message;
import io.left.meshim.models.User;

interface IMeshIMService {
    // Service funcionality.

    void setForeground(in boolean value);


    // RightMesh funcionality.

    void broadcastUpdatedProfile();

    List<User> getOnlineUsers();

    void registerActivityCallback(in IActivity callback);

    void sendTextMessage(in User recipient, in String message, in byte[] file, in String fileExtension);

    void showRightMeshSettings();


    // Database queries.

    List<ConversationSummary> fetchConversationSummaries();

    List<Message> fetchMessagesForUser(in User user);

    User fetchUserById(in int id);
}