package io.left.meshim.adapters;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.left.meshim.R;
import io.left.meshim.activities.MainActivity;
import io.left.meshim.models.ConversationSummary;
import io.left.meshim.services.IMeshIMService;

import java.util.ArrayList;

/**
 * Adapter that fetches conversations from the app service to populate the list of stored
 * conversations in {@link MainActivity}.
 */
public class ConversationListAdapter extends ArrayAdapter<ConversationSummary> {
    // Used to inflate views for the list.
    private Context mContext;

    /**
     * Stores context so we can inflate views.
     * @param context context of activity
     * @param conversations list to manage
     */
    public ConversationListAdapter(Context context, ArrayList<ConversationSummary> conversations) {
        super(context, R.layout.online_user_list_item, conversations);
        this.mContext = context;
    }

    /**
     * Updates the list of conversation summaries.
     * @param service service connection to fetch users from
     */
    public void updateList(IMeshIMService service) {
        if (service == null) {
            return;
        }
        try {
            this.clear();
            this.addAll(service.getConversationSummaries());
        } catch (RemoteException ignored) { /* Leave the list untouched on failure. */ }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)  {
        View v = View.inflate(mContext, R.layout.user_message_list, null);

        ConversationSummary conversationSummary = this.getItem(position);
        if (conversationSummary != null) {
            ImageView userAvatar = v.findViewById(R.id.userMessageAvatar);
            userAvatar.setImageResource(conversationSummary.avatar);
            TextView userName = v.findViewById(R.id.userNameMessageText);
            userName.setText(conversationSummary.username);
            TextView newMessage = v.findViewById(R.id.userNewestMessageText);
            newMessage.setText(conversationSummary.messageText);
            TextView time = v.findViewById(R.id.userNewestMessageTimeText);
            time.setText(conversationSummary.messageTime.toString());
        }

        return v;
    }
}