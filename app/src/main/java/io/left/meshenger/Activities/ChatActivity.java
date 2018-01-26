package io.left.meshenger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import io.left.meshenger.Adapters.MessageAdapter;
import io.left.meshenger.Models.Message;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity that displays a conversation between two users, and enables sending messages.
 */
public class ChatActivity extends Activity {
    private RecyclerView mChatrecyclerview;
    private MessageAdapter mMessageAdapter;
    List<Message> mMessagelist = new ArrayList<>();
    User mUser;

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mUser = new User("Bruce Lee",R.mipmap.avatar1);
        mChatrecyclerview = findViewById(R.id.reyclerview_message_list);
        mChatrecyclerview.setNestedScrollingEnabled(false);

        mMessageAdapter = new MessageAdapter(this, mMessagelist);
        mChatrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mChatrecyclerview.setAdapter(mMessageAdapter);
        mChatrecyclerview.smoothScrollToPosition(mChatrecyclerview.getAdapter().getItemCount() - 1);
        mMessageAdapter.notifyDataSetChanged();

        Button sendButton = findViewById(R.id.sendButton);
        EditText messageText = findViewById(R.id.myMessageEditText);
        sendButton.setOnClickListener(view -> {
            messageText.getText().toString();
        });
    }
}