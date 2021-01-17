package com.barmej.streetissues;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class IssuesDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String ISSUES_DATA =  "issues_data";
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 1;
    private static final LatLng DEFAULT_LOCATION = new LatLng(29.3760641, 47.9643571);
    private boolean mLocationPermissionGranted;
    private ImageView mIssuesImageView;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private Location mLastKnownlocation;
    private LatLng mSelectedLatLng;
    private FusedLocationProviderClient mLocationProviderClient;
    private Issues issues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIssuesImageView = findViewById(R.id.image_view_issues_photo);
        mTitleTextView = findViewById(R.id.text_view_issues_title);
        mDescriptionTextView = findViewById(R.id.text_view_issues_description);

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            issues = getIntent().getExtras().getParcelable(ISSUES_DATA);
            if (issues != null) {
                getSupportActionBar().setTitle(issues.getTitle());
                mTitleTextView.setText(issues.getTitle());
                mDescriptionTextView.setText(issues.getDescription());
                Glide.with(this).load(issues.getPhoto()).into(mIssuesImageView);

            }
        }
        requestLocationPermission();
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

    }

    private void requestLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            mLocationPermissionGranted = false;
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                requestDeviceCurrentLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mLocationPermissionGranted) {
            requestDeviceCurrentLocation();
        }
        setIssuesMarker();

    }

    private void requestDeviceCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationResult = mLocationProviderClient.getLastLocation();
        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLastKnownlocation = location;
                    mSelectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatLng, 15));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(mSelectedLatLng);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                } else {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));

                }
            }
        });
    }

    public void setIssuesMarker() {
        LatLng latLng = new LatLng(issues.getLocation().getLatitude(),issues.getLocation().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = mGoogleMap.addMarker(markerOptions);
        marker.setTag(issues);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}
