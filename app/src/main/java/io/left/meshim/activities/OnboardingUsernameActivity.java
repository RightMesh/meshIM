package io.left.meshim.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.left.meshim.R;
import io.left.meshim.models.User;


public class OnboardingUsernameActivity extends AppCompatActivity {
    private static final int MAX_LENGTH_USERNAME_CHARACTERS = 20;

    private boolean mIsUsernameValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_username);

        configureUsernameWatcher();
    }


    /**
     * Creates a User profile when the user presses the save button.
     * @param view button that calls the method
     */
    public void saveUsername(View view) {
        EditText userText = findViewById(R.id.onboarding_username_text_edit);
        String userName = userText.getText().toString();
        if (mIsUsernameValid) {
            User user = new User();
            user.setUsername(userName);
            user.setAvatar(1);
            user.save(OnboardingUsernameActivity.this);

            Intent intent = new Intent(OnboardingUsernameActivity.this, ChooseAvatarActivity.class);
            intent.setAction(ChooseAvatarActivity.ONBOARDING_ACTION);
            startActivity(intent);

            finish();
        }
    }

    /**
     * Checks for valid usernames.
     */
    private void configureUsernameWatcher() {
        TextView charecterCount = findViewById(R.id.onboarding_username_character_count_text);
        TextView errorText = findViewById(R.id.onboarding_username_error_text);
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = getResources().getString(R.string.username_length, s.length());
                charecterCount.setText(message);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_LENGTH_USERNAME_CHARACTERS) {
                    errorText.setText(R.string.username_warning_message_length);
                    errorText.setTextColor(Color.RED);
                    mIsUsernameValid = false;
                } else if (s.length() < 1) {
                    errorText.setText(R.string.username_warning_message_empty);
                    errorText.setTextColor(Color.RED);
                    mIsUsernameValid = false;
                } else {
                    errorText.setText(R.string.username_sub_prompt);
                    errorText.setTextColor(Color.BLACK);
                    mIsUsernameValid = true;
                }
            }
        };
        EditText editText = findViewById(R.id.onboarding_username_text_edit);
        editText.addTextChangedListener(textWatcher);
    }
}
