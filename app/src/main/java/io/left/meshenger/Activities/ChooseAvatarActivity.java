package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import io.left.meshenger.Models.User;
import io.left.meshenger.R;

/**
 * User avatar selection interface.
 */
public class ChooseAvatarActivity extends Activity {
    //used for the table layout
    private static final int ROWS = 9;
    private static final int COLUMNS = 3;

    private User mUser;
    private int mUserAvatarId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure UI
        setContentView(R.layout.activity_choose_avatar);
        setupAvatars();

        mUser = User.fromDisk(this);
        mUserAvatarId = R.mipmap.account_default;

        // Update user and launch app when save button is tapped.
        Button saveButton = findViewById(R.id.saveUserAvatarButton);
        saveButton.setOnClickListener(v -> {
            mUser.setUserAvatar(mUserAvatarId);
            mUser.save();
            Intent intent = new Intent(ChooseAvatarActivity.this, MainTabActivity.class);
            startActivity(intent);
        });
    }

    /**
     * The function finds the scrollview in the layout and creates a dynamic
     * table layout of avatars user can choose from.
     */
    private void setupAvatars() {
        //keep track of the avatar
        int avatarNum = 1;

        ScrollView scrollView = findViewById(R.id.avatarScrollView);
        TableLayout tableLayout = new TableLayout(this);

        for (int r = 0; r < ROWS; r++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1.0f));

            for (int c = 0; c < COLUMNS; c++) {
                final ImageButton imageButton = new ImageButton(this);

                //setting the avatar
                int id = getResources().getIdentifier("avatar" + avatarNum, "mipmap",
                        getPackageName());
                imageButton.setImageResource(id);
                imageButton.setBackgroundResource(R.color.white);
                imageButton.setLayoutParams(
                        new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT, 1.0f));
                imageButton.setPadding(0, 0, 0, 0);
                tableRow.addView(imageButton);

                int finalAvatarNum = avatarNum;
                imageButton.setOnClickListener(v -> {
                    mUserAvatarId = getResources().getIdentifier(
                            "avatar" + finalAvatarNum, "mipmap", getPackageName());
                    ImageButton button = findViewById(R.id.selectedAvatar);
                    button.setImageResource(mUserAvatarId);
                });
                avatarNum++;
            }
            tableLayout.addView(tableRow);
        }
        scrollView.addView(tableLayout);
    }
}