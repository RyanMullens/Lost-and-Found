package com.example.ryan.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void goToCreateLostItem(View v){
        Intent intent = new Intent(this, CreateLostItemActivity.class);
        startActivity(intent);
    }

    public void goToListItems(View v){
        Intent intent = new Intent(this, ListLostItemsActivity.class);
        startActivity(intent);
    }
}
