package co.runster.runster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //GPS-variabler
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    //konstanta variabler(FINAL)
    private final String RUNSTER_TAG = "RUNSTER";

    private GoogleMap map;
    public Marker YouPos;
    public Location lastLocation;

    @Override
    //vad som händer när appen startas
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(500)
                .setFastestInterval(1000)
                .setSmallestDisplacement(0.5f);

    }

    //region GPS Functions(Connect, Suspended, Failed)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(RUNSTER_TAG, "Location services Connected");
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }else{
            //region Permissions Check
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
            //endregion
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


            //om markören är lika med null så ge denne en position osv(KOnfigurera den)
            //annars flytta markören;
            if (YouPos == null){
                YouPos = map.addMarker(new MarkerOptions().position(
                        new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_i)));
                map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
            }else{
                moveMkr(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }

            onLocationChanged(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(RUNSTER_TAG, "Location Services suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(RUNSTER_TAG, "Location services failed to connect!");
    }
    //endregion

    //region Application Actions(OnPause, OnResume)
    @Override
    protected void onResume(){
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void  onPause(){
        super.onPause();
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }
    //endregion


    @Override
    public void onLocationChanged(Location location) {
        Log.i(RUNSTER_TAG, location.toString());
        moveMkr(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void moveMkr(LatLng newLocation) {
        YouPos.setPosition(newLocation);
        map.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
    }

    public void menuClick(View view) {
        Intent SettingsIntent = new Intent(this, Settings.class);
        startActivity(SettingsIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setMapType(1);
        map.setMinZoomPreference(18.0f);
        map.setMaxZoomPreference(18.0f);
    }
}