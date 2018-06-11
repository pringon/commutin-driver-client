package com.commutin.driverapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ConnectActivity extends AppCompatActivity {

    private EditText mClientIdField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mClientIdField = findViewById(R.id.et_client_id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setConnectionId(View view) {
        String driverId = mClientIdField.getText().toString();

        Intent intent = new Intent(this, TrackerActivity.class);
        intent.putExtra("driverId", Integer.parseInt(driverId));
        startActivity(intent);
    }
}
