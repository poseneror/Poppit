package posener.poppit.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import posener.poppit.DividerItemDecoration;
import posener.poppit.adapters.FeedsAdapter;
import posener.poppit.objects.Filler;
import posener.poppit.objects.Question;
import posener.poppit.R;
import posener.poppit.interfaces.SoftKeyboardListener;

/**
 * Created by Or on 06/06/2015.
 */
public class ProfileFragment extends Fragment implements SoftKeyboardListener {


    private List<ParseObject> questionList;
    private ArrayList<Object> questionsArray;
    private FeedsAdapter myRecentActivityAdapter;
    private RecyclerView myRecentView;
    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myRecentView = (RecyclerView) getActivity().findViewById(R.id.recent_activity_re_view);
        myRecentView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //friendsGrid = (GridView) getActivity().findViewById(R.id.friends_grid);
        new ProfileDataTask().execute();

    }

    @Override
    public void onShown() {

    }

    @Override
    public void onHidden() {

    }

    private class ProfileDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //set loading progress here
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Questions");
            query.whereEqualTo("user_id", ParseUser.getCurrentUser());
            query.orderByDescending("_created_at");
            try {
                questionList = query.find();
            }
            catch (ParseException e) {
                Log.e("profile questions fetching - ", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            questionsArray = new ArrayList<Object>();
            questionsArray.add(new Filler(Filler.TYPE_PROFILE));
            for (int i = 0; i < questionList.size(); i++) {
                questionsArray.add(new Question(questionList.get(i)));
            }
            myRecentActivityAdapter = new FeedsAdapter(questionsArray, getActivity());
            //myRecentView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            myRecentView.setAdapter(myRecentActivityAdapter);
            myRecentView.setItemAnimator(new DefaultItemAnimator());
            myRecentView.setVisibility(View.VISIBLE);
            //put data in it's place
        }
    }

    public void PutNewAnswer(Question question){
        Log.d("recived answer - ", question.content);
        myRecentActivityAdapter.update(question);
        myRecentActivityAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
