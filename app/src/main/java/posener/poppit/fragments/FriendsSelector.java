package posener.poppit.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import posener.poppit.DividerItemDecoration;
import posener.poppit.adapters.UsersAdapter;
import posener.poppit.interfaces.MainActivityComm;
import posener.poppit.adapters.MiniUserAdapter;
import posener.poppit.interfaces.OnItemClickListener;
import posener.poppit.objects.Question;
import posener.poppit.R;

/**
 * Created by Or on 06/06/2015.
 */
public class FriendsSelector extends Fragment {

    EditText friendsSearch;
    GridView recipientsGrid;
    RecyclerView friendsView;
    ArrayList<ParseUser> recipients_list;
    MainActivityComm comm;
    MiniUserAdapter miniUsersAdapter;
    UsersAdapter usersAdapter;
    Button continueButton;
    TextView searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends_selector, container, false);
        friendsView = (RecyclerView) v.findViewById(R.id.friends_listView);
        friendsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        comm = (MainActivityComm) getActivity();
        searchText = (TextView) getActivity().findViewById(R.id.who_text);
        recipientsGrid = (GridView) getActivity().findViewById(R.id.recipients_grid);
        continueButton = (Button) getActivity().findViewById(R.id.continu_btn);
        recipients_list = new ArrayList<ParseUser>();
        miniUsersAdapter = new MiniUserAdapter(getActivity(), new ArrayList<ParseUser>());
        recipientsGrid.setAdapter(miniUsersAdapter);
        recipientsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeRecipient(miniUsersAdapter.getItem(position));
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.InflateWithRecipients(recipients_list);
            }
        });

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("friends")) {
            List<ParseUser> friendsList = currentUser.getList("friends");
            ArrayList<Object> friendsArray = new ArrayList<Object>();
            for (int i = 0; i < friendsList.size(); i++) {
                friendsArray.add(friendsList.get(i));
            }
            usersAdapter = new UsersAdapter(friendsArray, getActivity());
            friendsView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            friendsView.setItemAnimator(new DefaultItemAnimator());
            usersAdapter.SetOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ParseUser selected_friend = (ParseUser) usersAdapter.myFilteredFriends.get(position);

                    if (!usersAdapter.getFilter().equals(""))
                        comm.ResetSearch();
                    addRecipient(selected_friend);
                }});
            friendsView.setAdapter(usersAdapter);
            usersAdapter.getFilter().filter("");
        }
    }

    private class LoadFriendsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loading progress start
        }

        @Override
        protected Void doInBackground(Void... params) {
            //server requests

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //items
        }
    }

    public void setFilter(String name){
        if(name.isEmpty()){
            searchText.setText(R.string.recipients_hint);
        }
        else {
            searchText.setText("Looking for " + name);
        }
        usersAdapter.getFilter().filter(name);

    }

    public void addRecipient(ParseUser user) {
        miniUsersAdapter.add(user);
        recipients_list.add(user);
        miniUsersAdapter.notifyDataSetChanged();
        usersAdapter.RemoveUser(user);
    }

    public void removeRecipient(ParseUser user) {
        miniUsersAdapter.remove(user);
        recipients_list.remove(user);
        miniUsersAdapter.notifyDataSetChanged();
        usersAdapter.AddUser(user);
    }

    public void setRecipients(ArrayList<ParseUser> recipients){
        miniUsersAdapter.addAll(recipients);
        recipients_list = recipients;
        miniUsersAdapter.notifyDataSetChanged();
        for(ParseUser rec : recipients){
            usersAdapter.RemoveUser(rec);
        }
    }

    public void ClearFields() {
        recipients_list.clear();
        friendsSearch.setText("");
        miniUsersAdapter.clear();
        miniUsersAdapter.notifyDataSetChanged();
    }

    public void SeeQuestion(Question question) {
        Log.d("Sending question to activity - ", question.content);
        comm.ViewPostedQuestion(question);
    }

    public void onSwitchToNextFragment(){

    }
}

