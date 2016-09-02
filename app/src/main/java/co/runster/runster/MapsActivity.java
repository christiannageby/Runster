package co.runster.runster;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener {

    private GoogleMap map;
    public Marker myMkr;

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

        //region MAP_SEGMENT_SETUP

        //Välj map-segmentet och använd det till kartan

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //endregion

        //region MENU_BUTTON_CLICK
        //definera en Floation button till menyknappen

        final FloatingActionButton menu = (FloatingActionButton) findViewById(R.id.menuBtn);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vad som ska hända när man trycker på kanppen
                moveMkr(new LatLng(1, 1));
            }
        });
        //endregion

    }

    //denna funktion flyttar användarens positionsindikator till en  ny postion
    public void moveMkr(LatLng newLocation){
        myMkr.setPosition(newLocation);
        map.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
    }

    @Override
    //vad som händer med kartan
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);

        //region LOCATION_PERMISSION_CHECK
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //endregion_

        Location lastLocation = locationManager.getLastKnownLocation(provider);

        myMkr = map.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_i)));

        //flyttar kameran till användarens position
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));

        //sätter zoom nivån till 17.0
        map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    //vad som händer när användaren förflyttar sig

    @Override
    public void onLocationChanged(Location location) {
        moveMkr(new LatLng(location.getLatitude(), location.getLongitude()));
    }
}