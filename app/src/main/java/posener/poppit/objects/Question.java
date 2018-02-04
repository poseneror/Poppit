package posener.poppit.objects;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ListView;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Or on 19/06/2015.
 */
public class Question {
    public String id;
    public ParseUser publisher;
    public String content;
    public Date publish_time;
    public ParseObject question;
    public List<ParseUser> voted_yes;
    public List<ParseUser> voted_no;
    public List<ParseUser> recipients;
    public List<Question> follow_ups;
    public String fo = "";
    public String so = "";
    public int total_votes = 0;

    public boolean tieBreaker = false;
    public boolean solved = false;

    public ParseFile first_opt_img;
    public ParseFile second_opt_img;
    public int type = 0;
    public static final int FIRST_OPTION = 100;
    public static final int SECOND_OPTION = 200;

    public static final int TYPE_YN = 0;
    public static final int TYPE_CUSTOM = 1;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_ANSWERED = 10;
    public static final int TYPE_ANSWERED_IMG = 13;

    public Question(ParseObject q) {
        this.question = q;
        try{
            q.fetchIfNeeded();
        }catch (ParseException e){

        }
        this.id = question.getObjectId();
        try {
            this.publisher = question.getParseUser("user_id").fetchIfNeeded();
        }catch (ParseException e){

        }
        this.content = question.get("content").toString();
        this.recipients = question.getList("recipients");
        this.voted_yes = question.getList("first_opt_users");
        if(voted_yes == null) {
            voted_yes = new ArrayList<ParseUser>();
        }
        this.voted_no = question.getList("second_opt_users");
        if(voted_no == null) {
            voted_no = new ArrayList<ParseUser>();
        }

        total_votes = voted_yes.size() + voted_no.size();

        if(total_votes == recipients.size()){
            solved = true;
        }

        if(!solved && voted_no.size() == voted_yes.size() && total_votes != 0){
            tieBreaker = true;
        }

        this.publish_time = question.getCreatedAt();
        if(question.has("type")){
            this.type = question.getInt("type");
        }

        else {
            this.type = 0;
        }

        if(question.has("first_option")){
            this.fo = question.getString("first_option");
        }

        if(question.has("second_option")){
            this.so = question.getString("second_option");
        }

        if(question.has("first_option_img")){
            this.first_opt_img = question.getParseFile("first_option_img");
        }

        if(question.has("second_option_img")){
            this.second_opt_img = question.getParseFile("second_option_img");
        }
    }
    public void VoteFirst(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(!UserVoted()){
            question.add("first_opt_users", currentUser);
            question.saveInBackground();
            voted_yes.add(currentUser);
            Log.d("Button - ", "yes clicked! for q: " + content +" user vote saved!");
        }
        else
        {
            Log.d("Button - ", "user already voted! for q: " + content);
        }
        pushAnswer(currentUser, "YES");

    }
    public void VoteSecond(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(!UserVoted()) {
            question.add("second_opt_users", currentUser);
            question.saveInBackground();
            voted_no.add(currentUser);
            Log.d("Button - ", "no clicked! for q: " + content + " user vote saved!");
        }
        else {
            Log.d("Button - ", "user already voted! for q: " + content);
        }
        pushAnswer(currentUser, "NO");

    }

    public boolean UserVoted(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(voted_yes != null){
            if(voted_yes.contains(currentUser)) {
                return true;
            }
        }
        if(voted_no != null){
            if(voted_no.contains(currentUser)){
                return true;
            }
        }
        return false;
    }

    public boolean IsPublisher(){
        if(publisher == ParseUser.getCurrentUser()){
            return true;
        }
        else{
            return false;
        }
    }
    public void pushAnswer(ParseUser user, String an){
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", publisher);
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        JSONObject jpush = new JSONObject();
        try {
            jpush.put("type", "answer");
            jpush.put("question", id);
            jpush.put("user", user.getObjectId());
            jpush.put("answer", an);
        }catch (JSONException e){
            Log.d("JSON - ", e.getMessage());
        }

        push.setData(jpush);
        push.sendInBackground();
        Log.d("Push sent for QID - ", id);
    }

    public ArrayList<Object> getFollowups(){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
        query.whereEqualTo("followup", question);
        List<ParseObject> qo = Collections.emptyList();
        ArrayList<Object> followups = new ArrayList<Object>();
        try{
            qo = query.find();
        }catch (ParseException e){
            Log.d("followup fetching - ", e.getMessage());
        }
        for (int i = 0; i < qo.size(); i++) {
            followups.add(new Question(qo.get(i)));
        }
        return followups;
    }
    public static Question GetQuestion(String qid){
        Question myQuestion;
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
        ParseObject question = new ParseObject("Question");
        try {
            query.include("user_id");
            question = query.get(qid);
            return myQuestion = new Question(question);
        }catch (ParseException e){
            Log.d("GetQuestion - ", e.getMessage());
            return new Question(question);
        }
    }

    public ArrayList<Object> GetComments(){
        List<ParseObject> comments;
        ParseQuery<ParseObject> commentsQuery = new ParseQuery<ParseObject>("Comments");
        commentsQuery.whereEqualTo("question", question);
        commentsQuery.orderByAscending("_created_at");
        try {
            comments = commentsQuery.find();
        }
        catch (ParseException e) {
            comments = Collections.emptyList();
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        ArrayList<Object> commentArrayList = new ArrayList<>();
        for (ParseObject c : comments){
            commentArrayList.add(new Comment(c));
        }
        return commentArrayList;
    }
    public int getFirstVotePercent(){
        float firstVotes = voted_yes.size();
        float totalVotes = total_votes;
        int percent = (int)((firstVotes * 100.0f) / totalVotes);
        return percent;
    }
    public int getSecondVotePercent(){
        float secondVotes = voted_no.size();
        float totalVotes = total_votes;
        int percent = (int)((secondVotes * 100.0f) / totalVotes);
        return percent;
    }

}
