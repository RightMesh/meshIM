package io.left.meshenger.Activities;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import io.left.meshenger.Adapters.MessageAdapter;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;

/**
 * An activity that displays a conversation between two users, and enables sending messages.
 */
public class ChatActivity extends ServiceConnectedActivity {
    private MessageAdapter mMessageAdapter;
    User mRecipient;

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Fetch the recipient from the intent and set up the message adapter.
        mRecipient = getIntent().getParcelableExtra("recipient");
        mMessageAdapter = new MessageAdapter(mRecipient);

        // Initialize the list view for the messages.
        RecyclerView messageListView = findViewById(R.id.reyclerview_message_list);
        messageListView.setNestedScrollingEnabled(false);
        messageListView.setLayoutManager(new LinearLayoutManager(this));
        messageListView.setAdapter(mMessageAdapter);
        messageListView.smoothScrollToPosition(0);

        // Connect the send button to the service.
        Button sendButton = findViewById(R.id.sendButton);
        EditText messageText = findViewById(R.id.myMessageEditText);
        sendButton.setOnClickListener(view -> {
            if (mService != null) {
                try {
                    mService.sendTextMessage(mRecipient, messageText.getText().toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    void updateInterface() {
        runOnUiThread(() -> {
            mMessageAdapter.updateList(mService);
            mMessageAdapter.notifyDataSetChanged();
        });
    }
}