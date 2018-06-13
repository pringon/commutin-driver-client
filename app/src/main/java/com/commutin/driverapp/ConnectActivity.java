package com.commutin.driverapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ConnectActivity extends AppCompatActivity {

    private TextView mErrorMessageTextView;

    private EditText mClientIdField;

    private EditText mRouteIdField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mClientIdField        = findViewById(R.id.et_client_id);
        mRouteIdField         = findViewById(R.id.et_route_id);

        showErrors();
    }

    private void showErrors() {
        Intent callerIntent = getIntent();
        if(callerIntent.hasExtra("errorType")) {
            String errorType = callerIntent.getStringExtra("errorType").toString();
            if(errorType.equals("connectionError")) {
                mErrorMessageTextView.setText("There was an error connecting to the server. Please try again");
                mErrorMessageTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setConnectionId(View view) {
        String driverId = mClientIdField.getText().toString();
        String routeId  = mRouteIdField.getText().toString();

        if(driverId == null || TextUtils.isEmpty(driverId)) {
            return;
        }
        if(routeId == null || TextUtils.isEmpty(routeId)) {
            return;
        }

        Intent intent = new Intent(this, TrackerActivity.class);
        intent.putExtra("driverId", Integer.parseInt(driverId));
        intent.putExtra("routeId", Integer.parseInt(routeId));
        startActivity(intent);
    }
}
