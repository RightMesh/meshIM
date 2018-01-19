package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import io.left.meshenger.Models.Settings;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;

public class WelcomeActivity extends Activity {

    private final int splashScreenTime = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {

            User user = new User();
            Settings settings = new Settings(true);

            //checking if we already have a user profile
            if(user.load(WelcomeActivity.this) && settings.load(WelcomeActivity.this)){
                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            // create a new profile
            else{
                Intent intent = new Intent(WelcomeActivity.this,FirstTimeActivity.class);
                startActivity(intent);
                finish();
            }

        }
    },splashScreenTime);
    }

}
