package posener.poppit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.objects.Comment;
import posener.poppit.objects.CommentItem;
import posener.poppit.objects.Filler;
import posener.poppit.objects.FollowupItem;
import posener.poppit.objects.Question;

/**
 * Created by Or on 19/06/2015.
 */
public class FollowupAdapter extends ArrayAdapter<Object> {
    public static final int TYPE_FOLLOWUP = 0;
    public static final int TYPE_FILLER = 1;
    private Question myQuestion;
    private ArrayList<Object> myFollowups;

    public FollowupAdapter(Context context, ArrayList<Object> followups, Question question) {
        super(context, 0, followups);
        myQuestion = question;
        myFollowups = followups;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Item num " + position, "TYPE " + this.getItemViewType(position));
        if (convertView == null) {
            if(this.getItemViewType(position) == TYPE_FOLLOWUP){
                Log.d("getView - ", "COMMENT");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.followup_item, parent, false);
                new FollowupItem(convertView, getContext(), this, myQuestion)
                        .bindFollowup((Question) getItem(position));
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.getItem(position) instanceof Filler) {
            return TYPE_FILLER;
        }
        else if (this.getItem(position) instanceof Question){
            return TYPE_FOLLOWUP;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
