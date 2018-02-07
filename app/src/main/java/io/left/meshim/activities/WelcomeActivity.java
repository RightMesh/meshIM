package io.left.meshim.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.left.meshim.models.Settings;
import io.left.meshim.models.User;

public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        //checking if we already have a user profile
        if (User.fromDisk(this) != null && Settings.fromDisk(this) != null) {
            intent = new Intent(WelcomeActivity.this, MainActivity.class);
        } else {
            // Launch first time activity to create a new profile
            intent = new Intent(WelcomeActivity.this,
                    FirstTimeCreateUsernameActivity.class);
        }
        startActivity(intent);
        finish();
    }
}