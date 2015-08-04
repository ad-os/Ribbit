package com.example.android.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.android.ribbit.R;
import com.example.android.ribbit.RibbitApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected EditText mFirstname;
    protected EditText mLastname;
    protected EditText mHometown;
    protected EditText mWebsite;
    protected Button mSignUpButton;
    protected Button mCancelButton;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mFirstname = (EditText) findViewById(R.id.firstnameField);
        mLastname = (EditText) findViewById(R.id.lastnameField);
        mHometown = (EditText) findViewById(R.id.hometownField);
        mWebsite = (EditText) findViewById(R.id.websiteField);
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname = mFirstname.getText().toString();
                String lastname = mLastname.getText().toString();
                String hometown = mHometown.getText().toString();
                String website = mWebsite.getText().toString();
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstname.isEmpty()
                        || lastname.isEmpty() || hometown.isEmpty()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle(R.string.signup_error_title);
                    builder.setMessage(R.string.signup_error_message);
                    //null because we don't want to set the listner.
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    //create a ner user.
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put("firstname", firstname);
                    newUser.put("lastname", lastname);
                    newUser.put("hometown", hometown);
                    newUser.put("website", website);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            if (e == null){
                                //success
                                RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());

                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle(R.string.signup_error_title);
                                builder.setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });

                }
            }
        });
    }
}
