package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.left.meshenger.Models.Settings;
import io.left.meshenger.Models.User;

public class WelcomeActivity extends Activity {

    private final int splashScreenTime = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (User.fromDisk(this) != null && Settings.fromDisk(this) != null) {
            intent = new Intent(WelcomeActivity.this, MainTabActivity.class);
        }
        // Launch first time activity to create a new profile
        else {
            intent = new Intent(WelcomeActivity.this, FirstTimeCreateUsernameActivity.class);

        }
        startActivity(intent);
        finish();
    }

}
