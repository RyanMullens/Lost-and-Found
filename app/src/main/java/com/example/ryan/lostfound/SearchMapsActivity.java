package com.example.ryan.lostfound;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<LatLng> clickedPoints;
    private ArrayList<LatLng> initialPoints;
    private int drawMode;
    private final int POINT = 0;
    private final int PATH = 1;
    private final int AREA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clickedPoints = new ArrayList<LatLng>();
        initialPoints = new ArrayList<LatLng>();



        drawMode = PATH;
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
        drawSearchPoints();
    }

    public void onMapClick(LatLng point) {
        clickedPoints.add(point);
        drawClickedPoints();
    }

    public void onMapLongClick(LatLng point) {
        if (clickedPoints.size() > 0) {
            clickedPoints.remove(clickedPoints.size() - 1);
        }

        drawClickedPoints();
    }

    private void drawSearchPoints() {
        if (initialPoints.size() == 0) {
            return;
        }

        switch (drawMode) {
            case POINT:
                for (LatLng point : initialPoints) {
                    MarkerOptions markerOptions = new MarkerOptions().position(point);
                    mMap.addMarker(markerOptions);
                }

                break;
            case AREA:
                PolygonOptions polygonOptions = new PolygonOptions();

                for (LatLng point : initialPoints) {
                    polygonOptions.add(point);
                }

                mMap.addPolygon(polygonOptions);

                break;
            case PATH:
                PolylineOptions polylineOptions = new PolylineOptions();

                for (LatLng point : initialPoints) {
                    polylineOptions.add(point);
                }

                mMap.addPolyline(polylineOptions);

                break;
            default:
                Log.d("DRAW", "NOTHING");
        }
    }

    private void drawClickedPoints() {
        mMap.clear();
        drawSearchPoints();

        if (clickedPoints.size() == 0) {
            return;
        }

        switch (drawMode) {
            default:
                for (LatLng point : clickedPoints) {
                    MarkerOptions markerOptions = new MarkerOptions().position(point);
                    mMap.addMarker(markerOptions);
                }

                break;
        }
    }


    private void getList() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        Intent myIntent = getIntent();
        String id = myIntent.getStringExtra("id");
        String url ="http://72.19.65.87:3000/lost/"+id;

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            Log.d("HEY", response.toString());

                        } catch (Exception e) {

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HEY", "did not work");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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

        double currentLat = currentLocation.latitude;
        double currentLng = currentLocation.longitude;
        initialPoints.add(new LatLng(currentLat + 0.01, currentLng - 0.01));
        initialPoints.add(new LatLng(currentLat, currentLng + 0.01));
        initialPoints.add(new LatLng(currentLat - 0.01, currentLng));
        drawSearchPoints();
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

    public void submit(View view){
        Intent returnIntent = new Intent();

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void addPoints(View view) {

    }

    public void foundItem(View view) {
        Intent mapIntent = new Intent(this, FoundActivity.class);
        startActivityForResult(mapIntent, 1);
    }

}
