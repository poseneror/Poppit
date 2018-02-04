package posener.poppit.handlers;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import posener.poppit.objects.Question;

/**
 * Created by Or on 13/08/2015.
 */
public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    private static ArrayList<NotificationBundle> notificationList = new ArrayList<>();

    public static int getNewId() {
        return c.incrementAndGet();
    }

    public static int getId(Question question){
        Log.d("notificationID", "requesting");
        if(notificationExists(question)){
            Log.d(notificationList.toString(), "notification found!");
            return notificationList.get(notificationList.indexOf(question)).notificationID;
        }
        Log.d(notificationList.toString(), "notification not found!");
        int id = getNewId();
        addNotification(id, question);
        return id;
    }
    public static boolean notificationExists(Question question){
        if(!notificationList.isEmpty()){
            for(NotificationBundle notification : notificationList){
                if(notification.question.id == question.id){
                    return true;
                }
            }
        }
        return false;
    }

    public static void addNotification(int id, Question question){
        if(!notificationExists(question)){
            notificationList.add(new NotificationBundle(id, question));
            Log.d("adding ", "notification!");
        }

    }

    public static void removeNotification(Question question) {
        if(!notificationList.isEmpty()){
            for(NotificationBundle notification : notificationList){
                if(notification.question == question){
                    notificationList.remove(notification);
                }
            }
        }
    }
}