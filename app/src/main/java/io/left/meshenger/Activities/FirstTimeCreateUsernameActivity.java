package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.left.meshenger.Models.Settings;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;

import org.apache.commons.lang3.StringUtils;



public class FirstTimeCreateUsernameActivity extends Activity {
    private User mUser = null;
    private Settings mSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_username);

        //saving dummy settings
        mSettings = new Settings(true);
        mSettings.save(this);
        finishButton();
    }


    /**
     * Creates a User profile.
     *
     */

    private void finishButton() {

        Button button = findViewById(R.id.saveUserNameButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userText = findViewById(R.id.userNameEditText);
                String userName = userText.getText().toString();
                if (StringUtils.isBlank(userName)) {
                    Toast.makeText(FirstTimeCreateUsernameActivity.this,
                            "You Must Enter User Name!", Toast.LENGTH_SHORT).show();
                } else {
                    mUser = new User(FirstTimeCreateUsernameActivity.this);
                    mUser.setUsername(userName);
                    mUser.setAvatar(1);
                    mUser.save();
                    Intent intent = new Intent(FirstTimeCreateUsernameActivity.this,
                            ChooseAvatarActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
