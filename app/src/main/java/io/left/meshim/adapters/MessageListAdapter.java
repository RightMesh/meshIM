package io.left.meshim.adapters;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
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
     * @throws DeadObjectException if service connection dies unexpectedly
     */
    public void updateList(IMeshIMService service) throws DeadObjectException {
        if (service == null) {
            return;
        }

        try {
            List<Message> query = service.fetchMessagesForUser(this.mRecipient);
            this.mMessageList.clear();
            this.mMessageList.addAll(query);
        } catch (RemoteException e) {
            // If the connection has died, propagate error, otherwise ignore it.
            if (e instanceof DeadObjectException) {
                throw (DeadObjectException) e;
            }
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mTime;
        TextView mMessageBody;
        ImageView mUserImage;
        TextView mDeliveryStatus;

        /**
         * Checks whether the message should be displayed in send or received layout.
         * Finds all the required layout fields.
         * @param view view to work with
         * @param messageType type of message, either
         *                    {@link MessageListAdapter#VIEW_TYPE_MESSAGE_SENT} or
         *                    {@link MessageListAdapter#VIEW_TYPE_MESSAGE_RECEIVED}
         */
        MessageViewHolder(View view, int messageType) {
            super(view);
            if (messageType == VIEW_TYPE_MESSAGE_RECEIVED) {
                mUserImage = view.findViewById(R.id.image_message_profile);
                mMessageBody = view.findViewById(R.id.text_message_body);
                mTime = view.findViewById(R.id.text_message_time_recieved);
            } else {
                mMessageBody = view.findViewById(R.id.text_message_body_sent);
                mTime = view.findViewById(R.id.text_message_time_sent);
                mDeliveryStatus = view.findViewById(R.id.text_message_delivery_text);
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
        if (message != null) {
            if (!(message.isMyMessage())) {
                holder.mMessageBody.setText(message.getMessage());
                holder.mTime.setText(Message.formateDate(message.getDate()));

                User sender = message.getSender();
                if (sender != null) {
                    holder.mUserImage.setImageResource(sender.getAvatar());
                }
            } else {
                holder.mMessageBody.setText(message.getMessage());
                holder.mTime.setText(Message.formateDate(message.getDate()));
                String x = "&#x2713";
                if(!message.isDelivered()) {
                    Log.d("good"," not delivered" +" actual variable: "+message.isDelivered());

                    holder.mDeliveryStatus.setText(Html.fromHtml(x)+ "b");
                } else {
                    holder.mDeliveryStatus.setText(Html.fromHtml(x)+"x"+Html.fromHtml(x));
                }
            }
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
     *         or {@link MessageListAdapter#VIEW_TYPE_MESSAGE_SENT}, or -1 for an invalid position
     */
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        if (message != null) {
            if (message.isMyMessage()) {
                return VIEW_TYPE_MESSAGE_SENT;
            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }
        return -1;
    }
}