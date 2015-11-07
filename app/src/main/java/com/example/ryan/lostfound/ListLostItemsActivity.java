package com.example.ryan.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListLostItemsActivity extends AppCompatActivity {

    ArrayList<String> titles;
    ListView listview;
    ArrayAdapter adapter;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_lost_items);

        listview = (ListView) findViewById(R.id.lostItemListView);
        titles = new ArrayList<>();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String item = "bad";

                try {
                    item = jsonArray.getJSONObject(position).getString("_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                goToMap(item);
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        getList();
        renderList();
    }

    private void getList() {
        
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://72.19.65.87:3000/lost";

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        try {
                            titles.clear();
                            jsonArray = response.getJSONArray("items");
                            Log.d("--------", jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = response.getJSONArray("items").getJSONObject(i);
                                titles.add(json.getString("title"));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(ListLostItemsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListLostItemsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void goToMap(String id) {
        Intent mapIntent = new Intent(this, SearchMapsActivity.class);
        mapIntent.putExtra("id", id);
        startActivityForResult(mapIntent, 1);
    }


    private void renderList() {
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, titles);
        listview.setAdapter(adapter);
    }

}

