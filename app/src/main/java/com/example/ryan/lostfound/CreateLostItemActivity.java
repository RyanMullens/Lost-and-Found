package com.example.ryan.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateLostItemActivity extends AppCompatActivity {

    EditText mEditTitle;
    EditText mEditDescription;
    EditText mEditEmail;

    JSONObject mLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lost_item);

        mEditTitle   = (EditText)findViewById(R.id.editTitleText);
        mEditDescription   = (EditText)findViewById(R.id.editDescriptionText);
        mEditEmail   = (EditText)findViewById(R.id.editEmailText);

    }



    public void createLostItem(View view){

        String server = getString(R.string.server_url);
        String path = "/lost";

        String url = server + path;

        RequestQueue queue = Volley.newRequestQueue(this);


        JSONObject object = new JSONObject();

        try {
            object.put("title", mEditTitle.getText());
            object.put("description", mEditDescription.getText());
            object.put("location", mLocation);
            object.put("email", mEditEmail.getText());
        }catch(Exception e){
            Toast.makeText(CreateLostItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, object, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(CreateLostItemActivity.this, "Created new lost item", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Toast.makeText(CreateLostItemActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Access the RequestQueue through your singleton class.
        queue.add(jsObjRequest);
    }

    public void getLocation(View view){
        Intent mapIntent = new Intent(this, MapActivity.class);
        startActivityForResult(mapIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String type = data.getStringExtra("type");
                ArrayList<LatLng> list = data.getParcelableArrayListExtra("list");


                mLocation = new JSONObject();
                try {

                    JSONArray locationList = new JSONArray();

                    for(int i = 0;i<list.size();i++){

                        LatLng latLong = list.get(i);

                        JSONObject position = new JSONObject();
                        position.put("lat",latLong.latitude);
                        position.put("long",latLong.longitude);

                        locationList.put(position);
                    }

                    mLocation.put("points", locationList);
                    mLocation.put("typeLocation", type);

                }catch(Exception e){
                    Toast.makeText(CreateLostItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
