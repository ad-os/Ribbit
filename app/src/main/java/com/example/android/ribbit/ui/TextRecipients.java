package com.example.android.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.ribbit.utils.ParseConstants;
import com.example.android.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class TextRecipients extends AppCompatActivity {

    protected ListView mFriendsListView;
    protected ProgressBar mProgressBar;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mFriends;
    protected String mMessage;
    protected MenuItem mMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recipients);

        mFriendsListView = (ListView) findViewById(android.R.id.list);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        Intent intent = getIntent();
        mMessage = intent.getStringExtra("Message");
        mFriendsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFriendsListView.getCheckedItemCount() > 0) {
                    mMenuItem.setVisible(true);
                } else {
                    mMenuItem.setVisible(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
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
                            TextRecipients.this,
                            android.R.layout.simple_list_item_checked,
                            usernames
                    );
                    mFriendsListView.setAdapter(adapter);
                } else if (e.getMessage().equals("java.lang.ClassCastException: java.lang.String cannot be cast to org.json.JSONObject")) {
                    //Do nothing since friends are not yet added.
                    //ignore
                } else {
                    //Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(TextRecipients.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_recipients, menu);

        mMenuItem = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_send){
           ParseObject message = createTextMessage();
            if (message == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(TextRecipients.this);
                builder.setTitle(R.string.title_creating_text_message);
                builder.setMessage(R.string.message_creating_text_message);
                builder.setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                send(message);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    //success
                    Toast.makeText(TextRecipients.this, "Messsage Sent", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TextRecipients.this);
                    builder.setTitle(R.string.title_error_sending_text);
                    builder.setMessage(R.string.message_error_sending_text);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private ParseObject createTextMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_TEXTMESSAGES);
        message.put(ParseConstants.KEY_TEXT_MESSAGE, mMessage);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        return message;
    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientsIds = new ArrayList<String>();
        for (int i =0; i < mFriendsListView.getCount(); i++)
        {
            if (mFriendsListView.isItemChecked(i)){
                recipientsIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsIds;
    }
}
