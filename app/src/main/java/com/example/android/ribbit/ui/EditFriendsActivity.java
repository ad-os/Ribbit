package com.example.android.ribbit.ui;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.parse.SaveCallback;

import java.util.List;

public class EditFriendsActivity extends AppCompatActivity{

    private static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ProgressBar mProgressBar;
    protected ListView mListView;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (ListView) findViewById(android.R.id.list);
        //If we extend ListActivity then getListView() would give us the ListView widget associates with the activity.
        //Enables checking on the List.
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        addFriend();
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
                if (e == null){
                    //success
                    mUsers = list;
                    int i = 0;
                    String[] users = new String[mUsers.size()];
                    for(ParseUser user : mUsers){
                        users[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,
                            users);
                    mListView.setAdapter(adapter);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addFriend() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Add the Parse user object to the parse relation object and save it.
                //Duplicate entries get ignored on the parse.com.It's Awesome ;-)
                if (mListView.isItemChecked(position)) {
                    //add the friend
                    //Added to thr fetched relation objects.
                    mFriendsRelation.add(mUsers.get(position));
                }
                else {
                   //Remove the friends.
                    mFriendsRelation.remove(mUsers.get(position));
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

                        for (ParseUser friend : friends)
                        {
                            if (user.getObjectId().equals(friend.getObjectId()))
                            {
                                mListView.setItemChecked(i, true);
                            }
                        }
                    }
                }
                else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}
