package com.barmej.streetissues;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddNewIssueActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION = 1;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 2;
    private static final int REQUEST_GET_PHOTO = 3;
    private static final LatLng DEFAULT_LOCATION = new LatLng(29.3760641, 47.9643571);
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private boolean mLocationPermissionGranted;
    private boolean mReadStorgePermissionGranted;
    private Uri mIssuePhotoUri;
    private FusedLocationProviderClient mLocationProviderClient;
    private Location mLastKnownlocation;
    private LatLng mSelectedLatLng;
    private GoogleMap mGoogleMap;
    private ConstraintLayout mConstraintLayout;
    private ImageView mIssuePhotoImageView;
    private EditText mIssueTitleEditText;
    private EditText mIssueDescEditText;
    private Button mAddIssuebtn;
    private Toolbar toolbar;
    private ProgressDialog mDailog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_issue);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mConstraintLayout = findViewById(R.id.add_issue_cord_layout);
        mIssuePhotoImageView = findViewById(R.id.image_view_issue);
        mIssueTitleEditText = findViewById(R.id.edit_text_issue_title);
        mIssueDescEditText = findViewById(R.id.edit_text_issue_desc);
        mAddIssuebtn = findViewById(R.id.button_add_issue);

        requestLocationPermission();
        requestExternalStoragePermission();
        mIssuePhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcherGalleryIntent();
            }
        });

        mAddIssuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIssueTitleEditText.setError(null);
                mIssueDescEditText.setError(null);
                if (TextUtils.isEmpty(mIssueTitleEditText.getText())) {
                    mIssueTitleEditText.setError(getString(R.string.error_msg_title));
                } else if (TextUtils.isEmpty(mIssueDescEditText.getText())) {
                    mIssueDescEditText.setError(getString(R.string.error_msg_desc));
                } else if (mIssuePhotoUri != null) {
                    addIssueToFirebase();
                }
            }
        });

        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mLocationPermissionGranted) {
            requestDeviceCurrentLocation();
        }
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mSelectedLatLng = latLng;
                mGoogleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(mSelectedLatLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mGoogleMap.addMarker(markerOptions);
            }
        });
    }

    private void addIssueToFirebase() {
        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference photoStorageReference = storageReference.child(UUID.randomUUID().toString());
        firebaseFirestore = FirebaseFirestore.getInstance();
        mDailog = new ProgressDialog(this);
        mDailog.setIndeterminate(true);
        mDailog.setTitle(R.string.app_name);
        mDailog.setMessage(getString(R.string.uploading_photo));
        mDailog.show();
        photoStorageReference.putFile(mIssuePhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    photoStorageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                final Issues issues = new Issues();
                                issues.setTitle(mIssueTitleEditText.getText().toString());
                                issues.setDescription(mIssueDescEditText.getText().toString());
                                issues.setPhoto(task.getResult().toString());
                                issues.setLocation(new GeoPoint(mSelectedLatLng.latitude, mSelectedLatLng.longitude));
                                firebaseFirestore.collection("issues").add(issues).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Snackbar.make(mConstraintLayout, R.string.add_issue_success, Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {
                                                @Override
                                                public void onDismissed(Snackbar transientBottomBar,int event) {
                                                    super.onDismissed(transientBottomBar,event);
                                                    mDailog.dismiss();
                                                    finish();
                                                }
                                            }).show();
                                        } else {
                                            Snackbar.make(mConstraintLayout, R.string.add_issue_failed, Snackbar.LENGTH_LONG).show();
                                            mDailog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Snackbar.make(mConstraintLayout, R.string.uploading_task_failed, Snackbar.LENGTH_LONG).show();
                                mDailog.dismiss();

                            }
                        }
                    });
                } else {
                    Snackbar.make(mConstraintLayout, R.string.uploading_task_failed, Snackbar.LENGTH_LONG).show();
                    mDailog.dismiss();
                }
            }
        });
    }

    private void requestLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_LOCATION);
        }
    }

    private void requestExternalStoragePermission() {
        mReadStorgePermissionGranted = false;
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mReadStorgePermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        }
    }

    private void requestDeviceCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationResult = mLocationProviderClient.getLastLocation();
        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLastKnownlocation = location;
                    mSelectedLatLng = new LatLng(mLastKnownlocation.getLatitude(), mLastKnownlocation.getLongitude());
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


    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_LOCATION:
                mLocationPermissionGranted = false;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    requestDeviceCurrentLocation();

                }
                break;

            case PERMISSION_REQUEST_READ_STORAGE:
                mReadStorgePermissionGranted = false;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mReadStorgePermissionGranted = true;
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_GET_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    mIssuePhotoUri = data.getData();
                    mIssuePhotoImageView.setImageURI(mIssuePhotoUri);
                } catch (Exception e) {
                    Snackbar.make(mConstraintLayout, R.string.photo_selection_error, Snackbar.LENGTH_LONG).show();
                }

            }
        }
    }

    private void launcherGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), REQUEST_GET_PHOTO);
    }
}
