package com.commutin.driverapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TrackerActivity extends AppCompatActivity {

    private String ACTIVITY_TAG = "tracker";

    private EditText mSendDataEditField;
    private TextView mGetResponseTextView;

    private Socket mSocket;
    {
        try{
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (URISyntaxException e) {}
    }

    private Emitter.Listener onResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    mGetResponseTextView.setText(data);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        mSendDataEditField = findViewById(R.id.et_send_data);
        mGetResponseTextView = findViewById(R.id.tv_get_response);

        mSocket.on("response", onResponse);
        mSocket.connect();
        mSocket.emit("set id","5044");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("response", onResponse);
    }

    private void sendData() {
        String dataToBeSent = mSendDataEditField.getText().toString().trim();
        if(TextUtils.isEmpty(dataToBeSent)) {
            return;
        }

        mSocket.emit("send data", dataToBeSent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItemId = item.getItemId();
        if(clickedItemId == R.id.send_data) {
            sendData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
