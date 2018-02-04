package posener.poppit.objects;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.activities.UserGroupDisplay;
import posener.poppit.adapters.FeedsAdapter;
import posener.poppit.handlers.SoundPlayer;
import posener.poppit.handlers.TimeHandler;
import posener.poppit.views.RoundProfilePictureView;

/**
 * Created by Or on 27/07/2015.
 */

public class ServiceItem extends RecyclerView.ViewHolder{
    private Question myQuestion;
    public TextView tvName;
    public TextView tvTime;
    public TextView tvContent;
    public RoundProfilePictureView profilePictureView;
    private int moduleType;
    private Context myContext;
    private FeedsAdapter myAdapter;
    private static final int FIRST_OPTION = 0;
    private static final int SECOND_OPTION = 1;

    public ServiceItem(View itemView) {
        super(itemView);
    }

    public void bindQuestion(Question question) {
        myQuestion = question;
        tvName = (TextView) itemView.findViewById(R.id.li_q_publisher);
        tvTime = (TextView) itemView.findViewById(R.id.li_q_date);
        tvContent = (TextView) itemView.findViewById(R.id.li_q_content);
        profilePictureView = (RoundProfilePictureView) itemView.findViewById(R.id.li_q_profile_picture);
        if(myQuestion.publisher != null){
            try {
                myQuestion.publisher = myQuestion.publisher.fetch();
                tvName.setText(myQuestion.publisher.getString("name"));
                profilePictureView.setProfileId(myQuestion.publisher.get("facebook_id").toString());
            }
            catch (ParseException e)
            {
                Log.d("Parse - ", e.getMessage());
            }
        }
        tvTime.setText(TimeHandler.When(myQuestion.publish_time));
        tvContent.setText(myQuestion.content);
        moduleType = myQuestion.type;
        if(!myQuestion.UserVoted()) {
            switch (moduleType) {
                case Question.TYPE_YN:
                    SetYesNoModule();
                    break;
                case Question.TYPE_CUSTOM:
                    SetCustomModule();
                    break;
                case Question.TYPE_IMAGE:
                    SetImageModule();
                    break;
            }
        }
        else{
            switch (moduleType) {
                case Question.TYPE_YN:
                    SetYesNoAnswerModule();
                    break;
                case Question.TYPE_CUSTOM:
                    SetYesNoAnswerModule();
                    break;
                case Question.TYPE_IMAGE:
                    SetImageAnswerModule();
                    break;
            }
        }
    }

