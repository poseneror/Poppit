package posener.poppit.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.DividerItemDecoration;
import posener.poppit.R;
import posener.poppit.adapters.UsersAdapter;
import posener.poppit.interfaces.MainActivityComm;
import posener.poppit.objects.FeedItem;


public class UserGroupDisplay extends ActionBarActivity {

    private ArrayList<Object> groupUsersList;
    private RecyclerView groupUsersLV;
    private Intent intent;
    private ArrayList<String> userIds;
    private String qid;
    private int option;
    private static final int FIRST_OPTION = 0;
    private static final int SECOND_OPTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_group_display);
        groupUsersLV = (RecyclerView) findViewById(R.id.user_group_display_listView);
        groupUsersLV.setLayoutManager(new LinearLayoutManager(this));
        groupUsersList = new ArrayList<Object>();
        intent = getIntent();
        qid = intent.getStringExtra("qid");
        option = intent.getIntExtra("option", FIRST_OPTION);
        userIds = intent.getStringArrayListExtra("groupUsersIds");
        Log.d("userIdArray - ", userIds.toString());
        for (String userId : userIds){
            ParseUser uObject = new ParseUser();
            try {
                uObject = ParseUser.getQuery().get(userId);
            }
            catch (ParseException e){
                Log.d("userGroup fetch - ", e.getMessage());
            }
            Log.d("user", uObject.toString());
            groupUsersList.add(uObject);
        }
        UsersAdapter usersAdapter = new UsersAdapter(groupUsersList, this);
        groupUsersLV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        groupUsersLV.setItemAnimator(new DefaultItemAnimator());
        groupUsersLV.setAdapter(usersAdapter);
    }

    public void Transfer(View v){
        Intent inflate = new Intent(this, MainActivity.class);
        inflate.putExtra("inflate_id", qid);
        inflate.putExtra("inflate_recipients", userIds);
        inflate.putExtra("inflate_option", option);
        inflate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        inflate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        inflate.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(inflate);
        finish();

    }
}
