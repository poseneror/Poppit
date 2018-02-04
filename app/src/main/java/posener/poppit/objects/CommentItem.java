package posener.poppit.objects;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.activities.UserGroupDisplay;
import posener.poppit.adapters.CommentsAdapter;
import posener.poppit.adapters.FeedsAdapter;
import posener.poppit.handlers.SoundPlayer;
import posener.poppit.handlers.TimeHandler;
import posener.poppit.views.RoundProfilePictureView;

/**
 * Created by Or on 21/07/2015.
 */
public class CommentItem extends RecyclerView.ViewHolder {
    private Comment myComment;
    public TextView tvName, tvTime, tvContent;
    public RoundProfilePictureView profilePictureView;
    private Context myContext;
    private CommentsAdapter myAdapter;
    Question myQuestion;

    public CommentItem(View itemView, Context context, CommentsAdapter adapter, Question question) {
        super(itemView);
        myContext = context;
        myAdapter = adapter;
        myQuestion = question;
    }


    public void bindComment(Comment comment) {
        Log.d("bindComment", "CALLED!");
        myComment = comment;
        tvName = (TextView) itemView.findViewById(R.id.comment_username);
        tvTime = (TextView) itemView.findViewById(R.id.comment_date);
        tvContent = (TextView) itemView.findViewById(R.id.comment_content);
        profilePictureView = (RoundProfilePictureView) itemView.findViewById(R.id.comment_profile_picture);
        tvName.setText(myComment.user.getString("name"));
        profilePictureView.setProfileId(myComment.user.get("facebook_id").toString());
        tvTime.setText(TimeHandler.When(myComment.date));
        tvContent.setText(myComment.content);
    }

    public void bindFiller(Filler filler) {
        Log.d("bindFiller", "CALLED!");
        SetNewComment();
    }

    private void SetNewComment(){
        tvName = (TextView) itemView.findViewById(R.id.comment_username);
        profilePictureView = (RoundProfilePictureView)  itemView.findViewById(R.id.comment_profile_picture);
        final EditText etContent = (EditText) itemView.findViewById(R.id.comment_box);
        Button submitComment = (Button) itemView.findViewById(R.id.comment_submit);
        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String text = etContent.getText().toString();
                ParseObject comment = new ParseObject("Comments");
                comment.put("user", currentUser);
                comment.put("content", text);
                comment.put("question", myQuestion.question);
                try {
                    comment.save();
                    Log.d("comment saved!", "YAY");
                } catch (ParseException e) {
                    Log.d("Comment - ", e.getMessage());
                }
                myAdapter.newComment(new Comment(comment));
            }
        });
        ParseUser currentUser = ParseUser.getCurrentUser();
        tvName.setText(currentUser.getString("name"));
        profilePictureView.setProfileId(currentUser.get("facebook_id").toString());
    }
}
