package com.uguurozkan.messagereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MessageReader extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_reader);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
