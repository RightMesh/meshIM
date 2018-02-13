package io.left.meshim.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import io.left.meshim.R;
import io.left.meshim.adapters.MessageListAdapter;
import io.left.meshim.models.User;

/**
 * An activity that displays a conversation between two users, and enables sending messages.
 */
public class ChatActivity extends ServiceConnectedActivity {
    private RecyclerView mMessageListView;
    private MessageListAdapter mMessageListAdapter;
    User mRecipient;

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Null-check recipient.
        mRecipient = getIntent().getParcelableExtra("recipient");
        if (mRecipient == null) {
            finish();
        }

        // Set up the message adapter.
        mMessageListAdapter = new MessageListAdapter(mRecipient);
        try {
            mMessageListAdapter.updateList(this.mService);
        } catch (DeadObjectException e) {
            reconnectToService();
        }

        // Initialize the list view for the messages.
        mMessageListView = findViewById(R.id.reyclerview_message_list);
        mMessageListView.setNestedScrollingEnabled(false);
        mMessageListView.setLayoutManager(new LinearLayoutManager(this));
        mMessageListView.setAdapter(mMessageListAdapter);

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
                } catch (RemoteException re) {
                    if (re instanceof DeadObjectException) {
                        reconnectToService();
                    }
                    // Otherwise ignore - we don't clear the message out, so the user can attempt
                    // to re-send it if they would like.
                }
            }
        });
        setupActionBar();

    }

    /**
     * Sets up the action bar to display user name and back button.
     */
    private void setupActionBar() {
        if (mRecipient != null && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
            String title = mRecipient.getUsername();
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(s);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    void updateInterface() {
        runOnUiThread(() -> {
            try {
                mMessageListAdapter.updateList(mService);
                mMessageListAdapter.notifyDataSetChanged();
                int index = mMessageListAdapter.getItemCount() - 1;
                if (index > -1) {
                    mMessageListView.smoothScrollToPosition(index);
                }
            } catch (DeadObjectException e) {
                reconnectToService();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}