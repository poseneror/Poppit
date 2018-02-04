package posener.poppit.objects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import posener.poppit.R;
import posener.poppit.adapters.CommentsAdapter;
import posener.poppit.adapters.FollowupAdapter;
import posener.poppit.handlers.TimeHandler;
import posener.poppit.views.RoundProfilePictureView;

/**
 * Created by Or on 21/07/2015.
 */
public class FollowupItem extends RecyclerView.ViewHolder {
    private Question myFollowup;
    public TextView tvName, tvTime, tvContent;
    public RoundProfilePictureView profilePictureView;
    private Context myContext;
    private FollowupAdapter myAdapter;
    Question myQuestion;

    public FollowupItem(View itemView, Context context, FollowupAdapter adapter, Question question) {
        super(itemView);
        myContext = context;
        myAdapter = adapter;
        myQuestion = question;
    }


    public void bindFollowup(Question followup) {
        Log.d("bindFollowup", "CALLED!");
        myFollowup = followup;
        tvName = (TextView) itemView.findViewById(R.id.comment_username);
        tvTime = (TextView) itemView.findViewById(R.id.comment_date);
        tvContent = (TextView) itemView.findViewById(R.id.comment_content);
        profilePictureView = (RoundProfilePictureView) itemView.findViewById(R.id.comment_profile_picture);
        tvName.setText(myFollowup.publisher.getString("name"));
        profilePictureView.setProfileId(myFollowup.publisher.get("facebook_id").toString());
        tvTime.setText(TimeHandler.When(myFollowup.publish_time));
        tvContent.setText(myFollowup.content);
    }
}
