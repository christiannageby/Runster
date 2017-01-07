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
import android.util.Log;
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

import java.sql.Time;
import java.util.Date;

import static android.app.PendingIntent.getActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //GPS-variabler
    private GoogleApiClient googleApiClient;    //en Google api-klient
    private LocationRequest locationRequest;    //En Positionsfråga

    private GoogleMap map;  //Kartan

    /*
        Beräkningsvariabler
     */
    public Marker YouPos;   //Spelarmarkören
    public Location lastLocation;   //Förra positionen
    public long time;   //Unixtiden

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

        //skriv in data i positionsfrågan
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) //vilken prioritet frågan skall ha
                .setInterval(500)   //vilken intervall(ms)
                .setFastestInterval(1000)   //snabbaste intervallen(ms)
                .setSmallestDisplacement(0.5f); //minsta rörelsen (0.5 meter);

        //time = System.currentTimeMillis() / 1000;   //den nuvarande(i startögonblicket) unix tiden

    }

    //region GPS Functions(Connect, Suspended, Failed)
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


            //om markören är lika med null så ge denne en position osv(KOnfigurera den)
            //annars flytta markören;
            if (YouPos == null){
                if (lastLocation.getLatitude() != 0 || lastLocation.getLongitude() != 0){
                    //open the map if a location is avalable
                    YouPos = map.addMarker(new MarkerOptions().position(
                            new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_i)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
                }else{
                    //tvinga använderen att avsluta appen
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
        //När Anslutningen avbryts
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //När anslutningen failar
    }
    //endregion

    //region Application Actions(OnPause, OnResume)
    @Override
    protected void onResume(){
        super.onResume();
        googleApiClient.connect();  //låt Api-klienten ansluta
    }

    @Override
    protected void  onPause(){
        super.onPause();
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();   //Ta bort api-klientens anslutning om klienten är ansluten
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

        time = System.currentTimeMillis() / 1000L;  //sätt tiden till den nuvarande
        lastLocation = location;    //sätt användarens nuvarande pos till den senaste positionen
    }

    public void moveMkr(LatLng newLocation) {
        YouPos.setPosition(newLocation);    //Ge sperarmarkören in position
        map.moveCamera(CameraUpdateFactory.newLatLng(newLocation)); //Flytta kameran till samma position
    }

    public void menuClick(View view) {
        Intent SettingsIntent = new Intent(this, Settings.class);   //skapa ett nytt intent för att öppna inställningsappen
        startActivity(SettingsIntent);  //starta inställningsaktiviteten
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;    //sätt kartan till kartvariabeln(googleMap)
        map.getUiSettings().setRotateGesturesEnabled(false);    //hindra rotering av kameran
        map.setMapType(1);  //Karttyp (1=Karta;2=Satelitbilder)
        map.setMinZoomPreference(16.0f); //minsta inzoomningen
        map.setMaxZoomPreference(18.0f);    //största inzoomningen
        //båda dessa är lika stora för att hindra användaren att zooma
    }
}