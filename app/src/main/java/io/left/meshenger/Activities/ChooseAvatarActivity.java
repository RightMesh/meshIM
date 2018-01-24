package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import io.left.meshenger.Models.User;
import io.left.meshenger.R;

public class ChooseAvatarActivity extends Activity {
    User user = new User();
    private final int ROWS = 10;
    private final int COLUMNS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_avatar);
        user.load(this);
        setupAvatars();
    }

    private void setupAvatars() {
        user.load(this);
        ScrollView scrollView = findViewById(R.id.avatarScrollView);
        TableLayout tableLayout = new TableLayout(this);


        for (int r = 0; r < ROWS; r++) {
            final int curRow = r;

            //start a new row
            TableRow tableRow = new TableRow(this);

            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT
                    , TableLayout.LayoutParams.MATCH_PARENT, 1.0f));
            // tableLayout.addView(tableRow);
            //fill in the column
            for (int c = 0; c < COLUMNS; c++) {
                int curCol = c;
                final ImageButton imageButton = new ImageButton(this);
                imageButton.setBackgroundResource(R.drawable.avatar);
                int id = getResources().getIdentifier("avatar_" + curRow + curCol, "mipmap", getPackageName());
                imageButton.setImageResource(id);
                imageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT, 1.0f));
                imageButton.setPadding(0, 0, 0, 0);
                tableRow.addView(imageButton);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = getResources().getIdentifier("avatar_" + curRow + curCol, "mipmap", getPackageName());
                        ImageButton button = findViewById(R.id.selectedAvatar);
                        button.setImageResource(id);
                    }
                });
            }

            tableLayout.addView(tableRow);

        }
        scrollView.addView(tableLayout);
    }

    private void saveAvatar(int id) {
        Button saveButton =findViewById(R.id.saveUserNameButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setUserAvatar(id);
                user.save(ChooseAvatarActivity.this);
                Intent intent = new Intent(ChooseAvatarActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
