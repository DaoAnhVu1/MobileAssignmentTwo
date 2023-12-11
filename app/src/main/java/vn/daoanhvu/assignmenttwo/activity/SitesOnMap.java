package vn.daoanhvu.assignmenttwo.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.databinding.SitesOnMapActivityBinding;
import vn.daoanhvu.assignmenttwo.model.Site;

public class SitesOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SitesOnMapActivityBinding binding;
    protected FusedLocationProviderClient client;


    private EditText searchLocationEditText;
    private MaterialButton locationSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SitesOnMapActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = LocationServices.getFusedLocationProviderClient(this);

        searchLocationEditText = findViewById(R.id.locationEditText);
        locationSubmitButton = findViewById(R.id.locationSubmit);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
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
            fetchAndAddSiteMarkers();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker clickedMarker) {
                Site clickedSite = (Site) clickedMarker.getTag();
                if (clickedSite != null) {
                    Intent intent = new Intent(SitesOnMap.this, SiteDetailsActivity.class);
                    intent.putExtra("site", clickedSite);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private void fetchAndAddSiteMarkers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }
        db.collection("sites")
                .whereNotEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Site site = document.toObject(Site.class);
                                LatLng siteLocation = new LatLng(site.getLatitude(), site.getLongitude());

                                BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.leafimage);

                                Marker marker = mMap.addMarker(new MarkerOptions().position(siteLocation)
                                        .title(site.getName())
                                        .icon(customMarker));

                                marker.setTag(site);
                            }
                        } else {
                            // Handle the case where fetching data fails
                            Toast.makeText(SitesOnMap.this, "Failed to fetch sites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.profile_image);

                        mMap.addMarker(new MarkerOptions().position(currentLocation)
                                .title("Current Location")
                                .icon(customMarker));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

                    } else {
                        // Display a toast message indicating that the location is null
                        Toast.makeText(SitesOnMap.this, "Last known location is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}