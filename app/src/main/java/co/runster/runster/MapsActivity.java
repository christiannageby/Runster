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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private LocationManager locationManager;
    private String provider;

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

        Criteria c = new Criteria();
        provider = locationManager.getBestProvider(c, false);

        //region PERM_CHK
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //endregion
        lastLocation = locationManager.getLastKnownLocation(provider);
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

        YouPos = map.addMarker(new MarkerOptions().position(
                new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_i)));
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
    }

    @Override
    public void onLocationChanged(Location location) {
        moveMkr(new LatLng(location.getLatitude(), location.getLongitude()));
        lastLocation = location;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}