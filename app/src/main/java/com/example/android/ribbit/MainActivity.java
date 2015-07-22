package com.example.android.ribbit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 10*1024*1024;

    protected Uri mMediaUri;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //which is index of the item that is tapped on in dialog.
            switch (which)
            {
                case 0:
                {
                    //Take Picture
                    // create Intent to take a picture and return control to the calling application
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);// create a file to save the image
                    if (mMediaUri == null) {
                        //there was an error.
                        Toast.makeText(MainActivity.this,
                                R.string.error_external_storage,
                                Toast.LENGTH_LONG).show();
                    } else {

                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);//set the image filename
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;
                }
                case 1:
                {
                    //Take video
                    //create a Intent to take a video and return control to the calling application.
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);//create a file to save the video or a Picture
                    if (mMediaUri == null) {
                        //there was an error.
                        Toast.makeText(MainActivity.this,
                                R.string.error_external_storage,
                                Toast.LENGTH_LONG).show();
                    } else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);//path send to be used to store a video or image.
                        //some restrictions for the video.
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);//) for the lowest quality.
                        startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                    }
                    break;
                }
                case 2:
                {
                    //choose Picture
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);//Allow the user to select a particular kind of data.
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;
                }
                case 3:
                {
                    //choose Video
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);//Allow the user to select a particular kind of data.
                    choosePhotoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, R.string.video_file_size_warning, Toast.LENGTH_LONG);
                    startActivityForResult(choosePhotoIntent, PICK_VIDEO_REQUEST);
                    break;
                }

            }
        }

        private Uri getOutputMediaFileUri(int mediaType) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.

            String appName = getString(R.string.app_name);

            if (isExternalStorageAvailable()){
                //get the Uri
                //1. Get the external storage directory.
                File mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);

                //2. Create subdirectory.
                if (! mediaStorageDir.exists()){
                    if (mediaStorageDir.mkdirs()){
                        Log.e(TAG, "Failed to create directory");
                        return null;
                    }
                }
                //3. Create a filename.
                //4. Create the file.
                File mediaFile;
                Date now = new Date();//Initializes to the current time.
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                String path = mediaStorageDir.getPath() + File.separator;
                if (mediaType == MEDIA_TYPE_IMAGE){
                    mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                }
                else if (mediaType == MEDIA_TYPE_VIDEO){
                    mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                } else {
                    return null;
                }
                //5.Return the file's URI
                Log.d(TAG, "File" + Uri.fromFile(mediaFile));

                return Uri.fromFile(mediaFile);//Returns the file's uniform resource identifier;

            } else {
                return null;
            }

        }

        private boolean isExternalStorageAvailable(){
            String state = Environment.getExternalStorageState();

            if (state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            }
            else {
                return false;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If we have a user cached on a disk show the mainActivity else navigate to login.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        } else {
            Log.i(TAG, currentUser.getUsername());
        }
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){


            //pick from the gallery.
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
                if (data == null){
                    Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG);
                }
                else {
                    mMediaUri = data.getData();//get the data the intent is operating on.
                }

                Log.i(TAG, "MEDIA URI:" + mMediaUri);

                InputStream inputStream = null;

                if (requestCode == PICK_VIDEO_REQUEST){
                    //make sure the file is less than 10MB,
                    int fileSize = 0;
                    inputStream = null;
                    try {
                        //getContentResolver returns a ContentResolver instance for your application's package.
                        //Content resolver class provides access to the content model.
                        //A content provider manages access to a central repository of data.
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(this,getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e){
                        Toast.makeText(this,getString(R.string.error_opening_file), Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                            try {
                                inputStream.close();
                            }
                            catch (IOException e) {/*This is intentionally black*/}
                    }

                    if (fileSize >= FILE_SIZE_LIMIT){
                        Toast.makeText(this, R.string.error_file_too_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

            }
            //add to the gallery
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//Implicit intent(Broadcast action)
                mediaScanIntent.setData(mMediaUri);//set data this intent will operate on.
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientIntent = new Intent(this, RecipientsActivity.class);
            recipientIntent.setData(mMediaUri);//set data this intent is operating on

            String fileType;
            if (requestCode == TAKE_PHOTO_REQUEST || requestCode == PICK_PHOTO_REQUEST){
                fileType = ParseConstants.TYPE_IMAGE;
            } else {
                fileType = ParseConstants.TYPE_VIDEO;
            }

            recipientIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);

            startActivity(recipientIntent);

        } else if (resultCode != RESULT_CANCELED){
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_logout:
            {
                ParseUser.logOut();
                navigateToLogin();
                break;
            }
            case R.id.action_edit_friends:
            {
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_camera:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //Creating a dialog of choices.
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            case R.id.action_sendMessage:
                Intent intent  = new Intent(this, SendMessage.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

}
