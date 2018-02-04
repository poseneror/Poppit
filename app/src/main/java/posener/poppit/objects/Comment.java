package posener.poppit.objects;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Or on 19/06/2015.
 */
public class Comment {
    public String id;
    public ParseUser user;
    public String content;
    public Date date;
    private ParseObject comment;

    public Comment(ParseObject c) {
        this.comment = c;
        this.id = comment.getObjectId();
        this.user = c.getParseUser("user");
        this.content = c.get("content").toString();
        this.date = c.getCreatedAt();
    }

    public static Comment getComment(String qid){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Comments");
        ParseObject c = new ParseObject("Comment");
        try {
            query.include("user");
            c = query.get(qid);
            return new Comment(c);
        }catch (ParseException e){
            Log.d("GetQuestion - ", e.getMessage());
            return new Comment(c);
        }
    }

}
