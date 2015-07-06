package com.example.android.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by adhyan on 5/7/15.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "FqxuOErsqMVQBSkDAlxpCHDOWa3Sr6a8sKLtlX0F", "ugsxOZJmokXi9Qr3rSHeW0qFGcwglF9TOUrBFVQM");

    }
}
