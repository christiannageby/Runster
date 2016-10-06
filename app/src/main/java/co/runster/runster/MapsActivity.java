package co.runster.runster;

import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleMap map;
    public Marker YouPos;

    //Initiera en ny PositionManager som används för att komma åt enhetens platsinfo
    public LocationManager locationManager;
    //Initiera en kriterie variabel
    public Criteria criteria;

    //få den bästa gps leverantören
    public String provider;
    //få den senaste positionen uppdaterat var 5e sekund
    public Location lastLocation;

    @Override
    //vad som händer när appen startas
    protected void onCreate(Bundle savedInstanceState) {
        //kolla gps-postionen och gps-status
        //region CHECK_GPS_STATUS
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //endregion

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        lastLocation = locationManager.getLastKnownLocation(provider);

        //region MAP_SEGMENT_SETUP

        //Välj map-segmentet och använd det till kartan

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //endregion
    }


    //denna funktion flyttar användarens positionsindikator till en  ny postion
    public void moveMkr(LatLng newLocation){
        YouPos.setPosition(newLocation);
        map.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
    }

    //denna funktion blir exekverad när man trycker på menuknappen
    public void menuClick(View view){
        moveMkr(new LatLng(1,2));
    }

    @Override
    //vad som händer med kartan
    public void onMapReady(GoogleMap googleMap) {
        //ge Kartvariabeln ett värde
        map = googleMap;

        //initiera spelarens positionsmarkör
        YouPos = map.addMarker(new MarkerOptions().position(
        new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_i)));

        //sätter zoom nivån till 17.0
        map.animateCamera(CameraUpdateFactory.zoomTo(27.0f));

        //flytta kameran & markern till nuvarnade kordinater
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "The application was closed because the gps signal is unavailable", Toast.LENGTH_LONG)
                .show();
        System.exit(1);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(),
                "Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
    }
}