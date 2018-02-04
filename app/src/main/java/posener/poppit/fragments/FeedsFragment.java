package posener.poppit.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import posener.poppit.DividerItemDecoration;
import posener.poppit.MyApplication;
import posener.poppit.R;
import posener.poppit.activities.MainActivity;
import posener.poppit.adapters.FeedsAdapter;
import posener.poppit.interfaces.MainActivityComm;
import posener.poppit.interfaces.OnFocusRequestListener;
import posener.poppit.objects.Question;


/**
 * Created by Or on 06/06/2015.
 */
public class FeedsFragment extends Fragment {
    List<ParseObject> newFeeds;
    FeedsAdapter myFeedsAdapter;
    ProgressBar spinner;
    ArrayList<Object> myFeeds = new ArrayList<Object>();
    MainActivityComm comm;
    public LinearLayoutManager lm;
    private RecyclerView FeedsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_re_feeds, container, false);
        FeedsView = (RecyclerView) v.findViewById(R.id.feeds_review);
        lm = new LinearLayoutManager(getActivity());
        FeedsView.setLayoutManager(lm);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner = (ProgressBar) getActivity().findViewById(R.id.feeds_loading_progress);
        new RemoteDataTask().execute();
        Button testPush = (Button) getActivity().findViewById(R.id.test_push);
        testPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("user", ParseUser.getCurrentUser());
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery);
                JSONObject jpush = new JSONObject();
                try {
                    jpush.put("type", "question");
                    jpush.put("question", "9D8eAI7VGh");
                }catch (JSONException e){
                    Log.d("JSON - ", e.getMessage());
                }
                push.setData(jpush);
                try {
                    push.send();
                }catch (ParseException e){

                }
            }
        });
        comm = (MainActivityComm) getActivity();
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setIndeterminate(true);
            spinner.setVisibility(View.VISIBLE);
            FeedsView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            ParseQuery<ParseObject> newFeedsQuery = new ParseQuery<ParseObject>("Questions");
            newFeedsQuery.whereEqualTo("recipients", currentUser);
            newFeedsQuery.whereNotEqualTo("user_id", currentUser);
            //newFeedsQuery.whereNotEqualTo("first_opt_users", currentUser);
            //newFeedsQuery.whereNotEqualTo("second_opt_users", currentUser);
            newFeedsQuery.orderByDescending("_created_at");
            try {
                newFeeds = newFeedsQuery.find();
            }
            catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for (int i = 0; i < newFeeds.size(); i++) {
                myFeeds.add(new Question(newFeeds.get(i)));
            }
            myFeedsAdapter = new FeedsAdapter(myFeeds, getActivity());
            //FeedsView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            FeedsView.setAdapter(myFeedsAdapter);
            FeedsView.setItemAnimator(new DefaultItemAnimator());
            spinner.setVisibility(View.GONE);
            FeedsView.setVisibility(View.VISIBLE);
            myFeedsAdapter.setOnFocusRequestListener(new OnFocusRequestListener() {
                @Override
                public void changeFocus(int position) {
                    //FeedsView.smoothScrollToPosition(position);
                }

                @Override
                public void scrollBy(int position, int amount) {
                    //FeedsView.scrollBy(0, FeedsView.getChildAt(0).getTop() + amount);
                }
            });
            comm.SetUnread(myFeedsAdapter.unAnsweredCount());
        }
    }

    public void PutNewQuestion(Question question){
        Log.d("recived question - ", question.content);
        myFeeds.add(question);
        comm.SetUnread(myFeedsAdapter.unAnsweredCount());
        myFeedsAdapter.insert(question);
        myFeedsAdapter.notifyDataSetChanged();

    }
}
