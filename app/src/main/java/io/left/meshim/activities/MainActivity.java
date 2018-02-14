package io.left.meshim.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.constraint.ConstraintLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import io.left.meshim.R;
import io.left.meshim.adapters.ConversationListAdapter;
import io.left.meshim.adapters.OnlineUserListAdapter;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.Settings;
import io.left.meshim.models.User;

import java.util.ArrayList;

/**
 * Main interface for MeshIM. Displays tabs for viewing online users, conversations, and the
 * user's account.
 */
public class MainActivity extends ServiceConnectedActivity {
    // Adapter that populates the online user list with user information from the app service.
    OnlineUserListAdapter mOnlineUserListAdapter;
    ArrayList<User> mUsers = new ArrayList<>();
    ArrayList<ConversationSummary> mConversationSummaries = new ArrayList<>();
    ConversationListAdapter mConversationListAdapter;

    /**
     * Initializes UI elements.
     * @param savedInstanceState passed by Android
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureTabs();
        configureUserList();
        configureMessageList();
        setupSettingTab();
        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSettingTab();
    }

    /**
     * Configure the content and UI of the tabs.
     */
    private void configureTabs() {
        TabHost host = findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator(createTabIndicator(this,"In Range",R.mipmap.in_range_default));
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator(createTabIndicator(this,"Messages",R.mipmap.messages_default));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator(createTabIndicator(this,"Account",R.mipmap.account_default));
        host.addTab(spec);
    }

    private View createTabIndicator(Context context, String title, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView iv = view.findViewById(R.id.imageView);
        iv.setImageResource(icon);
        TextView tv = view.findViewById(R.id.tabText);
        tv.setText(title);
        return view;
    }

    /**
     * Configure the user list adapter and click event.
     */
    private void configureUserList() {
        ListView listView = findViewById(R.id.userListView);
        listView.setEmptyView(findViewById(R.id.empty_list_item));
        mOnlineUserListAdapter = new OnlineUserListAdapter(this, mUsers);
        listView.setAdapter(mOnlineUserListAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            User peer = mOnlineUserListAdapter.getItem(position);
            if (peer != null) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("recipient", peer);
                startActivity(intent);
            }
        });
    }

    /**
     * Configures the conversation view list and click event.
     */
    private void configureMessageList() {
        ListView listView = findViewById(R.id.multiUserMessageListView);
        mConversationListAdapter = new ConversationListAdapter(this, mConversationSummaries);
        listView.setAdapter(mConversationListAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (mService != null) {
                ConversationSummary selected = mConversationListAdapter.getItem(position);
                if (selected != null) {
                    User peer = null;
                    try {
                        peer = mService.fetchUserById(selected.peerID);
                    } catch (RemoteException e) {
                        if (e instanceof DeadObjectException) {
                            reconnectToService();
                        }
                        // TODO: Message list might conceivably grow too big for AIDL. This should
                        // be handled here.
                    }
                    if (peer != null) {
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra("recipient", peer);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    /**
     * Updates all adapters when data changes on the service.
     */
    @Override
    public void updateInterface() {
        runOnUiThread(() -> {
            try {
                mOnlineUserListAdapter.updateList(mService);
                mOnlineUserListAdapter.notifyDataSetChanged();
                mConversationListAdapter.updateList(mService);
                mConversationListAdapter.notifyDataSetChanged();
            } catch (DeadObjectException doe) {
                reconnectToService();
            }
        });
    }

    /**
     * setup buttons and switches in the setting tab.
     */
    private void setupSettingTab() {
        Settings settings = Settings.fromDisk(this);
        if (settings != null) {
            //turn notification on/off
            Switch notificationSwitch = findViewById(R.id.settings_notifications_switch);
            notificationSwitch.setChecked(settings.isShowNotifications());
            notificationSwitch.setOnClickListener(view -> {
                if (notificationSwitch.isChecked()) {
                    settings.setShowNotifications(true);
                } else {
                    settings.setShowNotifications(false);
                }
                settings.save(this);
            });
        }

        //show rightmesh services
        ConstraintLayout fl = findViewById(R.id.settings_rightmesh);
        fl.setOnClickListener(v -> {
            try {
                mService.showRightMeshSettings();
            } catch (RemoteException e) {
                if (e instanceof DeadObjectException) {
                    reconnectToService();
                }
                // Otherwise, nothing we can do here.
            }
        });
        TextView userNameButton = findViewById(R.id.settings_username_button);
        userNameButton.setOnClickListener(v -> alertDialog());

        User user = User.fromDisk(this);
        //setup userAvatar
        if (user != null) {
            TextView userNameText = findViewById(R.id.settings_username_text_view);
            userNameText.setText(user.getUsername());
            ImageButton userAvatar = findViewById(R.id.settings_user_avatar);
            userAvatar.setImageResource(user.getAvatar());
            ImageButton button = findViewById(R.id.settings_user_avatar_edit_button);
            button.setOnClickListener(v -> {
                Intent avatarChooseIntent = new Intent(MainActivity.this,
                        ChooseAvatarActivity.class);
                startActivity(avatarChooseIntent);
            });
        }
    }

    /**
     * creates an alert dialog box to change username.
     */
    private void alertDialog() {
        final  AlertDialog levelDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_onboarding_username_activity);

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String username = input.getText().toString();
            if (!username.isEmpty()) {
                User user = User.fromDisk(this);
                if (user != null && username.length() <= 20) {
                    user.setUsername(username);
                    user.save(this);
                    TextView textView = findViewById(R.id.settings_username_text_view);
                    textView.setText(username);
                } else if (username.length() > 20) {
                    Toast.makeText(MainActivity.this,
                            R.string.username_warning_message_length, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.username_warning_message_empty,
                        Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> { /* Exit. */ });
        levelDialog = builder.create();
        levelDialog.show();
    }

    /**
     * Sets up action bar icon and logo.
     */
    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            String title = "MeshIM";
            SpannableString spannableTittle = new SpannableString(title);
            spannableTittle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(spannableTittle);
        }
    }
}