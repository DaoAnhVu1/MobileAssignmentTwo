package vn.daoanhvu.assignmenttwo.activity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;


import java.io.IOException;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.databinding.LocationPickerActivityBinding;

public class LocationPickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationPickerActivityBinding binding;
    protected FusedLocationProviderClient client;


    private EditText searchLocationEditText;
    private MaterialButton locationSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LocationPickerActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = LocationServices.getFusedLocationProviderClient(this);

        searchLocationEditText = findViewById(R.id.locationEditText);
        locationSubmitButton = findViewById(R.id.locationSubmit);

        locationSubmitButton.setOnClickListener(v -> {
            String locationName = searchLocationEditText.getText().toString();

            if (!locationName.isEmpty()) {
                Geocoder geocoder = new Geocoder(this);

                try {
                    List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng location = new LatLng(address.getLatitude(), address.getLongitude());

                        mMap.clear(); // Clear existing markers
                        mMap.addMarker(new MarkerOptions().position(location).title(locationName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
                    } else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error searching for location", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // If location permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            // If permission is granted, move the camera to the current location
            moveCameraToCurrentLocation();
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(latLng -> {
            // Toast the LatLng when the map is clicked
           getAddressFromLatLng(latLng);
        });
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressName = address.getAddressLine(0); // You can customize this based on your needs

                // Create an intent to send back the result
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedAddress", addressName);
                resultIntent.putExtra("selectedLatLng", latLng);
                setResult(RESULT_OK, resultIntent);

                // Finish the activity
                finish();
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCameraToCurrentLocation() {
        // Get the last known location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Display a toast message indicating that permission is not granted
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();

        } else {
            client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Move the camera to the current location with a zoom level of 16
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

                    } else {
                        // Display a toast message indicating that the location is null
                        Toast.makeText(LocationPickerActivity.this, "Last known location is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}