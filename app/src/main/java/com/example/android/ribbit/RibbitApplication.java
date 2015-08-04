package com.example.android.ribbit;

import android.app.Application;

import com.example.android.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by adhyan on 5/7/15.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "FqxuOErsqMVQBSkDAlxpCHDOWa3Sr6a8sKLtlX0F", "ugsxOZJmokXi9Qr3rSHeW0qFGcwglF9TOUrBFVQM");

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    //Below function is for user to user notifications like on the same device if other user logs in.

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }
}
