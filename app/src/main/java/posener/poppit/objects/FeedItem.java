package posener.poppit.objects;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.activities.UserGroupDisplay;
import posener.poppit.adapters.CommentsAdapter;
import posener.poppit.adapters.FeedsAdapter;
import posener.poppit.adapters.FollowupAdapter;
import posener.poppit.handlers.SoundPlayer;
import posener.poppit.handlers.TimeHandler;
import posener.poppit.interfaces.OnDataSetChangeListener;
import posener.poppit.views.RoundProfilePictureView;

/**
 * Created by Or on 21/07/2015.
 */
public class FeedItem extends RecyclerView.ViewHolder{
    private Question myQuestion;
    public TextView tvName;
    public TextView tvTime, tvContent;
    public Button tvFollowUps, tvTotalVotes, tvShouts;
    public RoundProfilePictureView profilePictureView;
    private int moduleType;
    private Context myContext;
    private FeedsAdapter myAdapter;
    private TextView tieBreakTV;
    private CardView myCard;
    private static final int FIRST_OPTION = 0;
    private static final int SECOND_OPTION = 1;

    private ListView shoutsView = (ListView) itemView.findViewById(R.id.extra_shouts);
    private ListView followupsView = (ListView) itemView.findViewById(R.id.extra_followups);

    public FeedItem(View itemView, Context context, FeedsAdapter adapter) {
        super(itemView);
        myContext = context;
        myAdapter = adapter;
    }

