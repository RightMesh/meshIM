package io.left.meshenger.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.left.meshenger.Models.Message;
import io.left.meshenger.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context mContext;
    private List<Message> mMessageList;

    /**
     * Constructor for the adapter
     * @param context context of the activity
     * @param messageList list of the messages
     */
    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }


    /**
     * Checks whether the message should be displayed in send or recieve layout.
     * Finds all the required layout fields.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView  time, messageBody;
        public ImageView userImage;

        public MyViewHolder(View view, int messageType) {
            super(view);
            if (messageType == VIEW_TYPE_MESSAGE_RECEIVED) {
                userImage = (ImageView) view.findViewById(R.id.image_message_profile);
                messageBody = (TextView) view.findViewById(R.id.text_message_body);
                time = (TextView) view.findViewById(R.id.text_message_time_recieved);
            } else {
                messageBody = (TextView) view.findViewById(R.id.text_message_body_sent);
                time = (TextView) view.findViewById(R.id.text_message_time_sent);
            }
        }
    }

    /**
     * sets up the required layout for the message
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_recieve, parent, false);

            return new MyViewHolder(itemView, VIEW_TYPE_MESSAGE_RECEIVED);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_send, parent, false);
            return new MyViewHolder(itemView, VIEW_TYPE_MESSAGE_SENT);

        }
    }

    /**
     * setup the layout field with user data
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        if (!(message.isMyMessage())) {
            holder.messageBody.setText(message.getMessage());
            holder.time.setText(message.getDate().toString());
            holder.userImage.setImageResource(message.getUser().getUserAvatar());
        } else {
            holder.messageBody.setText(message.getMessage());
            holder.time.setText(message.getDate().toString());
        }
    }

    /**
     * returns the size of the message
     * @return
     */
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    /**
     * Checks whether the message was sent or received.
     * @param position
     * @return
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