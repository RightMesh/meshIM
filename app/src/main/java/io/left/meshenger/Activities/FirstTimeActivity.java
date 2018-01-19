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

    private void finishButton(){
        Button button = findViewById(R.id.finishButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(StringUtils.isBlank(getUserName())){
                    Toast.makeText(FirstTimeActivity.this,"You Must Enter User Name!",Toast.LENGTH_SHORT).show();
                }
                else{
                    user = new User(getUserName(),1);
                    user.save(FirstTimeActivity.this);
                    Intent intent = new Intent(FirstTimeActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    /**
     * The function returns username entered by the user
     * @return a string
     */
    private String getUserName(){
        EditText userName = findViewById(R.id.userNameEditText);
        String user = userName.getText().toString();
        return user;
    }

}
