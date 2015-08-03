package com.example.android.ribbit.ui;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.ribbit.adapters.UserAdapter;
import com.example.android.ribbit.utils.ParseConstants;
import com.example.android.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity {

    private static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ProgressBar mProgressBar;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mGridView = (GridView) findViewById(R.id.friendsGridView);
        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.VISIBLE);

        mCurrentUser = ParseUser.getCurrentUser();
        //get relations for the current user if the column exists it is returned else it is created.
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //get the ParseUser objects.
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        //gets the query in ascending order.
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        //setting limit for the returned query.
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    //success
                    mUsers = list;
                    int i = 0;
                    String[] users = new String[mUsers.size()];
                    for (ParseUser user : mUsers) {
                        users[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).refill(mUsers);
                    }
                    addFriendCheckmarks();

                } else {
                    Log.i(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void addFriendCheckmarks() {
        //we are querying into parse relational object.
        //get the list of parse user objects which are in relation with the current parse user.
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    //list returned look for a match.
                    for (int i = 0; i < mUsers.size(); i++) {

                        ParseUser user = mUsers.get(i);

                        for (ParseUser friend : friends) {
                            if (user.getObjectId().equals(friend.getObjectId())) {
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }


    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Here view is Relative layout for user_grid.
            ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);


            //Add the Parse user object to the parse relation object and save it.
            //Duplicate entries get ignored on the parse.com.It's Awesome ;-)
            if (mGridView.isItemChecked(position)) {
                //add the friend
                //Added to thr fetched relation objects.
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                //Remove the friends.
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
            }
            //save the updated to the back-end.
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            });
        }
    };
}
