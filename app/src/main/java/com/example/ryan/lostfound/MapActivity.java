package com.example.ryan.lostfound;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;



public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<LatLng> clickedPoints;
    private int drawMode;
    private final int POINT = 0;
    private final int PATH = 1;
    private final int AREA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clickedPoints = new ArrayList<LatLng>();
        drawMode = POINT;
        buildGoogleApiClient();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);


    }

    public void onMapClick(LatLng point) {
        clickedPoints.add(point);
        mMap.clear();
        drawClickedPoints();
    }

    public void onMapLongClick(LatLng point) {
        if (clickedPoints.size() > 0) {
            clickedPoints.remove(clickedPoints.size() - 1);
        }

        drawClickedPoints();
    }

    private void drawClickedPoints() {
        mMap.clear();

        if (clickedPoints.size() == 0) {
            return;
        }

        switch (drawMode) {
            case POINT:
                for (LatLng point : clickedPoints) {
                    MarkerOptions markerOptions = new MarkerOptions().position(point);
                    mMap.addMarker(markerOptions);
                }

                break;
            case AREA:
                PolygonOptions polygonOptions = new PolygonOptions();

                for (LatLng point : clickedPoints) {
                    polygonOptions.add(point);
                }

                mMap.addPolygon(polygonOptions);

                break;
            case PATH:
                PolylineOptions polylineOptions = new PolylineOptions();

                for (LatLng point : clickedPoints) {
                    polylineOptions.add(point);
                }

                mMap.addPolyline(polylineOptions);

                break;
            default:
                Log.d("DRAW", "NOTHING");
        }
    }

    public void changeModeToPath(View view) {
        drawMode = PATH;
        drawClickedPoints();
    }

    public void changeModeToArea(View view) {
        drawMode = AREA;
        drawClickedPoints();
    }

    public void changeModeToPoint(View view) {
        drawMode = POINT;
        drawClickedPoints();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Add a marker in Sydney and move the camera
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng currentLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in current location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void returnLocation(View view){
        Intent returnIntent = new Intent();



        returnIntent.putExtra("type",""+drawMode);
        returnIntent.putExtra("list", clickedPoints);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}
