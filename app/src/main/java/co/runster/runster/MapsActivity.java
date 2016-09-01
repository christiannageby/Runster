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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private GoogleMap map;

    public LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    public Criteria criteria = new Criteria();
    public String provider = locationManager.getBestProvider(criteria, true);

    public Location lastLocation = locationManager.getLastKnownLocation(provider);

    public LatLng lastPos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

    public float distance = 0;

    //få avståndet mellan 2 punkter i meter
    public float getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        return l1.distanceTo(l2);
    }

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
                Toast.makeText(getApplicationContext(), "this is my Toast message!!! =)", Toast.LENGTH_LONG).show();
            }
        });
        //endregion


    }


    @Override
    //vad som händer med kartan
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
    }

    @Override
    public void onMyLocationChange(Location location) {
        distance++;
        lastPos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

    }
}