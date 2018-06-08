package io.left.meshim.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import io.left.meshim.R;
import io.left.meshim.adapters.MessageListAdapter;
import io.left.meshim.controllers.RightMeshController;
import io.left.meshim.models.User;

/**
 * An activity that displays a conversation between two users, and enables sending messages.
 */
public class ChatActivity extends ServiceConnectedActivity {
    private RecyclerView mMessageListView;
    private MessageListAdapter mMessageListAdapter;
    User mRecipient;
    ImageButton pickfiles;
    byte[] fileBytes = null;
    String fileExtention ="";
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
        ImageButton sendButton = findViewById(R.id.sendButton);
        EditText messageText = findViewById(R.id.myMessageEditText);
        sendButton.setOnClickListener(view -> {
            if (mService != null) {
                try {
                    String message = messageText.getText().toString();
                    if (!message.equals("") || fileBytes!=null) {
                        mService.sendTextMessage(mRecipient,message,fileBytes,fileExtention);
                        messageText.setText("");
                        fileBytes = null;
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
        pickfiles = findViewById(R.id.fileButton);
        pickfiles.setOnClickListener( view ->{
            Intent intent4 = new Intent(this, NormalFilePickActivity.class);
            intent4.putExtra(Constant.MAX_NUMBER, 1);
            intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
            startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
        });
        setupActionBar();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    NormalFile file = list.get(0);
                    Log.d("bugg",file.getMimeType());
                    Log.d("bugg",file.toString());
                    Log.d("bugg",file.getPath());
                    String extension = "";

                    int i = file.getPath().lastIndexOf('.');
                    int p = Math.max(file.getPath().lastIndexOf('/'), file.getPath().lastIndexOf('\\'));
                    if (i > p) {
                        fileExtention = file.getPath().substring(i+1);
                    }
                    Log.d("bugg",extension);
                    File file1 = new File(file.getPath());
                    try {
                        fileBytes = RightMeshController.getBytesFromFile(file1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
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