package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.left.meshenger.Models.Settings;
import io.left.meshenger.Models.User;

public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        //checking if we already have a user profile
        if (User.fromDisk(this) != null && Settings.fromDisk(this) != null) {
            intent = new Intent(WelcomeActivity.this, MainTabActivity.class);
        } else {
            // Launch first time activity to create a new profile
            intent = new Intent(WelcomeActivity.this,
                    FirstTimeCreateUsernameActivity.class);
        }
        startActivity(intent);
        finish();
    }
}