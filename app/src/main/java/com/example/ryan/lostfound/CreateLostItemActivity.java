package com.example.ryan.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CreateLostItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lost_item);
    }



    public void createLostItem(View view){
        Toast.makeText(CreateLostItemActivity.this, "Create new lost item", Toast.LENGTH_SHORT).show();
    }

    public void getLocation(View view){
        Toast.makeText(CreateLostItemActivity.this, "Would get location", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(CreateLostItemActivity.this, type, Toast.LENGTH_SHORT).show();
                Toast.makeText(CreateLostItemActivity.this, ""+list.get(0).latitude, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
