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
        User user = new User();
        Settings settings = new Settings(true);

        Intent intent;
        //checking if we already have a mUser profile
        if (user.load(WelcomeActivity.this) && settings.load(WelcomeActivity.this)) {
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
