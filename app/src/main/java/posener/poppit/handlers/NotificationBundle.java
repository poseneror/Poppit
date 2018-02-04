package posener.poppit.handlers;

import posener.poppit.objects.Question;

/**
 * Created by Or on 17/08/2015.
 */
public class NotificationBundle {

    public int notificationID;
    public Question question;

    public NotificationBundle(int id, Question q){
        notificationID = id;
        question = q;
    }
}
