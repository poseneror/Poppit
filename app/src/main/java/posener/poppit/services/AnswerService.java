package posener.poppit.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import posener.poppit.objects.Question;
import posener.poppit.R;

/**
 * Created by Or on 28/06/2015.
 */
public class AnswerService extends Service {
    LayoutInflater li;
    WindowManager wm;
    View myview;
    TextView userTv, contentTv, answerTV;
    ProfilePictureView userProfile;
    String qid, uid, answer;
    Question q;

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT |
                        WindowManager.LayoutParams.WRAP_CONTENT |
                        //WindowManager.LayoutParams.TYPE_INPUT_METHOD |
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,// | WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        myview = li.inflate(R.layout.service_answer, null);
        myview.setAlpha(0.0f);
        myview.animate().translationY(myview.getHeight()).alpha(1.0f);
        //myview.setY(-myview.getHeight());
        //myview.animate().translationY(myview.getHeight()).setDuration(5000);
        wm.addView(myview, params);

        userTv = (TextView) myview.findViewById(R.id.user_name);
        contentTv = (TextView) myview.findViewById(R.id.question_text);
        answerTV = (TextView) myview.findViewById(R.id.yes_no);
        userProfile = (ProfilePictureView) myview.findViewById(R.id.user_profile_picture);
        Log.d("answer sevice called", "!!!");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myview!=null){
            wm.removeView(myview);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        qid = intent.getStringExtra("question");
        uid = intent.getStringExtra("user");
        answer = intent.getStringExtra("answer");
        updateAnswer();
        return START_STICKY;
    }

    public void updateAnswer(){
        Log.d("Calling answer for QID - ", qid);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
        ParseObject question = new ParseObject("Question");
        ParseQuery<ParseUser> uQ = ParseUser.getQuery();
        ParseUser user = new ParseUser();
        try {
            question = query.get(qid);
            user = uQ.get(uid);
        }catch (ParseException e){
            Log.d("Question - ", e.getMessage());
        }
        q = new Question(question);
        userTv.setText(user.getString("name"));
        contentTv.setText(q.content);
        userProfile.setProfileId(user.get("facebook_id").toString());
        answerTV.setText(answer);
        if(answer.equals("YES")){
            answerTV.setTextColor(getResources().getColor(R.color.poppit_green));
        }
        else{
            answerTV.setTextColor(getResources().getColor(R.color.poppit_red));
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Dismiss();
            }
        }, 5000);
    }
    public void Dismiss(){
        stopSelf();
    }

}
