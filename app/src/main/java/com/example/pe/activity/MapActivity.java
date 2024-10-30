package com.example.pe.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.pe.R;
import com.example.pe.model.Child;
import com.example.pe.model.Parent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Child child;
    private RequestQueue requestQueue;
    private TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        child = (Child) getIntent().getSerializableExtra("child");

        requestQueue = Volley.newRequestQueue(this);
        addressTextView = findViewById(R.id.address_text);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("ggmap", child.getField5());
        geocodeAddress(child.getField5());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void geocodeAddress(String address) {
        String url = "https://nominatim.openstreetmap.org/search?q=" + address + "&format=json&addressdetails=1";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject location = response.getJSONObject(0);
                                double lat = location.getDouble("lat");
                                double lng = location.getDouble("lon");
                                String displayName = location.getString("display_name");

                                addressTextView.setText("Vĩ độ: " + lat + "\nKinh độ: " + lng + "\n" + displayName);

                                LatLng parentLocation = new LatLng(lat, lng);
                                Log.d("map", "onResponse: "+mMap);
                                if (mMap != null) {
                                    mMap.addMarker(new MarkerOptions().position(parentLocation).title(child.getField5()));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parentLocation, 15));
                                }
                            } else {
                                Toast.makeText(MapActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MapActivity.this, "Không thể phân tích vị trí", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(MapActivity.this, "Không thể lấy vị trí", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }
}