    public void bindQuestion(Question question) {
        myQuestion = question;
        myCard = (CardView) itemView.findViewById(R.id.feed_card);
        tvName = (TextView) itemView.findViewById(R.id.li_q_publisher);
        tvTime = (TextView) itemView.findViewById(R.id.li_q_date);
        tvContent = (TextView) itemView.findViewById(R.id.li_q_content);
        tvFollowUps = (Button) itemView.findViewById(R.id.extra_followups_button);
        tvTotalVotes = (Button) itemView.findViewById(R.id.extra_total);
        tvShouts = (Button) itemView.findViewById(R.id.extra_shouts_button);
        profilePictureView = (RoundProfilePictureView) itemView.findViewById(R.id.li_q_profile_picture);
        tieBreakTV = (TextView) itemView.findViewById(R.id.question_tiebreaker);
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

        followupsView.setAdapter(null);
        shoutsView.setAdapter(null);

        final ArrayList<Object> followups = question.getFollowups();
        if(!followups.isEmpty())
        {
            tvFollowUps.setText(followups.size() + " followups");
            tvFollowUps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadFollowups(followups);
                    shoutsView.setAdapter(null);
                }
            });
        }
        else {
            tvFollowUps.setText("no followups");
        }
        tvTotalVotes.setText(myQuestion.total_votes + " votes");

        final ArrayList<Object> comments = question.GetComments();
        if(!question.GetComments().isEmpty()) {
            tvShouts.setText(comments.size() + " shouts");
        }
        else {
            tvShouts.setText("shout");
        }
        tvShouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadShouts(comments);
                followupsView.setAdapter(null);
            }
        });

        resetHeight(followupsView);
        resetHeight(shoutsView);
        //myCard.setBackgroundColor(myContext.getResources().getColor(R.color.poppit_white));
        if((myQuestion.tieBreaker && !myQuestion.UserVoted()) || myQuestion.solved){
            tieBreakTV.setVisibility(View.VISIBLE);
            if(myQuestion.tieBreaker){
                tieBreakTV.setText("Tie Breaker!");
            }
            else{
                tieBreakTV.setText("SOLVED!");
                //myCard.setBackgroundColor(myContext.getResources().getColor(R.color.poppit_white));
            }
        }
        else{
            tieBreakTV.setVisibility(View.GONE);
        }

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
    public void bindFiller(Filler filler) {
        SetMyProfile();
    }
    private void SetMyProfile(){
        tvName = (TextView) itemView.findViewById(R.id.my_user_name);
        profilePictureView = (RoundProfilePictureView)  itemView.findViewById(R.id.my_profile_picture);
        tvTime = (TextView) itemView.findViewById(R.id.my_join_date);
        ParseUser currentUser = ParseUser.getCurrentUser();
        tvName.setText(currentUser.getString("name"));
        profilePictureView.setProfileId(currentUser.get("facebook_id").toString());
        tvTime.setText("joined poppit: " + TimeHandler.When(currentUser.getCreatedAt()));
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
                    FollowUp(FIRST_OPTION);
                }
            });

        }
        if(myQuestion.voted_no != null) {
            nVotes = myQuestion.voted_no.size();
            nBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FollowUp(SECOND_OPTION);
                }
            });
        }

        int prog = myQuestion.getFirstVotePercent();
        yBar.setProgress(prog);
        nBar.setProgress(100-prog);
        tvYes.setPadding(5,0,100-prog+10,0);
        tvNo.setPadding(5,0,prog+10,0);
        yPercent.setText(prog + "%");
        nPercent.setText(100-prog + "%");
        tvYes.setText(yVotes + " friends");
        tvNo.setText(nVotes + " friends");
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
        int prog = myQuestion.getFirstVotePercent();
        yBar.setProgress(prog);
        yPercent.setText(prog + "%");
        nPercent.setText(100-prog + "%");
        tvYes.setText(yVotes + " friends");
        tvNo.setText(nVotes + " friends");
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
        ImageButton foZoomButton = (ImageButton) itemView.findViewById(R.id.question_fo_zoom);
        foZoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(myContext);
                dialog.setContentView(R.layout.dialog_image);
                dialog.setTitle("Image zoom:");
                dialog.setCancelable(true);
                TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                dialogTitle.setText("Option 1");
                ParseImageView dialogImage = (ParseImageView) dialog.findViewById(R.id.dialog_image);
                dialogImage.setParseFile(myQuestion.first_opt_img);
                dialogImage.loadInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {

                    }
                });
                ImageButton dialogClose = (ImageButton) dialog.findViewById(R.id.dialog_close);
                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
                dialog.show();
            }
        });

        ImageButton soZoomButton = (ImageButton) itemView.findViewById(R.id.question_so_zoom);
        soZoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(myContext);
                dialog.setContentView(R.layout.dialog_image);
                dialog.setTitle("Image zoom:");
                dialog.setCancelable(true);
                TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                dialogTitle.setText("Option 2");
                ParseImageView dialogImage = (ParseImageView) dialog.findViewById(R.id.dialog_image);
                dialogImage.setParseFile(myQuestion.second_opt_img);
                dialogImage.loadInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {

                    }
                });
                ImageButton dialogClose = (ImageButton) dialog.findViewById(R.id.dialog_close);
                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
                dialog.show();
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
        iBSO.setParseFile(myQuestion.second_opt_img);
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

        ImageButton foZoomButton = (ImageButton) itemView.findViewById(R.id.question_fo_zoom);
        foZoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(myContext);
                dialog.setContentView(R.layout.dialog_image);
                dialog.setTitle("Image zoom:");
                dialog.setCancelable(true);
                TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                dialogTitle.setText("Option 1");
                ParseImageView dialogImage = (ParseImageView) dialog.findViewById(R.id.dialog_image);
                dialogImage.setParseFile(myQuestion.first_opt_img);
                dialogImage.loadInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {

                    }
                });
                ImageButton dialogClose = (ImageButton) dialog.findViewById(R.id.dialog_close);
                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
                dialog.show();
            }
        });

        ImageButton soZoomButton = (ImageButton) itemView.findViewById(R.id.question_so_zoom);
        soZoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(myContext);
                dialog.setContentView(R.layout.dialog_image);
                dialog.setTitle("Image zoom:");
                dialog.setCancelable(true);
                TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                dialogTitle.setText("Option 2");
                ParseImageView dialogImage = (ParseImageView) dialog.findViewById(R.id.dialog_image);
                dialogImage.setParseFile(myQuestion.second_opt_img);
                dialogImage.loadInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {

                    }
                });
                ImageButton dialogClose = (ImageButton) dialog.findViewById(R.id.dialog_close);
                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });
                dialog.show();
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
        myAdapter.moveToEnd(myQuestion);
    }

    private void FollowUp(int option){
        Intent groupIntent = new Intent(myContext, UserGroupDisplay.class);
        ArrayList<String> groupUserIds = new ArrayList<String>();
        switch(option){
            case FIRST_OPTION:
                for (ParseUser user : myQuestion.voted_yes) {
                    groupUserIds.add(user.getObjectId());
                }
                break;

            case SECOND_OPTION:
                for (ParseUser user : myQuestion.voted_no) {
                    groupUserIds.add(user.getObjectId());
                }
                break;
        }
        groupIntent.putExtra("qid", myQuestion.id);
        groupIntent.putExtra("groupUsersIds", groupUserIds);
        groupIntent.putExtra("option", option);
        groupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(groupIntent);
    }

    private void LoadFollowups(ArrayList<Object> followups){
        Log.d("comments", followups.toString());
        FollowupAdapter ca = new FollowupAdapter(myContext, followups, myQuestion);
        followupsView.setAdapter(ca);
        setTotalHeight(followupsView);
        followupsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.insertBelow((Question)parent.getItemAtPosition(position), getAdapterPosition());
                myAdapter.ChangeFocus(getAdapterPosition()+1);
            }
        });
    }

    private void LoadShouts( ArrayList<Object> comments){
        if(!comments.isEmpty()) {
            if(!(comments.get(comments.size()-1) instanceof Filler))
                comments.add(new Filler(Filler.TYPE_COMMENT));
                //comments.add(new Filler(Filler.TYPE_PAGER));
            int scrollAmount = itemView.getHeight()-itemView.findViewById(R.id.extra_shouts_button).getHeight();
            myAdapter.ScrollBy(getAdapterPosition(), scrollAmount);
        }
        else{
            comments.add(new Filler(Filler.TYPE_COMMENT));
        }
        Log.d("comments", comments.toString());
        CommentsAdapter ca = new CommentsAdapter(myContext, comments, myQuestion);
        ca.setDataSetChangeListener(new OnDataSetChangeListener() {
            @Override
            public void onDataChanged() {
                setTotalHeight(shoutsView);
            }
        });
        shoutsView.setAdapter(ca);
        ca.notifyDataSetChanged();
    }

    public static void setTotalHeight(ListView listView) {
        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View mView = adapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void resetHeight(ListView listView){
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = 0;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
