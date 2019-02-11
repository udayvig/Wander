package com.example.uday_vig.mapsparticularnearbyplace;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Button mHospitalButton;
    private Button mPoliceStationButton;
    private Location mLocation;

    private FusedLocationProviderClient mFusedLocationClient;
    /*private LocationCallback mHospitalLocationCallback;
    private LocationCallback mPoliceStationLocationCallback;*/

    /*private static final int REQUEST_LOCATION_PERMISSION_HOSPITAL = 1;
    private static final int REQUEST_LOCATION_PERMISSION_POLICE_STATION = 2;*/
    private static final int REQUEST_LOCATION_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                this);

        /*mHospitalLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // locationResult.getLastLocation() has the latest device location.
                new GetNearbyPlacesData().execute(mMap, getUrl(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude(), "hospital"));
            }
        };*/

        /*mPoliceStationLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                new GetNearbyPlacesData().execute(mMap, getUrl(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude(), "police_station"));
            }
        };*/

        allocateLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;

        LatLng home = new LatLng(28.603922, 77.294625);
        float zoom = 20;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, zoom));

        mHospitalButton = findViewById(R.id.hospitalButton);
        mPoliceStationButton = findViewById(R.id.policeStationButton);

        //initialStart();

        mHospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hospitalButtonFunction();
            }
        });

        mPoliceStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                policeStationButtonFunction();
            }
        });
    }

    /*private void initialStart() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }else{
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mHospitalLocationCallback, null);
        }
    }*/

    private void hospitalButtonFunction(){
        new GetNearbyPlacesData().execute(mMap, getUrl(mLocation.getLatitude(), mLocation.getLongitude(), "hospital"));
    }

    private void policeStationButtonFunction(){
        new GetNearbyPlacesData().execute(mMap, getUrl(mLocation.getLatitude(), mLocation.getLongitude(), "police_station"));
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + "1000");
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyD1u6nTZuCFYVPZVwl0haVIZFI1zEiYi2I");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    private void allocateLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            allocateLocation();
        }else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    mLocation = location;
                    //Log.e("location: ", "onSuccess: " + location.getLatitude() + " ," + location.getLongitude());
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    allocateLocation();
                }else{
                    Toast.makeText(this, "Permission Needed :(", Toast.LENGTH_SHORT).show();
                }
                break;

            /*case REQUEST_LOCATION_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    hospitalButtonFunction();
                }else{
                    Toast.makeText(this, "Permission Denied :(", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_LOCATION_PERMISSION_POLICE_STATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    policeStationButtonFunction();
                }else{
                    Toast.makeText(this, "Permission Denied :(", Toast.LENGTH_SHORT).show();
                }
                break;*/

            default: Toast.makeText(this, "Permission Denied :(", Toast.LENGTH_SHORT).show();
        }
    }
}
