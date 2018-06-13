package com.commutin.driverapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ConnectActivity extends AppCompatActivity {

    private EditText mClientIdField;

    private EditText mRouteIdField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mClientIdField = findViewById(R.id.et_client_id);
        mRouteIdField  = findViewById(R.id.et_route_id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
