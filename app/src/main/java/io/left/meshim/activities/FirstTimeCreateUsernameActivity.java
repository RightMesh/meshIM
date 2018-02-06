package io.left.meshim.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.left.meshim.R;
import io.left.meshim.models.Settings;
import io.left.meshim.models.User;

import org.apache.commons.lang3.StringUtils;


public class FirstTimeCreateUsernameActivity extends Activity {
    private User mUser = null;
    private Settings mSettings = null;
    private final int MAX_LENGTH_USERNAME_CHARACTERS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_username);

        mSettings = new Settings(true);
        mSettings.save(this);
        configureFinishButton();
        configureUsernameWatcher();
    }


    /**
     * Creates a User profile.
     *
     */

    private void configureFinishButton() {

        Button button = findViewById(R.id.saveUserNameButton);
        button.setOnClickListener(v -> {
            EditText userText = findViewById(R.id.userNameEditText);
            String userName = userText.getText().toString();
            if (StringUtils.isBlank(userName)) {
                Toast.makeText(FirstTimeCreateUsernameActivity.this,
                        "You Must Enter User Name!", Toast.LENGTH_SHORT).show();
            } else if (userName.length() > MAX_LENGTH_USERNAME_CHARACTERS) {

            }  else {
                mUser = new User(FirstTimeCreateUsernameActivity.this);
                mUser.setUsername(userName);
                mUser.setAvatar(1);
                mUser.save();
                Intent intent = new Intent(FirstTimeCreateUsernameActivity.this,
                        ChooseAvatarActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Checks for valid usernames.
     */
    private void configureUsernameWatcher() {
        TextView charecterCount = findViewById(R.id.charecterCountText);
        TextView errorText = findViewById(R.id.errrorText);
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charecterCount.setText(String.valueOf(s.length()) + "/20");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_LENGTH_USERNAME_CHARACTERS) {
                    errorText.setText("Username longer than 20 characters");
                    errorText.setTextColor(Color.RED);
                } else {
                    errorText.setText("Change it anytime");
                    errorText.setTextColor(Color.BLACK);
                }
            }
        };
        EditText editText = findViewById(R.id.userNameEditText);
        editText.addTextChangedListener(textWatcher);
    }

}
