package posener.poppit.interfaces;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import posener.poppit.objects.Question;

/**
 * Created by Or on 01/07/2015.
 */
public interface MainActivityComm {
    public void ViewPostedQuestion(Question question);
    public void InflateWithRecipients(ArrayList<ParseUser> recipients);
    public void SelectFriends(ArrayList<ParseUser> recipients);
    public void ResetSearch();
    public void SetUnread(int newQuestions);

}