    public void SetYesNoAnswerModule()
    {
        TextView UV = (TextView) itemView.findViewById(R.id.user_vote);
        TextView YN = (TextView) itemView.findViewById(R.id.li_q_yes_no);
        TextView tvYes = (TextView) itemView.findViewById(R.id.li_q_answered_y);
        TextView tvNo = (TextView) itemView.findViewById(R.id.li_q_answered_n);
        ProgressBar yBar = (ProgressBar) itemView.findViewById(R.id.li_y_bar);
        ProgressBar nBar = (ProgressBar) itemView.findViewById(R.id.li_n_bar);
        TextView yPercent = (TextView) itemView.findViewById(R.id.li_y_percent);
        TextView nPercent = (TextView) itemView.findViewById(R.id.li_n_percent);
        int yVotes = 0;
        int nVotes = 0;

        if (myQuestion.voted_yes != null) {
            yVotes = myQuestion.voted_yes.size();
            switch (myQuestion.type){
                case Question.TYPE_YN:
                    if (myQuestion.voted_yes.contains(ParseUser.getCurrentUser())) {
                        YN.setText("YES");
                        YN.setTextColor(myContext.getResources().getColor(R.color.poppit_green));
                    } else {
                        YN.setText("NO");
                        YN.setTextColor(myContext.getResources().getColor(R.color.poppit_red));
                    }
                    break;
                case Question.TYPE_CUSTOM:
                    if (myQuestion.voted_yes.contains(ParseUser.getCurrentUser())) {
                        YN.setText(myQuestion.fo);
                        YN.setTextColor(myContext.getResources().getColor(R.color.poppit_green));
                    } else {
                        YN.setText(myQuestion.so);
                        YN.setTextColor(myContext.getResources().getColor(R.color.poppit_red));
                    }
                    break;
            }
            yBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent groupIntent = new Intent(myContext, UserGroupDisplay.class);
                    ArrayList<String> groupUserIds = new ArrayList<String>();
                    for (ParseUser user : myQuestion.voted_yes) {
                        groupUserIds.add(user.getObjectId());
                    }
                    groupIntent.putExtra("groupUsersIds", groupUserIds);
                    groupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myContext.startActivity(groupIntent);
                }
            });

        }
        if(myQuestion.voted_no != null) {
            nVotes = myQuestion.voted_no.size();

            nBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent groupIntent = new Intent(myContext, UserGroupDisplay.class);
                    ArrayList<String> groupUserIds = new ArrayList<String>();
                    for (ParseUser user : myQuestion.voted_no) {
                        groupUserIds.add(user.getObjectId());
                    }
                    groupIntent.putExtra("groupUsersIds", groupUserIds);
                    groupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myContext.startActivity(groupIntent);
                }
            });
        }

        int prog = yVotes / (yVotes+nVotes) * 100;
        yBar.setProgress(prog);
        nBar.setProgress(100-prog);
        tvYes.setPadding(5,0,100-prog+10,0);
        tvNo.setPadding(5,0,prog+10,0);
        yPercent.setText(prog + "%");
        nPercent.setText(100-prog + "%");
        tvYes.setText(yVotes + "friends");
        tvNo.setText(nVotes + "friends");
        if(myQuestion.type==Question.TYPE_CUSTOM){
            tvYes.setText(myQuestion.fo + " (" + yVotes + ")");
            tvNo.setText(myQuestion.so + " (" + nVotes + ")");
        }
    }

    private void SetImageAnswerModule(){
        TextView UV = (TextView) itemView.findViewById(R.id.user_vote);
        TextView YN = (TextView) itemView.findViewById(R.id.li_q_yes_no);
        TextView tvYes = (TextView) itemView.findViewById(R.id.li_q_answered_y);
        TextView tvNo = (TextView) itemView.findViewById(R.id.li_q_answered_n);
        ProgressBar yBar = (ProgressBar) itemView.findViewById(R.id.li_y_bar);
        TextView yPercent = (TextView) itemView.findViewById(R.id.li_y_percent);
        TextView nPercent = (TextView) itemView.findViewById(R.id.li_n_percent);
        ParseImageView ivF = (ParseImageView) itemView.findViewById(R.id.li_first_option_image);
        ParseImageView ivS = (ParseImageView) itemView.findViewById(R.id.li_second_option_image);
        int yVotes = 0;
        int nVotes = 0;

        if (myQuestion.voted_yes != null) {
            yVotes = myQuestion.voted_yes.size();
            if (myQuestion.voted_yes.contains(ParseUser.getCurrentUser())) {
                YN.setText("<");
                YN.setTextColor(myContext.getResources().getColor(R.color.poppit_green));
            } else {
                YN.setText(">");
                YN.setTextColor(myContext.getResources().getColor(R.color.poppit_red));
            }
        }
        if(myQuestion.voted_no != null) {
            nVotes = myQuestion.voted_no.size();
        }
        int prog = yVotes / (yVotes+nVotes) * 100;
        yBar.setProgress(prog);
        yPercent.setText(prog + "%");
        nPercent.setText(100-prog + "%");
        tvYes.setText(yVotes + "friends");
        tvNo.setText(nVotes + "friends");
        ivF.setParseFile(myQuestion.first_opt_img);
        ivF.loadInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {

            }
        });
        ivS.setParseFile(myQuestion.second_opt_img);
        ivS.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {

            }
        });
    }

    private void SetYesNoModule(){
        Button bYes = (Button) itemView.findViewById(R.id.module_y_btn);
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(FIRST_OPTION);
            }
        });
        Button bNo = (Button) itemView.findViewById(R.id.module_n_btn);
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(SECOND_OPTION);
            }
        });
    }

    private void SetCustomModule(){
        Button bFO = (Button) itemView.findViewById(R.id.module_custom_fo);
        bFO.setText(myQuestion.fo);
        bFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(FIRST_OPTION);    }
        });
        Button bSO = (Button) itemView.findViewById(R.id.module_custom_so);
        bSO.setText(myQuestion.so);
        bSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(SECOND_OPTION);     }
        });
    }

    private void SetImageModule(){
        ParseImageView iBFO = (ParseImageView) itemView.findViewById(R.id.module_image_fo);
        iBFO.setParseFile(myQuestion.first_opt_img);
        iBFO.loadInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {

            }
        });
        iBFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vote(FIRST_OPTION);
            }
        });
        ParseImageView iBSO = (ParseImageView) itemView.findViewById(R.id.module_image_so);
        iBSO.setParseFile(myQuestion.first_opt_img);
        iBSO.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {

            }
        });
        iBSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vote(SECOND_OPTION);
            }
        });
    }

    private void Vote(int option){
        switch (option){
            case FIRST_OPTION:
                myQuestion.VoteFirst();
                break;
            case SECOND_OPTION:
                myQuestion.VoteSecond();
                break;
        }
        if(myAdapter!=null)
            myAdapter.notifyItemChanged(getAdapterPosition());
        SoundPlayer.playSound(myContext, SoundPlayer.BALLOON_POP);
    }
}

