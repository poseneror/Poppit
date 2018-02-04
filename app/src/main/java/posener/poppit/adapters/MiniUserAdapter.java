package posener.poppit.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.views.RoundProfilePictureView;

/**
 * Created by Or on 23/06/2015.
 */


public class MiniUserAdapter extends ArrayAdapter<ParseUser> {
    public MiniUserAdapter(Context context, ArrayList<ParseUser> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParseUser user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_miniuser, parent, false);
        }
        // Lookup view for data population
        RoundProfilePictureView profilePicture = (RoundProfilePictureView) convertView.findViewById(R.id.li_profile_picture);

        // Populate the data into the template view using the data object
        try {
            user = user.fetchIfNeeded();
        }
        catch (ParseException e)
        {

        }

        profilePicture.setProfileId(user.get("facebook_id").toString());
        // Return the completed view to render on screen
        return convertView;
    }
}
