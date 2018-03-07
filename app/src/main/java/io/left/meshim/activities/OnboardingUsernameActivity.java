package io.left.meshim.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
    public static final int MAX_LENGTH_USERNAME_CHARACTERS = 20;

    private UsernameTextWatcher mValidWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_username);

        configureUsernameWatcher();
    }

    static class UsernameTextWatcher implements TextWatcher {
        TextView mCharacterCount;
        TextView mErrorText;
        Resources resources;
        boolean mIsUsernameValid = false;

        boolean getIsUsernameValid() {
            return mIsUsernameValid;
        }

        UsernameTextWatcher(TextView characterCount, TextView errorText, Context context) {
            this.mCharacterCount = characterCount;
            this.mErrorText = errorText;
            resources = context.getResources();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String message = resources.getString(R.string.username_length, s.length());
            mCharacterCount.setText(message);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > MAX_LENGTH_USERNAME_CHARACTERS) {
                mErrorText.setText(R.string.username_warning_message_length);
                mErrorText.setTextColor(Color.RED);
                mIsUsernameValid = false;
            } else if (s.length() < 1) {
                mErrorText.setText(R.string.username_warning_message_empty);
                mErrorText.setTextColor(Color.RED);
                mIsUsernameValid = false;
            } else {
                mErrorText.setText(R.string.username_sub_prompt);
                mErrorText.setTextColor(Color.BLACK);
                mIsUsernameValid = true;
            }
        }
    };


    /**
     * Creates a User profile when the user presses the save button.
     * @param view button that calls the method
     */
    public void saveUsername(View view) {
        EditText userText = findViewById(R.id.onboarding_username_text_edit);
        String userName = userText.getText().toString();
        if (mValidWatcher.getIsUsernameValid()) {
            User user = new User();
            user.setUsername(userName);
            //set a default avatar
            user.setAvatar(R.mipmap.avatar2);
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
        TextView characterCount = findViewById(R.id.onboarding_username_character_count_text);
        TextView errorText = findViewById(R.id.onboarding_username_error_text);
        mValidWatcher = new UsernameTextWatcher(characterCount, errorText, this);

        EditText editText = findViewById(R.id.onboarding_username_text_edit);
        editText.addTextChangedListener(mValidWatcher);
    }
}
