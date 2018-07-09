package io.left.meshim.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import java.util.ArrayList;

import io.left.meshim.R;
import io.left.meshim.adapters.MessageListAdapter;
import io.left.meshim.models.User;

import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
import static com.vincent.filepicker.activity.VideoPickActivity.IS_NEED_CAMERA;
import static io.left.meshim.controllers.RightMeshController.getFileExtension;

/**
 * An activity that displays a conversation between two users, and enables sending messages.
 */
public class ChatActivity extends ServiceConnectedActivity {
    private RecyclerView mMessageListView;
    private MessageListAdapter mMessageListAdapter;
    User mRecipient;
    private String filePath = "";
    private String fileName ="";
    //we can only send one file at a time
    private final int MAX_FILES = 1;
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
                    if (!message.equals("") || !filePath.equals("")) {
                        mService.sendTextMessage(mRecipient,message,filePath, fileName);
                        messageText.setText("");
                        filePath = "";
                        fileName = "";
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    // always get the first file
                    NormalFile file = list.get(0);
                    //getting the file name and extension of the file
                    fileName = file.getName()+"."+getFileExtension(file.getPath());
                    filePath = file.getPath();
                }
                break;
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    ImageFile imageFile= list.get(0);
                    fileName = imageFile.getName()+"."+getFileExtension(imageFile.getPath());
                    filePath = imageFile.getPath();
                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    VideoFile videoFile = list.get(0);
                    fileName = videoFile.getName()+"."+getFileExtension(videoFile.getPath());
                    filePath = videoFile.getPath();
                }
                break;
            case Constant.REQUEST_CODE_PICK_AUDIO:
                if (resultCode == RESULT_OK) {
                    ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                    AudioFile audioFile = list.get(0);
                    fileName = audioFile.getName()+"."+getFileExtension(audioFile.getPath());
                    filePath = audioFile.getPath();
                }
                break;
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


    /**
     * creates an alert dialog box to choose files.
     */
    public void alertDialogToChooseFile(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select File Type");
        // Set view of dialog.
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.dialog_choose_file_type, null);
        builder.setView(view1);
        RadioGroup radioGroup = view1.findViewById(R.id.filetype_radio_buttons);
        // start an activityForResult based on the user choice
        builder.setPositiveButton("OK", (dialog, which) -> {
        int checkedButton = radioGroup.getCheckedRadioButtonId();
        Intent intent = null;
        switch (checkedButton){
            case R.id.image_radio_button:
                intent = new Intent(this, ImagePickActivity.class);
                intent.putExtra(IS_NEED_CAMERA, true);
                intent.putExtra(Constant.MAX_NUMBER, MAX_FILES);
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
            case R.id.video_radio_button:
                intent = new Intent(this, VideoPickActivity.class);
                intent.putExtra(IS_NEED_CAMERA, true);
                intent.putExtra(Constant.MAX_NUMBER, MAX_FILES);
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_VIDEO);
                break;
            case R.id.file_radio_button:
                intent = new Intent(this, NormalFilePickActivity.class);
                intent.putExtra(Constant.MAX_NUMBER, MAX_FILES);
                intent.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"xlsx", "xls", "doc",
                        "docx", "ppt", "pptx", "pdf","zip","gif"});
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                break;
            case R.id.audio_radio_button:
                intent = new Intent(this, AudioPickActivity.class);
                intent.putExtra(IS_NEED_RECORDER, true);
                intent.putExtra(Constant.MAX_NUMBER, MAX_FILES);
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_AUDIO);
                break;
        }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> { /* Exit. */ });
        final  AlertDialog dialog = builder.create();
        dialog.show();
    }
}