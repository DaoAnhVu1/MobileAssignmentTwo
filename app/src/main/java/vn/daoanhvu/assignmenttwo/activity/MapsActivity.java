package vn.daoanhvu.assignmenttwo.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.databinding.MapActivityBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapActivityBinding binding;

    private EditText searchLocationEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MapActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchLocationEditText = findViewById(R.id.searchLocation);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R
                        .id.map);
        mapFragment.getMapAsync(this);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchLocation();
            }
        });
    }

    private void searchLocation() {
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

// Add a marker in Hanoi and move the camera
        LatLng hanoi = new LatLng(21.0285, 105.8542);
        mMap.addMarker(new MarkerOptions().position(hanoi).title("Marker in Hanoi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 16)); // Adjust the zoom level as needed
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}