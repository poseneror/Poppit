package posener.poppit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import posener.poppit.R;
import posener.poppit.interfaces.OnFocusRequestListener;
import posener.poppit.objects.FeedItem;
import posener.poppit.objects.Filler;
import posener.poppit.objects.Question;

/**
 * Created by Or on 19/06/2015.
 */
public class FeedsAdapter extends RecyclerView.Adapter<FeedItem> {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_EXPANDABLE = 1;
    public static final int TYPE_PROFILE = 100;
    private int type = TYPE_NORMAL;
    private ArrayList<Object> myFeeds;
    private Context myContext;
    private OnFocusRequestListener onFocusRequestListener;

    public FeedsAdapter(ArrayList<Object> questions, Context context) {
        myFeeds = questions;
        myContext = context;
    }

    @Override
    public FeedItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case Question.TYPE_YN:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_question_yes_no, parent, false);
                break;
            case Question.TYPE_CUSTOM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_question_custom, parent, false);
                break;
            case Question.TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_question_image, parent, false);
                break;
            case Question.TYPE_ANSWERED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_answer, parent, false);
                break;
            case Question.TYPE_ANSWERED_IMG:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_answer_image, parent, false);
                break;
            case TYPE_PROFILE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_profile, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_question_yes_no, parent, false);
                break;
        }
        return new FeedItem(view, myContext, this);
    }

    @Override
    public void onBindViewHolder(FeedItem holder, int position) {
        if(getItemViewType(position) == TYPE_PROFILE){
            Filler f = (Filler) myFeeds.get(position);
            holder.bindFiller(f);
        }
        else {
            Question q = (Question) myFeeds.get(position);
            holder.bindQuestion(q);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if(myFeeds.get(position) instanceof Question) {
            Question q = (Question) myFeeds.get(position);
            if (!q.UserVoted()) {
                switch (q.type) {
                    case Question.TYPE_YN:
                        return Question.TYPE_YN;
                    case Question.TYPE_CUSTOM:
                        return Question.TYPE_CUSTOM;
                    case Question.TYPE_IMAGE:
                        return Question.TYPE_IMAGE;
                    default:
                        return Question.TYPE_YN;
                }
            } else {
                switch (q.type) {
                    case Question.TYPE_IMAGE:
                        return Question.TYPE_ANSWERED_IMG;
                    default:
                        return Question.TYPE_ANSWERED;
                }
            }
        }
        else if(myFeeds.get(position) instanceof Filler) {
            Filler f = (Filler) myFeeds.get(position);
            switch (f.type) {
                case Filler.TYPE_PROFILE:
                    return TYPE_PROFILE;

            }
        }
        return Question.TYPE_YN;
    }


    @Override
    public int getItemCount() {
        return myFeeds.size();
    }

    public void insert(Question question){
        myFeeds.add(0, question);
        notifyItemInserted(0);
    }
    public void insertBelow(Question question, int position){
        myFeeds.add(position+1, question);
        notifyItemInserted(position+1);
    }

    public void setOnFocusRequestListener(OnFocusRequestListener focusRequestListener){
        onFocusRequestListener = focusRequestListener;
    }

    public void ChangeFocus(int position){
        if(onFocusRequestListener!=null)
            onFocusRequestListener.changeFocus(position);
    }

    public void ScrollBy(int position, int amount){
        Log.d("SCrollby", "called");
        if(onFocusRequestListener!=null)
            onFocusRequestListener.scrollBy(position, amount);
    }


    public void moveToEnd(Question question){
        int firstPosition = myFeeds.indexOf(question);
        myFeeds.remove(question);
        myFeeds.add(question);
        notifyItemMoved(firstPosition, myFeeds.indexOf(question));
    }

    public void update(Question question){
        boolean found = false;
        for(Object item : myFeeds)
        {
            if(item instanceof Question){
                if(((Question) item).id.equals(question.id)){
                    myFeeds.set(myFeeds.indexOf(item), question);
                    found = true;
                }
            }
        }
        if(!found){
            insert(question);
        }
    }

    public int unAnsweredCount(){
        int unAnswered = 0;
        for(Object feed : myFeeds){
            if(feed instanceof Question){
                if(!((Question) feed).UserVoted()){
                    unAnswered += 1;
                }
            }
        }
        return unAnswered;
    }
}
