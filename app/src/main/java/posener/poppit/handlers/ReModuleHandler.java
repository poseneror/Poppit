package posener.poppit.handlers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.activities.UserGroupDisplay;
import posener.poppit.adapters.FeedsAdapter;
import posener.poppit.objects.Question;

/**
 * Created by Or on 19/07/2015.
 */
public class ReModuleHandler {

    private ViewFlipper moduleFlipper;
    private View moduleView;
    private Question q;
    private Context context;
    private FeedsAdapter myAdapter;
    private int type;

    private static final int FIRST_OPTION = 0;
    private static final int SECOND_OPTION = 1;

    public ReModuleHandler(){


    }

    public void SetModule(){
        if(q.UserVoted()){
            SetAnswerModule();
        }
        else {
            switch (type) {
                case Question.TYPE_YN:
                    SetYesNoModule();
                    break;
                case Question.TYPE_CUSTOM:
                    SetCustomModule();
                    break;
            }
        }
    }

    public void SetAnswerModule()
    {
        moduleFlipper.setDisplayedChild(0);
        final TextView UV = (TextView) moduleView.findViewById(R.id.user_vote);
        final TextView YN = (TextView) moduleView.findViewById(R.id.li_q_yes_no);
        final TextView tvYes = (TextView) moduleView.findViewById(R.id.li_q_answered_y);
        final TextView tvNo = (TextView) moduleView.findViewById(R.id.li_q_answered_n);
        final ProgressBar yBar = (ProgressBar) moduleView.findViewById(R.id.li_y_bar);
        final ProgressBar nBar = (ProgressBar) moduleView.findViewById(R.id.li_n_bar);
        final TextView yPercent = (TextView) moduleView.findViewById(R.id.li_y_percent);
        final TextView nPercent = (TextView) moduleView.findViewById(R.id.li_n_percent);
        int yVotes = 0;
        int nVotes = 0;

        if (q.voted_yes != null) {
            yVotes = q.voted_yes.size();
            if(type==Question.TYPE_YN) {
                if (q.voted_yes.contains(ParseUser.getCurrentUser())) {
                    YN.setText("YES");
                    YN.setTextColor(context.getResources().getColor(R.color.poppit_green));
                } else {
                    YN.setText("NO");
                    YN.setTextColor(context.getResources().getColor(R.color.poppit_red));
                }
            }
            if(type==Question.TYPE_CUSTOM){
                if (q.voted_yes.contains(ParseUser.getCurrentUser())) {
                    YN.setText(q.fo);
                    YN.setTextColor(context.getResources().getColor(R.color.poppit_green));
                } else {
                    YN.setText(q.so);
                    YN.setTextColor(context.getResources().getColor(R.color.poppit_red));
                }
            }
            yBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Intent groupIntent = new Intent(context, UserGroupDisplay.class);
                    ArrayList<String> groupUserIds = new ArrayList<String>();
                    for (ParseUser user : q.voted_yes) {
                        groupUserIds.add(user.getObjectId());
                    }
                    groupIntent.putExtra("groupUsersIds", groupUserIds);
                    groupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(groupIntent);
                }
                });

        }
        if(q.voted_no != null) {
            nVotes = q.voted_no.size();
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
        if(type==Question.TYPE_CUSTOM){
            tvYes.setText(q.fo + " (" + yVotes + ")");
            tvNo.setText(q.so + " (" + nVotes + ")");
        }
    }

    public void SetYesNoModule(){
        moduleFlipper.setDisplayedChild(1);
        final Button bYes = (Button) moduleView.findViewById(R.id.module_y_btn);
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(FIRST_OPTION);
            }
        });
        final Button bNo = (Button) moduleView.findViewById(R.id.module_n_btn);
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(SECOND_OPTION);
            }
        });
    }

    public void SetCustomModule(){
        moduleFlipper.setDisplayedChild(2);
        final Button bFO = (Button) moduleView.findViewById(R.id.module_custom_fo);
        bFO.setText(q.fo);
        bFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(FIRST_OPTION);    }
        });
        final Button bSO = (Button) moduleView.findViewById(R.id.module_custom_so);
        bSO.setText(q.so);
        bSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Vote(SECOND_OPTION);     }
        });
    }

    public void Vote(int option){
        switch (option){
            case FIRST_OPTION:
                q.VoteFirst();
                break;
            case SECOND_OPTION:
                q.VoteSecond();
                break;
        }
        if(myAdapter!=null)
            myAdapter.notifyDataSetChanged();
        SoundPlayer.playSound(context, SoundPlayer.BALLOON_POP);
    }
}
