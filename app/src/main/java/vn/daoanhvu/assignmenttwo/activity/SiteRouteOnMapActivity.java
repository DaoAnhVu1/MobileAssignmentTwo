package vn.daoanhvu.assignmenttwo.activity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import vn.daoanhvu.assignmenttwo.R;
import vn.daoanhvu.assignmenttwo.databinding.SiteRouteOnMapActivityBinding;

public class SiteRouteOnMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    protected FusedLocationProviderClient client;
    private SiteRouteOnMapActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SiteRouteOnMapActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void moveCameraToCurrentLocation() {
        // Get the last known location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();

        } else {
            client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        double destLat = getIntent().getDoubleExtra("lat", 0.0);
                        double destLng = getIntent().getDoubleExtra("long", 0.0);
                        LatLng destinationLatLng = new LatLng(destLat, destLng);
                        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.profile_image);

                        mMap.addMarker(new MarkerOptions().position(currentLocation)
                                .title("Current Location")
                                .icon(customMarker));

                        BitmapDescriptor customMarker2 = BitmapDescriptorFactory.fromResource(R.drawable.leafimage);
                        mMap.addMarker(new MarkerOptions().position(destinationLatLng)
                                .title("Destination")
                                .icon(customMarker2));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));

                    } else {
                        Toast.makeText(SiteRouteOnMapActivity.this, "Last known location is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void drawRoute(LatLng src, LatLng des) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            moveCameraToCurrentLocation();
        }
    }
}