package posener.poppit.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.activities.UserGroupDisplay;
import posener.poppit.handlers.SoundPlayer;
import posener.poppit.handlers.TimeHandler;
import posener.poppit.objects.FeedItem;
import posener.poppit.views.RoundProfilePictureView;
import posener.poppit.objects.Question;

/**
 * Created by Or on 28/06/2015.
 */

public class QuestionService extends Service {
    LayoutInflater li;
    WindowManager wm;
    LinearLayout detailsV, answerV;
    View itemView;
    TextView tvName, tvTime, tvContent;
    RoundProfilePictureView profilePictureView;
    String qid;
    Question myQuestion;
    int mNotificationId = 001;

    private static final int FIRST_OPTION = 0;
    private static final int SECOND_OPTION = 1;

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT |
                        WindowManager.LayoutParams.WRAP_CONTENT |
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        itemView = li.inflate(R.layout.service_question, null);
        wm.addView(itemView, params);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();/*
        if(itemView!=null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(itemView);
        }*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("start command", "called!");
        qid = intent.getStringExtra("question");
        int option = intent.getIntExtra("vote", Question.FIRST_OPTION);
        myQuestion = Question.GetQuestion(qid);
        String notID = intent.getStringExtra("notID");
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notID, mNotificationId);
        Vote(option);

    /*
        qid = intent.getStringExtra("question");
        Log.d("QID - ", qid);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
        ParseObject question = new ParseObject("Question");
        try {
            question = query.get(qid);
        }catch (ParseException e){
            Log.d("Question - ", e.getMessage());
        }
        try {
            question.fetchIfNeeded();
            question.getParseUser("user_id").fetchIfNeeded();
        }catch (ParseException e) {
        }
        myQuestion = new Question(question);
        updateQuestion();*/
        return START_STICKY;
    }

    public void updateQuestion() {
        tvName = (TextView) itemView.findViewById(R.id.li_q_publisher);
        tvTime = (TextView) itemView.findViewById(R.id.li_q_date);
        tvContent = (TextView) itemView.findViewById(R.id.li_q_content);
        profilePictureView = (RoundProfilePictureView) itemView.findViewById(R.id.li_q_profile_picture);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Dismiss();
            }
        }, 5000);

        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.removeCallbacksAndMessages(null);
            }
        });

        if (myQuestion.publisher != null) {
            try {
                myQuestion.publisher = myQuestion.publisher.fetch();
                tvName.setText(myQuestion.publisher.getString("name"));
                profilePictureView.setProfileId(myQuestion.publisher.get("facebook_id").toString());
            } catch (ParseException e) {
                Log.d("Parse - ", e.getMessage());
            }
        }

        tvTime.setText(TimeHandler.When(myQuestion.publish_time));
        tvContent.setText(myQuestion.content);

        ViewFlipper vf = (ViewFlipper) itemView.findViewById(R.id.modules_flipper);

        switch (myQuestion.type) {
            case Question.TYPE_YN:
                SetYesNoModule();
                vf.setDisplayedChild(0);
                break;
            case Question.TYPE_CUSTOM:
                SetCustomModule();
                vf.setDisplayedChild(1);
                break;
            case Question.TYPE_IMAGE:
                SetImageModule();
                vf.setDisplayedChild(2);
                break;
        }
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

    private void SetImageModule() {
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
    }

    private void Vote(int option){
        switch (option){
            case Question.FIRST_OPTION:
                myQuestion.VoteFirst();
                break;
            case Question.SECOND_OPTION:
                myQuestion.VoteSecond();
                break;
        }
        SoundPlayer.playSound(getApplicationContext(), SoundPlayer.BALLOON_POP);
        Dismiss();
    }

    public void Dismiss(){
        stopSelf();
    }

}
