package co.runster.runster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private GoogleMap map;


    public Marker YouPos;
    public Location lastLocation;
    public long time;



    @Override
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

        time = System.currentTimeMillis() / 1000;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }else{
            //region Permissions Check
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {return;}
            //endregion
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (YouPos == null){
                if (lastLocation.getLatitude() != 0 || lastLocation.getLongitude() != 0){
                    YouPos = map.addMarker(new MarkerOptions().position(
                            new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_i)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
                }else{
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("GPS error")
                            .setMessage("Please restart again later")
                            .setPositiveButton("Quit Runster", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(500);
                                }
                            }).show();
                }
            }else{
                moveMkr(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }

            onLocationChanged(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i){
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
        moveMkr(new LatLng(location.getLatitude(), location.getLongitude()));

        double Distance = Haversin.distance(lastLocation, location);

        if(Distance/(Math.abs((System.currentTimeMillis() / 1000L) - time)) <= 7) {
            Toast.makeText(getApplicationContext(), "Points revarded", Toast.LENGTH_SHORT).show();
        }

        time = System.currentTimeMillis() / 1000L;
        lastLocation = location;
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
        map.setMinZoomPreference(16.0f);
        map.setMaxZoomPreference(18.0f);
    }
}