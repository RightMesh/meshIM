package io.left.meshim.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;

import io.left.meshim.R;
import io.left.meshim.activities.OnboardingUsernameActivity.UsernameTextWatcher;
import io.left.meshim.adapters.ConversationListAdapter;
import io.left.meshim.adapters.OnlineUserListAdapter;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.Settings;
import io.left.meshim.models.User;

import java.util.ArrayList;

/**
 * Main interface for meshIM. Displays tabs for viewing online users, conversations, and the
 * user's account.
 */
public class MainActivity extends ServiceConnectedActivity
        implements TabHost.OnTabChangeListener {
    // Adapter that populates the online user list with user information from the app service.
    OnlineUserListAdapter mOnlineUserListAdapter;
    ArrayList<User> mUsers = new ArrayList<>();
    ArrayList<ConversationSummary> mConversationSummaries = new ArrayList<>();
    ConversationListAdapter mConversationListAdapter;
    boolean mShouldBroadcast = false;
    //view to update message tab to show there are unread messages
    View mViewForMessageTab;

    // Tab management
    private TabHost mTabs;
    private static final int[] DEFAULT_TAB_ICONS = { R.mipmap.in_range_default,
            R.mipmap.messages_default, R.mipmap.account_default};
    private static final int[] ACTIVE_TAB_ICONS = { R.drawable.ic_in_range_active,
            R.drawable.ic_message_active, R.drawable.ic_account_active };
    private View mActiveTabView = null;
    private int mActiveTabPosition;

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
        if (mShouldBroadcast) {
            if (mService != null) {
                try {
                    mService.broadcastUpdatedProfile();
                } catch (RemoteException e) {
                    if (e instanceof DeadObjectException) {
                        reconnectToService();
                    }
                    // Otherwise, the change will be saved just not propagated now.
                }
            }
            mShouldBroadcast = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (mActiveTabPosition > 0) {
            mTabs.setCurrentTab(0);
        } else if (mActiveTabPosition == 0) {
            super.onBackPressed();
        }
    }

    /**
     * Configure the content and UI of the tabs.
     */
    private void configureTabs() {
        Resources resources = getResources();

        mTabs = findViewById(R.id.tabHost);
        mTabs.setOnTabChangedListener(this);
        mTabs.setup();

        //Tab 1
        TabHost.TabSpec spec = mTabs.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator(createTabIndicator(this, resources.getString(R.string.tab_in_range),
                DEFAULT_TAB_ICONS[0]));
        mTabs.addTab(spec);

        //Tab 2
        spec = mTabs.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        //reference for future when we need to indicate that there are new messages
        mViewForMessageTab = createTabIndicator(this, resources.getString(R.string.tab_messages),
                DEFAULT_TAB_ICONS[1]);
        spec.setIndicator(mViewForMessageTab);
        mTabs.addTab(spec);

        //Tab 3
        spec = mTabs.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator(createTabIndicator(this,resources.getString(R.string.tab_account),
                DEFAULT_TAB_ICONS[2]));
        mTabs.addTab(spec);
    }

    private View createTabIndicator(Context context, String title, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView iv = view.findViewById(R.id.tab_image);
        iv.setImageResource(icon);
        TextView tv = view.findViewById(R.id.tab_text);
        tv.setText(title);
        return view;
    }

    @Override
    public void onTabChanged(String s) {
        ImageView icon;
        TextView text;
        if (mActiveTabView != null) {
            icon = mActiveTabView.findViewById(R.id.tab_image);
            icon.setImageResource(DEFAULT_TAB_ICONS[mActiveTabPosition]);
            text = mActiveTabView.findViewById(R.id.tab_text);
            text.setTextSize(14);
        }

        mActiveTabView = mTabs.getCurrentTabView();
        mActiveTabPosition = mTabs.getCurrentTab();
        icon = mActiveTabView.findViewById(R.id.tab_image);
        icon.setImageResource(ACTIVE_TAB_ICONS[mActiveTabPosition]);
        text = mActiveTabView.findViewById(R.id.tab_text);
        text.setTextSize(15);
    }

    /**
     * Configure the user list adapter and click event.
     */
    private void configureUserList() {
        ListView listView = findViewById(R.id.userListView);
        listView.setEmptyView(findViewById(R.id.user_list_loading_view));
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
        listView.setEmptyView(findViewById(R.id.message_list_loading_view));
        mConversationListAdapter = new ConversationListAdapter(this, mConversationSummaries);
        listView.setAdapter(mConversationListAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (mService != null) {
                ConversationSummary selected = mConversationListAdapter.getItem(position);
                if (selected != null) {
                    User peer = null;
                    try {
                        peer = mService.fetchUserById(selected.peerId);
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
                //notify user of new messages.
                TextView newMessageNotification =
                        mViewForMessageTab.findViewById(R.id.tab_badge);
                /*whenever the UI is updated, we check in the conversation summary list if there are
                any unread messages. if there are unread messages, the notification badge in the
                message tab pops up.
                we only check the first item on the conversation summary list as they are arranged
                by the newest message*/
                if (!mConversationSummaries.isEmpty() && !mConversationSummaries.get(0).isRead) {
                    newMessageNotification.setVisibility(View.VISIBLE);
                } else {
                    newMessageNotification.setVisibility(View.INVISIBLE);
                }
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
                MainActivity.this.mShouldBroadcast = true;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.username);

        // Set view of dialog.
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_username, null);
        builder.setView(view);
        EditText input = view.findViewById(R.id.dialog_username_text_edit);

        // Load user and initialize username
        User user = User.fromDisk(this);
        if (user == null) {
            return;
        }
        String username = user.getUsername();
        input.setText(username);
        input.requestFocus();

        // Configure validity checker for username.
        TextView characterCount = view.findViewById(R.id.dialog_username_character_count_text);
        TextView errorText = view.findViewById(R.id.dialog_username_error_text);
        characterCount.setText(getResources().getString(R.string.username_length,
                username.length()));
        UsernameTextWatcher validWatcher = new UsernameTextWatcher(characterCount, errorText, this);
        input.addTextChangedListener(validWatcher);

        // Check validity and attempt to save new username when save button pressed.
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String newUsername = input.getText().toString();
            if (validWatcher.getIsUsernameValid()) {
                user.setUsername(newUsername);
                user.save(this);
                TextView textView = findViewById(R.id.settings_username_text_view);
                textView.setText(newUsername);

                if (mService != null) {
                    try {
                        mService.broadcastUpdatedProfile();
                    } catch (RemoteException e) {
                        if (e instanceof DeadObjectException) {
                            reconnectToService();
                        }
                        // Otherwise, the change will be saved just not propagated now.
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> { /* Exit. */ });

        final  AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Sets up action bar icon and logo.
     */
    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            String title = getResources().getString(R.string.app_name);
            SpannableString spannableTittle = new SpannableString(title);
            spannableTittle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(spannableTittle);
        }
    }
}