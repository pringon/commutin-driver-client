package com.commutin.driverapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.commutin.driverapp.Structures.ClientCoordinates;
import com.commutin.driverapp.Structures.ConnectionData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TrackerActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
          mSocket = IO.socket("http://10.0.2.2:3001");
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Boolean tripInProgress;

    LocationManager mLocationManager;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if(tripInProgress) {
                ClientCoordinates coordinates = new ClientCoordinates(location.getLatitude(),
                                                                    location.getLongitude());
                String jsonCoordinates = (new Gson()).toJson(coordinates);
                mSocket.emit("location change", jsonCoordinates);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        Intent calledIntent = getIntent();
        int driverId = calledIntent.getIntExtra("driverId", 404);
        int routeId  = calledIntent.getIntExtra("routeId", 404);

        mSocket.connect();
        ConnectionData sendIds = new ConnectionData(driverId, routeId);
        String jsonSendIds = (new Gson()).toJson(sendIds);
        mSocket.emit("set id", jsonSendIds);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        tripInProgress = false;
        startTracker();

    }

    private boolean hasLocationPermission() {
        int locationPermission = ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION);
        if(locationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void promptGrantLocationPermission() {
        ArrayList<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this, requiredPermissions.toArray(new String[requiredPermissions.size()]),
                1);
    }

    private boolean isLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void promptEnableLocation() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(this.getResources().getString(R.string.enable_location_prompt_message));
        dialog.setPositiveButton(this.getResources().getString(R.string.enable_location_prompt_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(locationIntent);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i("TRACKER", "Attempting to get location");
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(isLocationEnabled()) {
                startTracker();
                return;
            }
            promptEnableLocation();
            startTracker();
            return;
        }
        promptGrantLocationPermission();
    }

    private void startTracker() throws SecurityException {
        if(hasLocationPermission()) {
            if(isLocationEnabled()) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        100, 0, mLocationListener);
                return;
            }
            promptEnableLocation();
            startTracker();
        }
        promptGrantLocationPermission();
    }

    private void startTrip() {
        if(!tripInProgress) {
            tripInProgress = true;
        }
    }

    private void endTrip() {
        if(tripInProgress) {
            tripInProgress = false;
        }
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
        switch(clickedItemId) {

            case R.id.start_trip:
                startTrip();
                return true;

            case R.id.end_trip:
                endTrip();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
