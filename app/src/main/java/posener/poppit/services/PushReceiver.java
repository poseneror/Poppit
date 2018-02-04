package posener.poppit.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.parse.ParseBroadcastReceiver;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import posener.poppit.MyApplication;
import posener.poppit.R;
import posener.poppit.activities.MainActivity;
import posener.poppit.handlers.NotificationID;
import posener.poppit.objects.Question;

/**
 * Created by Or on 27/06/2015.
 */
public class PushReceiver extends ParseBroadcastReceiver {
    Intent resultIntent;
    int mNotificationId = 001;
    String type = "";
    final static String GROUP_KEY_QUESTIONS = "group_key_questions";

    @Override
    public void onReceive(Context context, Intent intent){
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            type = json.getString("type");
            Log.d("Received push type: ",type);
        } catch (JSONException e) {

        }
        if(type.equals("question")) {
                String qid;
                try {
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                    qid = json.getString("question");
                } catch (JSONException e) {
                    qid = "";
                }
                Log.d("question: ", qid);

                if(MyApplication.isActivityVisible()) {
                    Intent preview = new Intent(context, MainActivity.class);
                    preview.putExtra("preview", qid);
                    preview.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    preview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(preview);
                }
                else {
                    Question myQuestion = Question.GetQuestion(qid);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        resultIntent = new Intent(context, MainActivity.class);
                        Intent voteYIntent = new Intent(context, QuestionService.class);
                        voteYIntent.putExtra("question", qid);
                        voteYIntent.putExtra("notID", qid);
                        voteYIntent.putExtra("vote", Question.FIRST_OPTION);
                        voteYIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Intent voteNIntent = new Intent(context, QuestionService.class);
                        voteNIntent.putExtra("question", qid);
                        voteNIntent.putExtra("notID", qid);
                        voteNIntent.putExtra("vote", Question.SECOND_OPTION);
                        voteNIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent foPendingIntent = PendingIntent.getService(context,
                                0, voteYIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent soPendingIntent = PendingIntent.getService(context,
                                0, voteNIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        String name = myQuestion.publisher.getString("name");
                        String question = myQuestion.content;
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        Notification not = builder.setSmallIcon(R.drawable.ic_launcher)
                                .setTicker(name + ": " + question)
                                .setAutoCancel(true)
                                .setContentIntent(resultPendingIntent)
                                .setGroup(GROUP_KEY_QUESTIONS)
                                .setContentTitle(name)
                                .setContentText(question)
                                .build();

                        RemoteViews remoteViews;
                        switch (myQuestion.type){
                            case Question.TYPE_YN:
                                remoteViews = new RemoteViews("posener.poppit",
                                        R.layout.notification_view_yes_no);
                                remoteViews.setOnClickPendingIntent(R.id.not_first_option, foPendingIntent);
                                remoteViews.setOnClickPendingIntent(R.id.not_second_option, soPendingIntent);
                                break;
                            case Question.TYPE_CUSTOM:
                                remoteViews = new RemoteViews("posener.poppit",
                                        R.layout.notification_view_custom);
                                remoteViews.setOnClickPendingIntent(R.id.not_first_option, foPendingIntent);
                                remoteViews.setOnClickPendingIntent(R.id.not_second_option, soPendingIntent);
                                break;
                            case Question.TYPE_IMAGE:
                                remoteViews = new RemoteViews("posener.poppit",
                                        R.layout.notification_view_image);
                                byte[] foData, soData;
                                try {
                                    foData = myQuestion.first_opt_img.getData();
                                    Bitmap foBmp = BitmapFactory.decodeByteArray(foData, 0, foData.length);
                                    remoteViews.setImageViewBitmap(R.id.not_first_option, foBmp);
                                    soData = myQuestion.second_opt_img.getData();
                                    Bitmap soBmp = BitmapFactory.decodeByteArray(soData, 0, soData.length);
                                    remoteViews.setImageViewBitmap(R.id.not_second_option, soBmp);
                                    remoteViews.setOnClickPendingIntent(R.id.not_first_option, foPendingIntent);
                                    remoteViews.setOnClickPendingIntent(R.id.not_second_option, soPendingIntent);
                                }catch (ParseException e){
                                    Log.d("Parse Image - ", e.getMessage());
                                }
                                break;
                            default:
                                remoteViews = new RemoteViews("posener.poppit",
                                        R.layout.notification_view);
                                break;
                        }
                        remoteViews.setTextViewText(R.id.not_username, name);
                        remoteViews.setTextViewText(R.id.not_question, question);
                        not.bigContentView = remoteViews;

                        NotificationManager notificationManager = (NotificationManager) context
                                .getSystemService(context.NOTIFICATION_SERVICE);
                        notificationManager.notify(qid, mNotificationId, not);
                    } else {
                        resultIntent = new Intent(context, MainActivity.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        String name = myQuestion.publisher.getString("name");
                        String question = myQuestion.content;
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setTicker(name + ": " + question)
                                .setAutoCancel(true)
                                .setContentIntent(resultPendingIntent)
                                .setContentText(question)
                                .setSubText(name);
                        NotificationManager notificationManager = (NotificationManager) context
                                .getSystemService(context.NOTIFICATION_SERVICE);
                        notificationManager.notify(qid, mNotificationId, builder.build());
                    }
                }

        }
        if(type.equals("answer")){
            Intent qIntent = new Intent(context, AnswerService.class);//new Intent(context, QuestionDisplay.class);
            String qid = "";
            String uid = "";
            String answer = "";

            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                qid = json.getString("question");
                uid =  json.getString("user");
                answer = json.getString("answer");
            } catch (JSONException e) {

            }
            if(MyApplication.isActivityVisible()) {
                Intent preview = new Intent(context, MainActivity.class);
                preview.putExtra("preview_answer", qid);
                preview.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                preview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(preview);
            }
            else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                resultIntent = new Intent(context, MainActivity.class);

                Question myQuestion = Question.GetQuestion(qid);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                        0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String name = myQuestion.publisher.getString("name");
                String question = myQuestion.content;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Notification not = builder.setSmallIcon(R.drawable.ic_launcher)
                        .setTicker("answer for: " + question)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent).build();
                RemoteViews remoteViews;
                int prog = myQuestion.getFirstVotePercent();


                switch (myQuestion.type){
                    case Question.TYPE_YN:
                        remoteViews = new RemoteViews("posener.poppit",
                                R.layout.notification_view_results_yes_no);

                        break;
                    case Question.TYPE_CUSTOM:
                        remoteViews = new RemoteViews("posener.poppit",
                                R.layout.notification_view_results_yes_no);

                        break;
                    case Question.TYPE_IMAGE:
                        remoteViews = new RemoteViews("posener.poppit",
                                R.layout.notification_view_results_yes_no);
                        remoteViews.setProgressBar(R.id.not_first_bar, 100, prog, false);
                        remoteViews.setProgressBar(R.id.not_second_bar, 100, 100-prog, false);
                        remoteViews.setTextViewText(R.id.not_first_percent, prog+"%");
                        remoteViews.setTextViewText(R.id.not_second_percent, 100-prog+"%");
                        remoteViews.setTextViewText(R.id.not_first_answered, myQuestion.voted_yes.size() + " friends");
                        remoteViews.setTextViewText(R.id.not_second_answered, myQuestion.voted_no.size() + " friends");
                        break;
                    default:
                        remoteViews = new RemoteViews("posener.poppit",
                                R.layout.notification_view_results);
                        break;
                }
                remoteViews.setTextViewText(R.id.not_username, name);
                remoteViews.setTextViewText(R.id.not_question, question);
                not.bigContentView = remoteViews;

                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify(qid, mNotificationId, not);

            } else {
                resultIntent = new Intent(context, MainActivity.class);
                Question myQuestion = Question.GetQuestion(qid);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                        0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                String name = myQuestion.publisher.getString("name");
                String question = myQuestion.content;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker("answer for: " + question)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        .setContentText(question)
                        .setSubText("a friend answered:");
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify(qid, mNotificationId, builder.build());
            }
            }
        }

    }
}
