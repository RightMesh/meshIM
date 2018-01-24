package io.left.meshenger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import io.left.meshenger.Adapters.MessageAdapter;
import io.left.meshenger.Models.Message;
import io.left.meshenger.R;
import io.left.rightmesh.id.MeshID;

public class ChatActivity extends Activity {
    //things needed to work with the list view
    private RecyclerView chat;
    private MessageAdapter messageListAdapter;
    List<Message> messages = new ArrayList<>();
    private static final int HELLO_PORT = 9876;
    //MeshID userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //userID = (MeshID) getIntent().getExtras().getSerializable("meshID");
      //  Toast.makeText(ChatActivity.this, userID.toString(), Toast.LENGTH_SHORT).show();

        chat = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        chat.setNestedScrollingEnabled(false);
        Message message = new Message("Adapt what is useful, reject what is useless, " +
                "and add what is specifically your own.", "Bruce Lee", true);
        Message message1 = new Message("Always be yourself, express yourself, have faith" +
                " in yourself, do not go out and look for a successful personality and duplicate it.",
                "Bruce Lee", false);
        Message message2 = new Message("If you love life, don't waste time, for time is what" +
                " life is made up of.", "Bruce Lee", true);
        Message message3 = new Message("The key to immortality is first living a life" +
                " worth remembering", "Bruce Lee", false);
        messages.add(message);
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message);
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message);
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message);
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message);
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messageListAdapter = new MessageAdapter(this, messages);

        chat.setLayoutManager(new LinearLayoutManager(this));
        chat.setAdapter(messageListAdapter);


      //  sendButton();

    }







}
