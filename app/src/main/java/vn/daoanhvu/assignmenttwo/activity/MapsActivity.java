package vn.daoanhvu.assignmenttwo.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.databinding.MapActivityBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MapActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R
                        .id.map);
        mapFragment.getMapAsync(this);

        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBJOKhGBRS9jIjnfUVecVzDCBUmMDnL7KI");
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autoComplete);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(20.5, 105.0), // Southwest corner of the bounding box
                new LatLng(21.5, 106.5)  // Northeast corner of the bounding box
        ));

        autocompleteFragment.setPlaceFields(
                Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setCountries(Arrays.asList("VN"));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                System.out.println("Selected");
                LatLng selectedPlaceLatLng = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(selectedPlaceLatLng).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceLatLng, 16));
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle errors
                // You can log the error or display an error message
            }
        });

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