package posener.poppit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.handlers.TimeHandler;
import posener.poppit.interfaces.OnDataSetChangeListener;
import posener.poppit.objects.Comment;
import posener.poppit.objects.CommentItem;
import posener.poppit.objects.FeedItem;
import posener.poppit.objects.Filler;
import posener.poppit.objects.Question;
import posener.poppit.views.RoundProfilePictureView;

/**
 * Created by Or on 19/06/2015.
 */
public class CommentsAdapter extends ArrayAdapter<Object> {
    public static final int TYPE_COMMENT = 0;
    public static final int TYPE_FILLER = 1;
    private Question myQuestion;
    private ArrayList<Object> myComments;
    OnDataSetChangeListener dataSetChangeListener;
    int totalHeight = 0;

    public CommentsAdapter(Context context, ArrayList<Object> comments, Question question) {
        super(context, 0, comments);
        myQuestion = question;
        myComments = comments;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("Item num " + position, "TYPE " + this.getItemViewType(position));
        if (convertView == null) {
            if(this.getItemViewType(position) == TYPE_FILLER) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_generator, parent, false);
                new CommentItem(convertView, getContext(), this, myQuestion)
                        .bindFiller((Filler) getItem(position));
            }
            else {
                Log.d("getView - ", "COMMENT");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
                new CommentItem(convertView, getContext(), this, myQuestion)
                        .bindComment((Comment) getItem(position));
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.getItem(position) instanceof Filler) {
            return TYPE_FILLER;
        }
        else if (this.getItem(position) instanceof Comment){
            return TYPE_COMMENT;
        }
        return -1;
    }

    public void newComment(Comment comment){
        myComments.add(myComments.size()-1,comment);
        notifyDataSetChanged();
    }

    public void setDataSetChangeListener(OnDataSetChangeListener listener){
        dataSetChangeListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("setNotifyOnChange", "running");
        if(dataSetChangeListener!=null)
            dataSetChangeListener.onDataChanged();
    }



    @Override
    public int getViewTypeCount() {
        return 2;
    }
}



        /*extends RecyclerView.Adapter<CommentItem> {

    public static final int TYPE_COMMENT = 0;
    public static final int TYPE_FILLER = 1;
    private int type;
    private ArrayList<Object> myComments;
    private Context myContext;
    private Question myQuestion;

    public CommentsAdapter(ArrayList<Object> comments, Context context, Question question) {
        myComments = comments;
        myContext = context;
        myQuestion = question;
    }

    @Override
    public CommentItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TYPE_COMMENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_item, parent, false);
                break;
            case TYPE_FILLER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_generator, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_item, parent, false);
                break;
        }
        return new CommentItem(view, myContext, this, myQuestion);
    }

    @Override
    public void onBindViewHolder(CommentItem holder, int position) {
        if(getItemViewType(position) == TYPE_COMMENT){
            Filler f = (Filler) myComments.get(position);
            holder.bindFiller(f);
        }
        else {
            Comment q = (Comment) myComments.get(position);
            holder.bindComment(q);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        Log.d("get view type: ", ""+position);
        if(myComments.get(position) instanceof Comment) {
            return TYPE_COMMENT;
        }
        else {
            return TYPE_FILLER;
        }

    }


    @Override
    public int getItemCount() {
        return myComments.size();
    }

    public void insert(Comment comment){
        myComments.add(0, comment);
        notifyItemInserted(0);
    }
}*/
