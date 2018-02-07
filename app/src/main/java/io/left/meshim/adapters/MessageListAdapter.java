package io.left.meshim.adapters;

import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.left.meshim.R;
import io.left.meshim.models.Message;
import io.left.meshim.models.User;
import io.left.meshim.services.IMeshIMService;
import io.left.meshim.services.MeshIMService;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that fetches a conversation between the devices's user and a recipient, rendering
 * messages sent by this device's user in a `list_item_message_sent` view and received messages in a
 * `list_item_message_received` view.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    // List of both sent and received messages.
    private List<Message> mMessageList = new ArrayList<>();

    // User these messages have been exchanged with.
    private User mRecipient;

    public MessageListAdapter(User recipient) {
        this.mRecipient = recipient;
    }

    /**
     * Update the list of messages from the service.
     * @param service open connection to {@link MeshIMService}
     */
    public void updateList(IMeshIMService service) {
        if (service == null) {
            return;
        }
        try {
            this.mMessageList.clear();
            List<Message> results = service.getMessagesForUser(this.mRecipient);
            if (results != null) {
                this.mMessageList.addAll(results);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mTime;
        TextView mMessagaeBody;
        ImageView mUserImage;

        /**
         * Checks whether the message should be displayed in send or received layout.
         * Finds all the required layout fields.
         * @param view view to work with
         * @param messageType type of message, either {@link MessageListAdapter#VIEW_TYPE_MESSAGE_SENT}
         *                    or {@link MessageListAdapter#VIEW_TYPE_MESSAGE_RECEIVED}
         */
        MessageViewHolder(View view, int messageType) {
            super(view);
            if (messageType == VIEW_TYPE_MESSAGE_RECEIVED) {
                mUserImage = view.findViewById(R.id.image_message_profile);
                mMessagaeBody = view.findViewById(R.id.text_message_body);
                mTime = view.findViewById(R.id.text_message_time_recieved);
            } else {
                mMessagaeBody = view.findViewById(R.id.text_message_body_sent);
                mTime = view.findViewById(R.id.text_message_time_sent);
            }
        }
    }

    /**
     * Sets up the required layout for the message.
     * @param parent parent group of view
     * @param viewType type of view, either {@link MessageListAdapter#VIEW_TYPE_MESSAGE_RECEIVED}
     *                 or {@link MessageListAdapter#VIEW_TYPE_MESSAGE_SENT}
     * @return view holder instance
     */
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_received, parent, false);
            return new MessageViewHolder(itemView, VIEW_TYPE_MESSAGE_RECEIVED);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_message_sent, parent, false);
            return new MessageViewHolder(itemView, VIEW_TYPE_MESSAGE_SENT);
        }
    }

    /**
     * Setup the layout field with user data.
     * @param holder holder for view to be initialised
     * @param position position of message in list to populate view with
     */
    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        if (!(message.isMyMessage())) {
            holder.mMessagaeBody.setText(message.getMessage());
            holder.mTime.setText(message.getDateAsString());
            holder.mUserImage.setImageResource(message.getSender().getAvatar());
        } else {
            holder.mMessagaeBody.setText(message.getMessage());
            holder.mTime.setText(message.getDateAsString());
        }
    }

    /**
     * Returns the size of the message list.
     * @return size of message list
     */
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    /**
     * Checks whether the message was sent or received.
     * @param position index of message in list
     * @return message type, either {@link MessageListAdapter#VIEW_TYPE_MESSAGE_RECEIVED}
     *         or {@link MessageListAdapter#VIEW_TYPE_MESSAGE_SENT}
     */
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        if (message.isMyMessage()) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
}