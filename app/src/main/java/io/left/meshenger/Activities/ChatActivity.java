package io.left.meshenger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
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
public class ChatActivity extends ServiceConnectedActivity {
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
        mUser = getIntent().getParcelableExtra("user");

        mMessageAdapter = new MessageAdapter(this, mMessagelist);
        mMessageAdapter.notifyDataSetChanged();

        mChatrecyclerview = findViewById(R.id.reyclerview_message_list);
        mChatrecyclerview.setNestedScrollingEnabled(false);
        mChatrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mChatrecyclerview.setAdapter(mMessageAdapter);
        mChatrecyclerview.smoothScrollToPosition(0);

        Button sendButton = findViewById(R.id.sendButton);
        EditText messageText = findViewById(R.id.myMessageEditText);
        sendButton.setOnClickListener(view -> {
            if (mService != null) {
                try {
                    mService.sendTextMessage(mUser, messageText.getText().toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    void updateInterface() {

    }
}