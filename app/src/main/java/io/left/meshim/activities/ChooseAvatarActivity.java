package io.left.meshim.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import io.left.meshim.R;
import io.left.meshim.models.User;

/**
 * User avatar selection interface.
 */
public class ChooseAvatarActivity extends AppCompatActivity {
    public static final String ONBOARDING_ACTION = "from onboarding";

    //used for the table layout
    private static final int ROWS = 9;
    private static final int COLUMNS = 3;

    private Button mSaveButton;
    private ImageButton mSelectedAvatar;
    private int mUserAvatarId = R.mipmap.account_default;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_avatar);

        // Populate list of avatars.
        setupAvatars();

        // Save reference to buttons for use when avatars are selected
        mSaveButton = findViewById(R.id.saveUserAvatarButton);
        mSelectedAvatar = findViewById(R.id.selectedAvatar);
    }

    /**
     * Update user and launch next activity when save button is tapped.
     * @param view save button
     */
    public void saveAvatar(View view) {
        User user = User.fromDisk(this);
        if (user == null) {
            // Initialize if not found, so we can still save the user's selection.
            user = new User();
        }
        user.setAvatar(mUserAvatarId);
        user.save(this);

        // Launch app if called from onboarding activity.
        String action = getIntent().getAction();
        if (action != null && action.equals(ONBOARDING_ACTION)) {
            Intent intent = new Intent(ChooseAvatarActivity.this, MainActivity.class);
            startActivity(intent);
        }

        finish();
    }

    /**
     * The function finds the scrollview in the layout and creates a dynamic
     * table layout of avatars user can choose from.
     */
    private void setupAvatars() {
        //keep track of the avatars
        int avatarNum = 1;

        ScrollView scrollView = findViewById(R.id.avatarScrollView);
        TableLayout tableLayout = new TableLayout(this);
       /* android.support.constraint.ConstraintLayout.LayoutParams fl = ( android.support.constraint
                .ConstraintLayout.LayoutParams)scrollView.getLayoutParams();
        fl.setMargins(16,24,16,24);
        tableLayout.setLayoutParams(fl);*/
        for (int r = 0; r < ROWS; r++) {
            TableRow tableRow = new TableRow(this);
            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
            tableRow.setLayoutParams(tableRowParams);

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
                    mSelectedAvatar.setImageResource(mUserAvatarId);
                    mSaveButton.setClickable(true);
                });
                avatarNum++;
            }
            tableLayout.addView(tableRow);
        }
        scrollView.addView(tableLayout);
    }
}