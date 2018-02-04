package posener.poppit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.interfaces.OnItemClickListener;
import posener.poppit.objects.Filler;
import posener.poppit.objects.Question;

import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.parse.ParseUser;

import posener.poppit.objects.UserItem;

/**
 * Created by Or on 31/07/2015.
 */
public class UsersAdapter extends RecyclerView.Adapter<UserItem> implements Filterable {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_EXPANDABLE = 1;
    public static final int TYPE_PROFILE = 100;
    private int type = TYPE_NORMAL;
    private ArrayList<Object> myFeeds;
    public ArrayList<Object> myFilteredFriends;
    private Context myContext;
    public OnItemClickListener myItemClickListener;

    public UsersAdapter(ArrayList<Object> questions, Context context) {
        myFeeds = questions;
        myContext = context;
        myFilteredFriends = myFeeds;
    }

    @Override
    public UserItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user_divider, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user, parent, false);
                break;
        }
        return new UserItem(view, myContext, this, myItemClickListener);
    }

    @Override
    public void onBindViewHolder(UserItem holder, int position) {
        if(getItemViewType(position) == 0) {
            ParseUser user = (ParseUser) myFilteredFriends.get(position);
            holder.bindUser(user);
        }
        else if(getItemViewType(position) == 1) {
            holder.bindFiller("All Friends:");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(myFilteredFriends.get(position) instanceof ParseUser)
            return 0;
        else if(myFilteredFriends.get(position) instanceof Filler)
            return 1;
        return 0;
    }


    @Override
    public int getItemCount() {
        return myFilteredFriends.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d("Filtering - ", constraint.toString());
                FilterResults filterResults = new FilterResults();
                if (constraint.toString().isEmpty()) {
                    ArrayList<Object> users = DefaultUsers();
                    filterResults.values = users;
                    filterResults.count = users.size();
                } else {
                    ArrayList<Object> users = findUsers(constraint);
                    filterResults.values = users;
                    filterResults.count = users.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    myFilteredFriends = (ArrayList<Object>) results.values;
                    notifyDataSetChanged();
                } else {
                    myFilteredFriends.clear();
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    private ArrayList<Object> findUsers(CharSequence username) {
        Log.d("looking for - ", username.toString());
        ArrayList<Object> filteredUsers = new ArrayList<>();
        for (Object feed : myFeeds) {
            if(feed instanceof ParseUser) {
                ParseUser friend = (ParseUser) feed;
                if (friend.getString("name").toLowerCase().contains(username.toString().toLowerCase())) {
                    filteredUsers.add(friend);
                }
            }
        }
        Log.d("found - ", filteredUsers.toString());
        return filteredUsers;
    }

    private ArrayList<Object> DefaultUsers() {
        //myFilteredFriends = myFeeds;
        //myFilteredFriends.add(0, new Filler(Filler.TYPE_DIVIDER));
        return myFeeds;
    }

    public void AddUser(ParseUser user){
        myFeeds.add(0, user);
        notifyItemInserted(0);
    }

    public void moveToEnd(Question question){
        int firstPosition = myFeeds.indexOf(question);
        myFeeds.remove(question);
        myFeeds.add(question);
        notifyItemMoved(firstPosition, myFeeds.indexOf(question));
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        myItemClickListener = mItemClickListener;
    }

    public void RemoveUser(ParseUser user){
        if(myFeeds.contains(user)) {
            int position = myFeeds.indexOf(user);
            myFeeds.remove(position);
            notifyItemRemoved(position);
        }
    }
}
