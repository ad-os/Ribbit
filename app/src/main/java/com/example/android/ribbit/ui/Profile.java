package com.example.android.ribbit.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.ribbit.utils.ParseConstants;
import com.example.android.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public class Profile extends AppCompatActivity {

    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mCurrentUserRelations;
    protected ParseQuery<ParseUser> mParseQuery;
    protected TextView mUsername;
    protected TextView mFirstname;
    protected TextView mLastname;
    protected TextView mHometown;
    protected TextView mEmail;
    protected TextView mWebsite;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUsername = (TextView) findViewById(R.id.username);
        mFirstname = (TextView) findViewById(R.id.firstname);
        mLastname = (TextView) findViewById(R.id.lastname);
        mHometown = (TextView) findViewById(R.id.hometown);
        mEmail = (TextView) findViewById(R.id.email);
        mWebsite = (TextView) findViewById(R.id.website);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        final int position = intent.getIntExtra("position", 0);
        mCurrentUser = ParseUser.getCurrentUser();
        //getting the relations of the current user
        mCurrentUserRelations = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //querying the relation
        mParseQuery = mCurrentUserRelations.getQuery();
        mParseQuery.addAscendingOrder(ParseConstants.KEY_USERNAME);
        mParseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                ParseUser user = list.get(position);
                if (e == null){
                    mUsername.setText("Username : " + user.getUsername());
                    mFirstname.setText("Firstname : " + user.getString("firstname"));
                    mLastname.setText("Lastname : " + user.getString("lastname"));
                    mEmail.setText("Email : " + user.getEmail());
                    mHometown.setText("Hometown : " + user.getString("hometown"));
                    mWebsite.setText("Website : " + user.getString("website"));
                }
            }
        });
    }

}
