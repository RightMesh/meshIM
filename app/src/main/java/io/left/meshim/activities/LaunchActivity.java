package io.left.meshim.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import io.left.meshim.models.Settings;
import io.left.meshim.models.User;

/**
 * Class that launches the application. Checks whether the user needs to be onboarded or not,
 * and launches the next appropriate Activity.
 */
public class LaunchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize settings if not found.
        Settings settings = Settings.fromDisk(this);
        if (settings == null) {
            settings = new Settings();
            settings.save(this);
        }

        Intent intent;
        //checking if we already have a user profile
        if (User.fromDisk(this) != null) {
            intent = new Intent(LaunchActivity.this, MainActivity.class);
        } else {
            // Launch first time activity to create a new profile
            intent = new Intent(LaunchActivity.this, OnboardingUsernameActivity.class);
        }
        startActivity(intent);
        finish();
    }
}