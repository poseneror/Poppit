package posener.poppit.objects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import posener.poppit.R;
import posener.poppit.adapters.UsersAdapter;
import posener.poppit.handlers.TimeHandler;
import posener.poppit.interfaces.OnItemClickListener;
import posener.poppit.views.RoundProfilePictureView;

/**
 * Created by Or on 21/07/2015.
 */
public class UserItem extends RecyclerView.ViewHolder implements View.OnClickListener{
    private ParseUser myUser;
    TextView lastQuestion;
    public TextView tvName;
    public TextView tvTime;
    public TextView tvContent;
    public RoundProfilePictureView profilePictureView;
    private int moduleType;
    private Context myContext;
    private UsersAdapter myAdapter;
    private OnItemClickListener myItemClickListener;

    public UserItem(View itemView, Context context, UsersAdapter adapter, OnItemClickListener clickListener) {
        super(itemView);
        myContext = context;
        myAdapter = adapter;
        myItemClickListener = clickListener;
    }

    public void bindFiller(String text) {
        TextView dividerText = (TextView) itemView.findViewById(R.id.seperator_text);
        dividerText.setText(text);
    }

    public void bindUser(ParseUser user) {
        itemView.setClickable(true);
        itemView.setOnClickListener(this);
        try{
            user.fetch();
        }catch (ParseException e){

        }
        tvName = (TextView) itemView.findViewById(R.id.li_fname);
        profilePictureView = (RoundProfilePictureView) itemView.findViewById(R.id.li_profile_picture);
        lastQuestion = (TextView) itemView.findViewById(R.id.li_last_question);
        tvName.setText(user.getString("name"));
        profilePictureView.setProfileId(user.get("facebook_id").toString());
        lastQuestion.setText("last seen " + TimeHandler.When(user.getUpdatedAt()));
    }

    @Override
        public void onClick(View v) {
        if(myItemClickListener != null) {
            Log.d("UserClicked pos - ", "" + getAdapterPosition());
            myItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
