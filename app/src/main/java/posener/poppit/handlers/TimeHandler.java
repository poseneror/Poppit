package posener.poppit.handlers;


import java.util.Date;

/**
 * Created by Or on 02/07/2015.
 */
public class TimeHandler {
    public static String When(Date date){
        Date now = new Date();
        String when = "";
        long diff = now.getTime() - date.getTime();

        long seconds = diff / 1000 % 60;
        long minutes = diff / (60 * 1000) % 60;
        long hours = diff / (60 * 60 * 1000) % 24;
        long days = diff / (24 * 60 * 60 * 1000);

        if(diff / (60 * 1000) < 1){
            when = "a few seconds ago";
        }
        else if(diff / (60*1000) > 1 && diff / (60*1000) < 9){
            when = "a few minutes ago";
        }
        else if(diff / (60 * 60 * 1000) < 1){
            when = minutes + " minutes ago";
        }
        else if(diff / (60 * 60 * 1000) < 24){
            when = hours + " hours ago";
        }
        else if(diff / (24 * 60 * 60 * 1000) == 1){
            when = "yesterday";
        }
        else if(diff / (24 * 60 * 60 * 1000) < 5){
            when = days + " days ago";
        }
        else{
            when = date.toLocaleString();
        }
        return when;
    }
}
