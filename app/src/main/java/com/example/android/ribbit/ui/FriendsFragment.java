package com.example.android.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.android.ribbit.utils.ParseConstants;
import com.example.android.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by adhyan on 7/7/15.
 */
public class FriendsFragment extends ListFragment{

    private static final String TAG = FriendsFragment.class.getSimpleName();
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //analogous to setContentView method in the Activity.
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        //get relations for the current user if the column exists it is returned else it is created.
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //we are querying into parse relational object.
        //get the list of parse user objects which are in relation with the current parse user.
        mProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mFriends = friends;
                if (e == null) {
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            /*The list view knows in which context it is in because it's layout in fragment and
                             in activity that contains the fragment. getActivity() can also be used.*/
                            getListView().getContext(),
                            android.R.layout.simple_list_item_1,
                            usernames
                    );
                    setListAdapter(adapter);
                }
                else if(e.getMessage().equals("java.lang.ClassCastException: java.lang.String cannot be cast to org.json.JSONObject")){
                    //Do nothing since friends are not yet added.
                    //ignore
                }
                else {
                    //Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setTitle(R.string.error_title);
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), Profile.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
