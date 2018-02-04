package posener.poppit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import posener.poppit.R;

/**
 * Created by Or on 30/06/2015.
 */
public class UserSearchAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<ParseUser> resultList = new ArrayList<ParseUser>();

    public UserSearchAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public ParseUser getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_autocomplete_user, parent, false);
        }
        TextView username = (TextView) convertView.findViewById(R.id.ac_user_name);
        username.setText(getItem(position).getString("name"));
        ProfilePictureView profilePicture = (ProfilePictureView) convertView.findViewById(R.id.ac_profile_picture);
        profilePicture.setProfileId(getItem(position).get("facebook_id").toString());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<ParseUser> users = findUsers(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = users;
                    filterResults.count = users.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<ParseUser>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<ParseUser> findUsers(Context context, String username) {
        // GoogleBooksProtocol is a wrapper for the Google Books API
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friendsList = currentUser.getList("friends");

        return friendsList;
    }
}