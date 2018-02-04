package posener.poppit.activities;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import posener.poppit.MyApplication;
import posener.poppit.adapters.MyFragmentAdapter;
import posener.poppit.fragments.FeedsFragment;
import posener.poppit.fragments.ProfileFragment;
import posener.poppit.interfaces.MainActivityComm;
import posener.poppit.objects.Question;
import posener.poppit.R;
import posener.poppit.fragments.FriendsSelector;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, MainActivityComm {

    TextView saved_user;
    ActionBar actionBar;
    ViewPager viewPager = null;
    private MenuItem searchItem;
    private SearchView searchView;

    String[] tabs = { "Feeds", "Popit", "Profile"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // menu:
        viewPager = (ViewPager) findViewById(R.id.fragment_container);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyFragmentAdapter(fragmentManager));
        viewPager.setOffscreenPageLimit(2);
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (String tab : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab)
                    .setTabListener(this));
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                actionBar.show();
                if(position==1){
                    MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
                    if (adapter.getRegisteredFragment(1) instanceof FriendsSelector){
                        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    }

                }
                else{
                    searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("new intent", "called");
        if(intent.hasExtra("preview")){
            Log.d("showing preview", "has it" + intent.getStringExtra("preview"));
            String qid = intent.getStringExtra("preview");
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
            ParseObject question = new ParseObject("Question");
            try {
                question = query.get(qid);
            }catch (ParseException e){
                Log.d("Question - ", e.getMessage());
            }
            ApplyNewQuestion(new Question(question));
        }
        if(intent.hasExtra("preview_answer")){
            Log.d("showing preview_answer", "has it" + intent.getStringExtra("preview_answer"));
            String qid = intent.getStringExtra("preview_answer");
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
            ParseObject question = new ParseObject("Question");
            try {
                question = query.get(qid);
            }catch (ParseException e){
                Log.d("Question - ", e.getMessage());
            }
            ApplyNewAnswer(new Question(question));
        }
        if(intent.hasExtra("inflate_id")){
            Log.d("inflating for", intent.getStringExtra("inflate_id"));
            String qid = intent.getStringExtra("inflate_id");
            ParseObject question = new ParseObject("Question");
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
            try {
                question = query.get(qid);
            }catch (ParseException e){
                Log.d("Question - ", e.getMessage());
            }
            ArrayList<ParseUser> recipients = new ArrayList<>();
            ArrayList<String> userIds = intent.getStringArrayListExtra("inflate_recipients");
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
                recipients.add(uObject);
            }
            int option = intent.getIntExtra("option", 0);
            InflateFollowUp(question, recipients, option);
            viewPager.setCurrentItem(1);
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onTabSelected(Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void ViewPostedQuestion(Question question) {
        MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
        FeedsFragment feedsFragment = (FeedsFragment) adapter.getRegisteredFragment(0);
        feedsFragment.PutNewQuestion(question);
        Log.d("sending question to feeds - ", question.content);
        viewPager.setCurrentItem(0);
    }

    private void ApplyNewQuestion(Question question){
        MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
        FeedsFragment feedsFragment = (FeedsFragment) adapter.getRegisteredFragment(0);
        feedsFragment.PutNewQuestion(question);
    }

    private void ApplyNewAnswer(Question question){
        MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
        ProfileFragment profileFragment = (ProfileFragment) adapter.getRegisteredFragment(2);
        profileFragment.PutNewAnswer(question);
    }

    private void InflateFollowUp(ParseObject question, ArrayList<ParseUser> recipients, int option){
        MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
        adapter.SwitchToFollowUp(question, recipients, option);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public void SetUnread(int newQuestions) {
        if(newQuestions == 0){
            actionBar.getTabAt(0).setText("Feeds");
        }
        else {
            actionBar.getTabAt(0).setText("Feeds (" + newQuestions + ")");
        }
    }

    @Override
    public void InflateWithRecipients(ArrayList<ParseUser> recipients){
        MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
        adapter.SwitchToInflater(recipients);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }
    @Override
    public void SelectFriends(ArrayList<ParseUser> recipients){
        MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
        adapter.SwitchToFriendsSelector(recipients);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public void ResetSearch(){
        searchView.setQuery("", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MyFragmentAdapter adapter = (MyFragmentAdapter) viewPager.getAdapter();
                FriendsSelector selectorFragment = (FriendsSelector) adapter.getRegisteredFragment(1);
                selectorFragment.setFilter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            case R.id.menu_search:
                searchView.setIconified(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
