package posener.poppit.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import posener.poppit.R;


public class LoginActivity extends ActionBarActivity {

    private ViewFlipper loginFlipper;
    private ProgressBar login_spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);

        loginFlipper = (ViewFlipper) findViewById(R.id.login_view_flipper);
        login_spinner = (ProgressBar) findViewById(R.id.login_loading_progress);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            new UserDataUpdateTask().execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginClick(View v) {
        List<String> permissions = Arrays.asList("public_profile", "user_friends");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("Login - ", "Uh oh. The user cancelled the Facebook login.");
                }
                else if (user.isNew()) {
                    Log.d("Login - ", "User signed up and logged in through Facebook!");
                    new UserDataUpdateTask().execute();

                }
                else {
                    Log.d("Login - ", "User logged in through Facebook!");
                    new UserDataUpdateTask().execute();

                }

            }
        });

    }

    private class UserDataUpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            login_spinner.setIndeterminate(true);
            loginFlipper.setDisplayedChild(1);
            Log.d("onpreexecute", "called");
            //set loading progress here
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("doinbackground", "called");
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                            if (jsonObject != null) {
                                try {
                                    ParseUser currentUser = ParseUser.getCurrentUser();
                                    currentUser.put("facebook_id", Long.parseLong(jsonObject.getString("id")));
                                    currentUser.put("name", jsonObject.getString("name"));
                                    if (jsonObject.getString("gender") != null)
                                        currentUser.put("gender", jsonObject.getString("gender"));
                                    try{
                                        currentUser.save();
                                        Log.d("Profile fetching - ", "Saved.");
                                    }catch (ParseException e){
                                        Log.d("Couldn't update user - ", e.getMessage());
                                    }
                                } catch (JSONException e) {
                                    Log.d("Profile fetching - ",
                                            "Error parsing returned user data. " + e);
                                }
                            } else if (graphResponse.getError() != null) {
                                switch (graphResponse.getError().getCategory()) {
                                    case LOGIN_RECOVERABLE:
                                        Log.d("Profile fetching - ",
                                                "Authentication error: " + graphResponse.getError());
                                        break;
                                    case TRANSIENT:
                                        Log.d("Profile fetching - ",
                                                "Transient error. Try again. " + graphResponse.getError());
                                        break;
                                    case OTHER:
                                        Log.d("Profile fetching - ",
                                                "Some other error: " + graphResponse.getError());
                                        break;
                                }
                            }
                        }
                    });
            GraphRequest friends_request = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONArrayCallback() {
                        @Override
                        public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                            if (jsonArray != null) {
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                List<ParseUser> friends_list= new ArrayList<ParseUser>();
                                List<Long> myFriendsIds = new ArrayList<Long>();
                                for(int i=0; i<jsonArray.length(); i++) {
                                    try {
                                        myFriendsIds.add(jsonArray.getJSONObject(i).getLong("id"));
                                    } catch (JSONException e) {
                                    }
                                }
                                ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
                                query.whereContainedIn("facebook_id", myFriendsIds);
                                try{
                                    friends_list = query.find();
                                }
                                catch (ParseException e)
                                {

                                }
                                Log.d("friends: ", friends_list.toString());
                                currentUser.put("friends",friends_list);
                                try{
                                    currentUser.save();
                                    Log.d("Friends fetching - ", "Saved.");
                                }catch (ParseException e){
                                Log.d("Couldn't update user friends - ", e.getMessage());
                                }
                            }
                            else{
                                Log.d("Friends Request - ", "no friends.");
                            }
                        }
                    }
            );
            request.executeAndWait();
            friends_request.executeAndWait();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ContinueToActivity();
            //set loading progress here
        }
    }

    private void ContinueToActivity(){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
