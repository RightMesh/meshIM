package io.left.meshenger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import io.left.meshenger.Models.User;
import io.left.meshenger.R;

public class FirstTimeCreateUsernameActivity extends Activity {
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_username);

        finishButton();
    }


    /**
     * Creates a user profile
     */

    private void finishButton() {

        Button button = findViewById(R.id.saveUserNameButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userText = findViewById(R.id.userNameEditText);
                String userName = userText.getText().toString();
                if (StringUtils.isBlank(userName)) {
                    Toast.makeText(FirstTimeCreateUsernameActivity.this, "You Must Enter User Name!", Toast.LENGTH_SHORT).show();
                } else {
                    user = new User(userName, 1);
                    user.save(FirstTimeCreateUsernameActivity.this);
                    Intent intent = new Intent(FirstTimeCreateUsernameActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
