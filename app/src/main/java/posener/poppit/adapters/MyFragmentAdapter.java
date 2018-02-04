package posener.poppit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import posener.poppit.fragments.FeedsFragment;
import posener.poppit.fragments.ProfileFragment;
import posener.poppit.fragments.FriendsSelector;
import posener.poppit.fragments.InflaterFragment;
import posener.poppit.objects.Question;

/**
 * Created by Or on 02/07/2015.
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    private final FragmentManager myFM;
    private Fragment mFragmentAtPos1;

    public MyFragmentAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
        myFM = fm;
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position)
    {
        if (position==0) {
            return new FeedsFragment();
        }
        if (position == 1)
        {
            if (mFragmentAtPos1 == null)
            {
                mFragmentAtPos1 = new FriendsSelector();
            }
            return mFragmentAtPos1;
        }
        else {
            return new ProfileFragment();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getItemPosition(Object object)
    {
        if (object instanceof FriendsSelector && mFragmentAtPos1 instanceof InflaterFragment)
            return POSITION_NONE;
        if (object instanceof InflaterFragment && mFragmentAtPos1 instanceof FriendsSelector)
            return POSITION_NONE;
        return POSITION_UNCHANGED;
    }

    public void SwitchToInflater(ArrayList<ParseUser> recipients) {
        if (mFragmentAtPos1 instanceof FriendsSelector){
            InflaterFragment myFrag = new InflaterFragment();
            mFragmentAtPos1 = myFrag;
            Log.d("Switching!!!", "To inflater");
            notifyDataSetChanged();
            myFrag.setRecipients(recipients);
        }
    }

    public void SwitchToFollowUp(ParseObject question, ArrayList<ParseUser> recipients, int option) {
        if (mFragmentAtPos1 instanceof FriendsSelector){
            InflaterFragment myFrag = new InflaterFragment();
            mFragmentAtPos1 = myFrag;
            Log.d("Switching!!!", "To inflater");
            notifyDataSetChanged();
            myFrag.setFollowUp(question, recipients, option);
        }
    }

    public void SwitchToFriendsSelector(ArrayList<ParseUser> recipients) {
        if (mFragmentAtPos1 instanceof InflaterFragment){
            FriendsSelector myFrag = new FriendsSelector();
            mFragmentAtPos1 = myFrag;
            Log.d("Switching!!!", "To selector");
            notifyDataSetChanged();
            myFrag.setRecipients(recipients);
        }
    }
}