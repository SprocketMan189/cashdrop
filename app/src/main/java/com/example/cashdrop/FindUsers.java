package com.example.cashdrop;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FindUsers extends AppCompatActivity{
    Button signOutBtn;
    TextView userName;
    TextView latitudeView;
    TextView longitudeView;

    LocationManager locationManager;
    String locationProvider;

    double latitude;
    double longitude;

    //testing purposes
    String lat;
    String lon;

    // reference for the fireBase auth library
    private FirebaseAuth mAuth;
    // Listener that detects changes in the user's current authentication state
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        //get a reference to the FireBase auth object
        mAuth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            // does nothing?
        }

        signOutBtn = (Button) findViewById(R.id.signoutBtn);
        latitudeView = (TextView) findViewById(R.id.latitudeView);
        longitudeView = (TextView) findViewById(R.id.longitudeView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 5000, 0, locationListener);


        // log out button
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUserOut();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        //remove auth listener
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void updateUI(FirebaseUser user) {
        userName = (TextView) findViewById(R.id.welcomeText);
        if (user != null) {
            userName.setText(user.getEmail());
        } else {
            userName.setText("Please Sign Back In");
        }
    }

    /**
     * Sign User out
     */
    private void signUserOut() {
        mAuth.signOut();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            Toast.makeText(FindUsers.this, "Could not sign user out", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FindUsers.this, "User signed out", Toast.LENGTH_SHORT).show();
            // if signing out was successful, go to log in page
            Intent signInIntent = new Intent(FindUsers.this, LoginActivity.class);
            startActivity(signInIntent);
        }
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            lat = Double.toString(latitude);
            lon = Double.toString(longitude);

            latitudeView.setText(lat);
            longitudeView.setText(lon);

            // update the database with the new gps coordinate of the user
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



}
