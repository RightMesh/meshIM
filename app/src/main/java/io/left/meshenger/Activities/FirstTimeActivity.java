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
    private Settings settings = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);
        finishButton();
    }





    private String getUserName(){
        EditText userName = findViewById(R.id.userNameEditText);
        String user = userName.getText().toString();
        return user;
    }
    private boolean getNotifications(){
        Switch notification = findViewById(R.id.notificationSwitch);
        if(notification.isChecked()){
        return true;
        }
        else {
        return false;
        }
    }

    private void finishButton(){
        Button button = findViewById(R.id.finishButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StringUtils.isBlank(getUserName())){
                    Toast.makeText(FirstTimeActivity.this,"Empty UserName",Toast.LENGTH_SHORT).show();
                }
                else{
                    user = new User(getUserName(),1);
                    user.save(FirstTimeActivity.this);
                    settings =new Settings(getNotifications());
                    settings.save(FirstTimeActivity.this);

                    Intent intent = new Intent(FirstTimeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
