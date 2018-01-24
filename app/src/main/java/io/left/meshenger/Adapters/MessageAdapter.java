package io.left.meshenger.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.left.meshenger.Models.Message;
import io.left.meshenger.R;


/**
 * Created by Sachin on 2018-01-13.
 */


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context mContext;
    private List<Message> mMessageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }


    //message recived
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName, year, messageBody;

        public MyViewHolder(View view, int messageType) {
            super(view);
            if (messageType == VIEW_TYPE_MESSAGE_RECEIVED) {
                userName = (TextView) view.findViewById(R.id.text_message_name);
                messageBody = (TextView) view.findViewById(R.id.text_message_body);
                year = (TextView) view.findViewById(R.id.text_message_time_recieved);
            } else {
                messageBody = (TextView) view.findViewById(R.id.text_message_body_sent);
                year = (TextView) view.findViewById(R.id.text_message_time_sent);
            }
        }
    }


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

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        if (!(message.isMyMessage())) {
            holder.userName.setText(message.getUserName());
            holder.messageBody.setText(message.getMessage());
            holder.year.setText(message.getDate().toString());
        } else {
            holder.messageBody.setText(message.getMessage());
            holder.year.setText(message.getDate().toString());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

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