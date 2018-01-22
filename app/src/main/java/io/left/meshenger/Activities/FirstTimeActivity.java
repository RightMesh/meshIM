package io.left.meshenger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import io.left.meshenger.Models.Settings;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;

public class FirstTimeActivity extends Activity {
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        finishButton();
    }


    /**
     * Creates a user profile
     */

    private void finishButton() {

        EditText userText = findViewById(R.id.userNameEditText);
        String userName = userText.getText().toString();

        Button button = findViewById(R.id.finishButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtils.isBlank(userName)) {
                    Toast.makeText(FirstTimeActivity.this, "You Must Enter User Name!", Toast.LENGTH_SHORT).show();
                } else {
                    user = new User(userName, 1);
                    user.save(FirstTimeActivity.this);
                    Intent intent = new Intent(FirstTimeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
