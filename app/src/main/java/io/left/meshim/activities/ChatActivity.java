package io.left.meshim.activities;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import io.left.meshim.R;
import io.left.meshim.adapters.MessageAdapter;
import io.left.meshim.models.User;

/**
 * An activity that displays a conversation between two users, and enables sending messages.
 */
public class ChatActivity extends ServiceConnectedActivity {
    private RecyclerView mMessageListView;
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
        mMessageListView = findViewById(R.id.reyclerview_message_list);
        mMessageListView.setNestedScrollingEnabled(false);
        mMessageListView.setLayoutManager(new LinearLayoutManager(this));
        mMessageListView.setAdapter(mMessageAdapter);

        // Connect the send button to the service.
        Button sendButton = findViewById(R.id.sendButton);
        EditText messageText = findViewById(R.id.myMessageEditText);
        sendButton.setOnClickListener(view -> {
            if (mService != null) {
                try {
                    String message = messageText.getText().toString();
                    if (!message.equals("")) {
                        mService.sendTextMessage(mRecipient, message);
                        messageText.setText("");
                    }
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
            int index = mMessageAdapter.getItemCount() - 1;
            if (index > -1) {
                mMessageListView.smoothScrollToPosition(index);
            }
        });
    }
}