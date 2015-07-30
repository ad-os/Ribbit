package com.example.android.ribbit.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.ribbit.R;

public class SendMessage extends AppCompatActivity {

    private EditText mMessageText;
    private Button mSendButton;
    protected String mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        mMessageText = (EditText) findViewById(R.id.editText);
        mSendButton = (Button) findViewById(R.id.send_button);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessage = mMessageText.getText().toString();
                Intent intent = new Intent(SendMessage.this, TextRecipients.class);
                intent.putExtra("Message", mMessage);
                startActivity(intent);
            }
        });

    }

}
