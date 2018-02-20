package io.left.meshim.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.left.meshim.R;
import io.left.meshim.activities.MainActivity;
import io.left.meshim.database.MeshIMDao;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.models.Message;
import io.left.meshim.services.IMeshIMService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Adapter that fetches conversations from the app service to populate the list of stored
 * conversations in {@link MainActivity}.
 */
public class ConversationListAdapter extends ArrayAdapter<ConversationSummary> {
    // Used to inflate views for the list.
    private Context mContext;
    private MeshIMDao dao;
    /**
     * Stores context so we can inflate views.
     * @param context context of activity
     * @param conversations list to manage
     */
    public ConversationListAdapter(Context context, ArrayList<ConversationSummary> conversations) {
        super(context, R.layout.list_item_online_user, conversations);
        this.mContext = context;
    }

    /**
     * Updates the list of conversation summaries.
     * @param service service connection to fetch users from
     * @throws DeadObjectException if service connection dies unexpectedly
     */
    public void updateList(IMeshIMService service) throws DeadObjectException {
        if (service == null) {
            return;
        }
        try {
            List<ConversationSummary> query = service.fetchConversationSummaries();
            this.clear();
            this.addAll(query);
        } catch (RemoteException e) {
            // If the connection has died, propagate error, otherwise ignore it.
            if (e instanceof DeadObjectException) {
                throw (DeadObjectException) e;
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)  {
        View v = View.inflate(mContext, R.layout.list_item_conversation, null);

        ConversationSummary conversationSummary = this.getItem(position);
        if (conversationSummary != null) {
            ImageView userAvatar = v.findViewById(R.id.userMessageAvatar);
            userAvatar.setImageResource(conversationSummary.avatar);
            TextView userName = v.findViewById(R.id.userNameMessageText);
            userName.setText(conversationSummary.username);
            TextView newMessage = v.findViewById(R.id.userNewestMessageText);
            newMessage.setText(conversationSummary.messageText);
            TextView newMessageBadge = v.findViewById(R.id.newMessageNotificationBadge);
            Log.d("bug","convAdapter "+conversationSummary.isRead);
            if(!conversationSummary.isRead){
                Log.d("bug","convAdapter inside if statement "+conversationSummary.isRead);

                newMessageBadge.setBackgroundColor(Color.RED);
             //   newMessage.setTextColor(Color.BLACK);
            }
            else{
                newMessageBadge.setBackgroundColor(Color.WHITE);
                Log.d("bug","convAdapter outside "+conversationSummary.isRead);

            }
            Date currDate = conversationSummary.messageTime;
            TextView time = v.findViewById(R.id.userNewestMessageTimeText);
            time.setText(Message.formateDate(conversationSummary.messageTime));
        }

        return v;
    }
}