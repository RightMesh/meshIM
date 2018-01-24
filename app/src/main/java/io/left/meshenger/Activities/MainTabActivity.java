package io.left.meshenger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

import io.left.meshenger.Adapters.UserListAdapter;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;

public class MainTabActivity extends Activity {

    UserListAdapter userListAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("In Range");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Messages");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Account");
        host.addTab(spec);
        sampleUserList();
        onListClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sampleUserList(){
        ListView listView = findViewById(R.id.userListView);
        List<User> userList = new ArrayList<>();
        for(int i =0;i<15;i++){
            User user;
            if(i%2==0){
                user = new User("user "+i,R.mipmap.avatar_00);
            }
            else {
                user = new User("user "+i,R.mipmap.avatar_01);

            }
            userList.add(user);
        }
        userListAdapter = new UserListAdapter(this,userList);
        listView.setAdapter(userListAdapter);

    }
    private void onListClick(){
        ListView listView = findViewById(R.id.userListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainTabActivity.this,ChatActivity.class);
                startActivity(intent);
            }
        });
    }



}
