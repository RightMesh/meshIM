package io.left.meshenger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import io.left.meshenger.Adapters.MessageAdapter;
import io.left.meshenger.Models.Message;
import io.left.meshenger.Models.User;
import io.left.meshenger.R;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {
    private RecyclerView mChatrecyclerview;
    private MessageAdapter mMessageAdapter;
    List<Message> mMessagelist = new ArrayList<>();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
       user = new User("Bruce Lee",R.mipmap.avatar_00);
        mChatrecyclerview = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mChatrecyclerview.setNestedScrollingEnabled(false);

        MessageTest();

        mMessageAdapter = new MessageAdapter(this, mMessagelist);
        mChatrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mChatrecyclerview.setAdapter(mMessageAdapter);
        mChatrecyclerview.smoothScrollToPosition(mChatrecyclerview.getAdapter().getItemCount()-1);
        mMessageAdapter.notifyDataSetChanged();


    }

    private void MessageTest() {
        Message message = new Message("Adapt what is useful, reject what is useless, "
                + "and add what is specifically your own.", user, true);
        Message message1 = new Message("Always be yourself, express yourself, have faith"
                + " in yourself, do not go out and look for a successful "
                + "personality and duplicate it.",
                user, false);
        Message message2 = new Message("If you love life, don't waste time, for time is what"
                + " life is made up of.", user, true);
        Message message3 = new Message("The key to immortality is first living a life"
                + " worth remembering", user, false);
        mMessagelist.add(message);
        mMessagelist.add(message1);
        mMessagelist.add(message2);
        mMessagelist.add(message3);
        mMessagelist.add(message);
        mMessagelist.add(message1);
        mMessagelist.add(message2);
        mMessagelist.add(message3);
        mMessagelist.add(message);
        mMessagelist.add(message1);
        mMessagelist.add(message2);
        mMessagelist.add(message3);
    }


}
