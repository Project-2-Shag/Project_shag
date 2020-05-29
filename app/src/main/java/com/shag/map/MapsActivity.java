package com.shag.map;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.directions.route.AbstractRouting;
//import com.directions.route.BuildConfig;
import com.directions.route.BuildConfig;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.shag.BuildConfig;
import com.shag.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.shag.map.Utils.readEncodedPolyLinePointsFromCSV;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        com.google.android.gms.location.LocationListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener,
        RoutingListener
{
    private static final String LOG_TAG = MapsActivity.class.getName();

    private RecyclerView mDriverListRecyclerView;
    private RelativeLayout mMapContainer;

    // bound values for camera focus on app start
    private static final LatLng BOUND1 = new LatLng( 47.207904, 38.943254);
    private static final LatLng BOUND2 = new LatLng(47.210295, 38.937009);

    private static final float DIRECTION_ARROW_WIDTH = 400f;

    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // important: these keywords values must be exactly the same as ones in polylines.csv file in raw folder
    public static final String MAIN_MARKER = "mainMarker";
    public static final String LINE_BLUE = "lineBlue";
    public static final String LINE_ORANGE = "lineOrange";
    public static final String LINE_VIOLET = "lineViolet";
    public static final String LINE_GREEN = "lineGreen";
    public static final String LINE_PINK = "linePink";
    public static final String MARKER = "marker";
    public static final String LAT_LNG_POINT = "latLngPoint";

    private GoogleMap mMap;
    SearchView searchView;
    LatLng searchLatLng;

    String[][] busStops =
    {
            {
            "31, 47.209312, 38.941226, Добролюбовский",
            "31, 47.213866, 38.934799, Итальянский",
            "31, 47.211375, 38.932284, Тургеневский",
            "31, 47.205330, 38.938383, Некрасовский",
            "31, 47.209312, 38.941226, Добролюбовский"
            },

            {
             "6, 47.206943, 38.925618, Магнит",
             "6, 47.211682, 38.910803, Смирновский",
             "6, 47.208621, 38.911076, Карл Либнехт",
             "6, 47.204089, 38.918993, Антон Глушко",
             "6, 47.203603, 38.925714, Итальянский",
             "6, 47.206943, 38.925618, Магнит"
            }
    };

    int pickUpI;
    int pickUpJ;
    int endUpI;
    int endUpJ;

    //Button reserveButton;
    boolean reserve = false;
    boolean getBus = false;

    AutocompleteSupportFragment autocompleteSupportFragment;
    AutocompleteSupportFragment autocompleteFragment;

    private List<LatLng> places = new ArrayList<>();
    private int width;

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    //protected GoogleApiClient mGoogleApiClient;



    // Polyline instances
    private Polyline mMutablePolylineBlue;
    private Polyline mMutablePolylineOrange;
    private Polyline mMutablePolylineViolet;
    private Polyline mMutablePolylineGreen;
    private Polyline mMutablePolylinePink;

    // list of GroundOverlay objects
    private List<GroundOverlay> mGroundOverlay = new ArrayList<>();

    /* *************************************************************************************
     * Below is all declarations for Location service
     */

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-transfusionTime-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    /* *************************************************************************************/

    boolean tempGetBus = false;
    //ImageButton reserveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        findViewById(R.id.reserveButton).setOnClickListener(this);;

        polylines = new ArrayList<>();



        //searchView = findViewById(R.id.autocomplete_fragment);

        autocompleteSupportFragment = (AutocompleteSupportFragment )
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);

        autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(47.204850, 38.939889),
                new LatLng(47.206549, 38.934647)));
        autocompleteSupportFragment.setCountry("RU");

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        //setContentView(R.layout.fragment_driver_list);
        mMapContainer = findViewById(R.id.map_container);
        mDriverListRecyclerView = findViewById(R.id.driver_list_recycler_view);
        findViewById(R.id.btn_full_screen_map).setVisibility(View.GONE);
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //autocompleteSupportFragment.getView().setBackgroundColor(Color.WHITE);



        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.unrestricted_map_key));
        Places.createClient(this);
        //setAutocompleteFragment();

        // initialize mRequestingLocationUpdates and mLastUpdateTime
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // initialize FusedLocationProviderClient and SettingsClient object to invoke location settings
        // on app first time startup
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        // Kick off the process of building the LocationCallback, LocationRequest, and
        // LocationSettingsRequest objects.
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates to save battery.
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove location updates to save battery.
        stopLocationUpdates();
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
    public void onMapReady(GoogleMap googleMap)
    {
        // initialize map object
        mMap = googleMap;

        // set up location buttons and onclick events
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(@NonNull final Place place)
            {
                if (place.getLatLng() != null)
                {
                    //double latitude = place.getLatLng().latitude;
                    //double longitude = place.getLatLng().longitude;
                    String name = place.getName();
                    // Creating a marker
                    final MarkerOptions markerOptions = new MarkerOptions();

                    //Toast.makeText(getApplicationContext(), String.valueOf(place.getLatLng()), Toast.LENGTH_LONG).show();
                    // Setting the position for the marker
                    markerOptions.position(place.getLatLng());

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker

                    markerOptions.title(name);

                    // Clears the previously touched position

                    mMap.clear();


                    searchLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    String[] currentStart;
                    String[] currentEnd;
                    String currentWay;
                    double currentLatitude;
                    double currentLongitude;

                    boolean checkFlag = true;
                    float minDist = 10000000;
                    int minI = -1;
                    int minJ = -1;

                    boolean checkEndFlag = true;
                    float minEndDist = 100000000;
                    int minEndI = -1;
                    int minEndJ = -1;

                    boolean checkAllFlag = true;
                    float minAllDist = 100000000;
                    int minAllI = -1;
                    int minAllJ = -1;
                    int minEndAllI = -1;
                    int minEndAllJ = -1;

                    //getRouteToMarker(place.getLatLng());

                    for (int i = 0; i < busStops.length; i++)
                    {
                        for (int j = 0; j < busStops[i].length; j++)
                        {
                            currentStart = busStops[i][j].split(",");

                            final float[] minStart = new float[10];
                            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                    Double.parseDouble(currentStart[1]), Double.parseDouble(currentStart[2]),
                                    minStart);

                            if (checkFlag)
                            {
                                minDist = minStart[0];
                                minI = i;
                                minJ = j;
                                checkFlag = false;
                            }
                            else if (minStart[0] < minDist)
                            {
                                minDist = minStart[0];
                                minI = i;
                                minJ = j;
                            }
                        }

                        for (int j = 0; j < busStops[i].length; j++)
                        {
                            currentEnd = busStops[i][j].split(",");

                            final float[] minEnd = new float[10];
                            Location.distanceBetween(place.getLatLng().latitude, place.getLatLng().longitude,
                                    Double.parseDouble(currentEnd[1]), Double.parseDouble(currentEnd[2]),
                                    minEnd);

                            if (checkEndFlag && j != minJ)
                            {
                                minEndDist = minEnd[0];
                                minEndI = i;
                                minEndJ = j;
                                checkEndFlag = false;
                            }
                            else if (minEnd[0] < minEndDist && j != minJ)
                            {
                                minEndDist = minEnd[0];
                                minEndI = i;
                                minEndJ = j;
                            }
                        }

                        float tempAllDist = minDist + minEndDist;
                        if (checkAllFlag)
                        {
                            minAllDist = minDist + minEndDist;
                            minAllI = minI;
                            minAllJ = minJ;
                            minEndAllI = minEndI;
                            minEndAllJ = minEndJ;
                            checkAllFlag = false;

                            checkFlag = true;
                            minDist = 10000000;
                            minI = -1;
                            minJ = -1;

                            checkEndFlag = true;
                            minEndDist = 100000000;
                            minEndI = -1;
                            minEndJ = -1;
                        }
                        else if (tempAllDist < minAllDist)
                        {
                            minAllDist = minDist + minEndDist;
                            minAllI = minI;
                            minAllJ = minJ;
                            minEndAllI = minEndI;
                            minEndAllJ = minEndJ;

                            checkFlag = true;
                            minDist = 10000000;
                            minI = -1;
                            minJ = -1;

                            checkEndFlag = true;
                            minEndDist = 100000000;
                            minEndI = -1;
                            minEndJ = -1;
                        }
                    }

                    String[] allBusDist1;
                    String[] allBusDist2;
                    if (minEndAllJ > minAllJ)
                    {
                        if (minAllJ + 1 == minEndAllJ)
                        {
                            allBusDist1 = busStops[minAllI][minAllJ].split(",");
                            allBusDist2 = busStops[minEndAllI][minEndAllJ].split(",");

                            final float[] minAllBusDist = new float[10];
                            Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                    Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                    minAllBusDist);

                            final float[] minAllWalkDist = new float[10];
                            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                    place.getLatLng().latitude, place.getLatLng().longitude,
                                    minAllWalkDist);


                            float timeWithBus = (float) ((minAllDist / 1.7) + (minAllBusDist[0] / 16.7));
                            float timeWithWalk = (float) (minAllWalkDist[0] / 1.7);

                            if (timeWithBus <= timeWithWalk)
                            {
                                getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                        new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
                                        "walk");

                                addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                                addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                                drawRoute(allBusDist1[0], minAllJ, minEndAllJ);

                                getRouteToMarker(new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
                                        place.getLatLng(),
                                        "walk");

                                reserve = true;
                                pickUpI = minAllI;
                                pickUpJ = minAllJ;
                                endUpI = minEndAllI;
                                endUpJ = minEndAllJ;
                            }
                            else
                            {
                                getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), place.getLatLng(), "walk");
                                reserve = false;
                            }
                        }
                        else
                        {
                            float AllBusDist = 0f;
                            for (int j = minAllJ; j < minEndAllJ - 1; j++)
                            {
                                allBusDist1 = busStops[minAllI][j].split(",");
                                allBusDist2 = busStops[minEndAllI][j + 1].split(",");

                                final float[] minAllBusDist = new float[10];
                                Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                        Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                        minAllBusDist);

                                AllBusDist += minAllBusDist[0];
                            }

                            final float[] minAllWalkDist = new float[10];
                            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                    place.getLatLng().latitude, place.getLatLng().longitude,
                                    minAllWalkDist);

                            float timeWithBus = (float) ((minAllDist / 1.7) + (AllBusDist / 16.7));
                            float timeWithWalk = (float) (minAllWalkDist[0] / 1.7);

                            if (timeWithBus <= timeWithWalk)
                            {
                                allBusDist1 = busStops[minAllI][minAllJ].split(",");
                                allBusDist2 = busStops[minEndAllI][minEndAllJ].split(",");

                                getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                        new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
                                        "walk");
                                getRouteToMarker(new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
                                        place.getLatLng(),
                                        "walk");

                                for (int j = minAllJ; j < minEndAllJ; j++)
                                {
                                    allBusDist1 = busStops[minAllI][j].split(",");
                                    allBusDist2 = busStops[minEndAllI][j + 1].split(",");

//                                    getRouteToMarker(new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
//                                            new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
//                                            "route");
                                    addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                                    addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                                    drawRoute(allBusDist1[0], j, j+1);
                                }

                                reserve = true;
                                pickUpI = minAllI;
                                pickUpJ = minAllJ;
                                endUpI = minEndAllI;
                                endUpJ = minEndAllJ;
                            }
                            else
                            {
                                getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), place.getLatLng(), "walk");
                                reserve = false;
                            }
                        }
                    }
                    else
                    {
                        float AllBusDist = 0f;

                        if (minAllJ != busStops[minAllI].length - 1)
                        {
                            for (int j = minAllJ; j < busStops[minAllI].length - 1; j++)
                            {
                                allBusDist1 = busStops[minAllI][j].split(",");
                                allBusDist2 = busStops[minAllI][j + 1].split(",");

                                final float[] minAllBusDist = new float[10];
                                Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                        Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                        minAllBusDist);

                                AllBusDist += minAllBusDist[0];
                            }
                        }

                        if (minEndAllJ != 0)
                        {
                            for (int j = 0; j < minEndAllJ - 1; j++)
                            {
                                allBusDist1 = busStops[minAllI][j].split(",");
                                allBusDist2 = busStops[minEndAllI][j + 1].split(",");

                                final float[] minAllBusDist = new float[10];
                                Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                        Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                        minAllBusDist);

                                AllBusDist += minAllBusDist[0];
                            }
                        }

                        final float[] minAllWalkDist = new float[10];
                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                place.getLatLng().latitude, place.getLatLng().longitude,
                                minAllWalkDist);

                        float timeWithBus = (float) ((minAllDist / 1.7) + (AllBusDist / 16.7));
                        float timeWithWalk = (float) (minAllWalkDist[0] / 1.7);

                        if (timeWithBus <= timeWithWalk)
                        {
                            allBusDist1 = busStops[minAllI][minAllJ].split(",");
                            allBusDist2 = busStops[minEndAllI][minEndAllJ].split(",");

                            getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                    new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
                                    "walk");
                            getRouteToMarker(new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
                                    place.getLatLng(),
                                    "walk");

                            if (minAllJ != busStops[minAllI].length - 1)
                            {
                                for (int j = minAllJ; j < busStops[minAllI].length - 1; j++)
                                {
                                    allBusDist1 = busStops[minAllI][j].split(",");
                                    allBusDist2 = busStops[minEndAllI][j + 1].split(",");

//                                    getRouteToMarker(new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
//                                            new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
//                                            "route");
                                    addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                                    addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                                    drawRoute(allBusDist1[0], j, j+1);
                                }
                            }

                            //if (minEndAllJ != 0)
                            //{
                                for (int j = 0; j < minEndAllJ; j++)
                                {
                                    allBusDist1 = busStops[minEndAllI][j].split(",");
                                    allBusDist2 = busStops[minEndAllI][j + 1].split(",");

//                                    getRouteToMarker(new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
//                                            new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
//                                            "route");
                                    addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                                    addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                                    drawRoute(allBusDist1[0], j, j+1);
                                }
                            //}
                            reserve = true;
                            pickUpI = minAllI;
                            pickUpJ = minAllJ;
                            endUpI = minEndAllI;
                            endUpJ = minEndAllJ;
                        }
                        else
                        {
                            getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), place.getLatLng(), "walk");
                            reserve = false;
                        }
                    }

                    //String[] minFinal;
                    //minFinal = busStops[minAllI][minAllJ].split(",");
                    //getRouteToMarker(new LatLng(Double.parseDouble(minFinal[1]), Double.parseDouble(minFinal[2])));

//                    final float[] results = new float[10];
//                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
//                            place.getLatLng().latitude, place.getLatLng().longitude,
//                            results);
                    // mMap.addMarker(new MarkerOptions().position(searchLatLng).title("L").snippet("Destination = " + results[0]));
                    // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 10));

                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            // Animating to the touched position
                            //markerOptions.snippet("Destination = " + results[0]);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13.3f));

                            // Placing a marker on the touched position

                            mMap.addMarker(markerOptions);
                        }
                    });

                }

            }

            @Override
            public void onError(@NonNull Status status)
            {
                //Убрать
                //Toast.makeText(getApplicationContext(), "Error: " + status, Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
        {
            @Override
            public void onMapLongClick(LatLng place) {
                final MarkerOptions markerOptions = new MarkerOptions();

                //Toast.makeText(getApplicationContext(), String.valueOf(place.getLatLng()), Toast.LENGTH_LONG).show();
                // Setting the position for the marker
                markerOptions.position(place);

                // Setting the title for the marker.
                // This will be displayed on taping the marker

                markerOptions.title("");

                // Clears the previously touched position

                mMap.clear();

                searchLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                String[] currentStart;
                String[] currentEnd;
                String currentWay;
                double currentLatitude;
                double currentLongitude;

                boolean checkFlag = true;
                float minDist = 10000000;
                int minI = -1;
                int minJ = -1;

                boolean checkEndFlag = true;
                float minEndDist = 100000000;
                int minEndI = -1;
                int minEndJ = -1;

                boolean checkAllFlag = true;
                float minAllDist = 100000000;
                int minAllI = -1;
                int minAllJ = -1;
                int minEndAllI = -1;
                int minEndAllJ = -1;

                //getRouteToMarker(place.getLatLng());

                for (int i = 0; i < busStops.length; i++) {
                    for (int j = 0; j < busStops[i].length; j++) {
                        currentStart = busStops[i][j].split(",");

                        final float[] minStart = new float[10];
                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                Double.parseDouble(currentStart[1]), Double.parseDouble(currentStart[2]),
                                minStart);

                        if (checkFlag) {
                            minDist = minStart[0];
                            minI = i;
                            minJ = j;
                            checkFlag = false;
                        } else if (minStart[0] < minDist) {
                            minDist = minStart[0];
                            minI = i;
                            minJ = j;
                        }
                    }

                    for (int j = 0; j < busStops[i].length; j++) {
                        currentEnd = busStops[i][j].split(",");

                        final float[] minEnd = new float[10];
                        Location.distanceBetween(place.latitude, place.longitude,
                                Double.parseDouble(currentEnd[1]), Double.parseDouble(currentEnd[2]),
                                minEnd);

                        if (checkEndFlag && j != minJ) {
                            minEndDist = minEnd[0];
                            minEndI = i;
                            minEndJ = j;
                            checkEndFlag = false;
                        } else if (minEnd[0] < minEndDist && j != minJ) {
                            minEndDist = minEnd[0];
                            minEndI = i;
                            minEndJ = j;
                        }
                    }

                    float tempAllDist = minDist + minEndDist;
                    if (checkAllFlag) {
                        minAllDist = minDist + minEndDist;
                        minAllI = minI;
                        minAllJ = minJ;
                        minEndAllI = minEndI;
                        minEndAllJ = minEndJ;
                        checkAllFlag = false;

                        checkFlag = true;
                        minDist = 10000000;
                        minI = -1;
                        minJ = -1;

                        checkEndFlag = true;
                        minEndDist = 100000000;
                        minEndI = -1;
                        minEndJ = -1;
                    } else if (tempAllDist < minAllDist) {
                        minAllDist = minDist + minEndDist;
                        minAllI = minI;
                        minAllJ = minJ;
                        minEndAllI = minEndI;
                        minEndAllJ = minEndJ;

                        checkFlag = true;
                        minDist = 10000000;
                        minI = -1;
                        minJ = -1;

                        checkEndFlag = true;
                        minEndDist = 100000000;
                        minEndI = -1;
                        minEndJ = -1;
                    }
                }

                String[] allBusDist1;
                String[] allBusDist2;
                if (minEndAllJ > minAllJ) {
                    if (minAllJ + 1 == minEndAllJ) {
                        allBusDist1 = busStops[minAllI][minAllJ].split(",");
                        allBusDist2 = busStops[minEndAllI][minEndAllJ].split(",");

                        final float[] minAllBusDist = new float[10];
                        Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                minAllBusDist);


                        final float[] minAllWalkDist = new float[10];
                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                place.latitude, place.longitude,
                                minAllWalkDist);


                        float timeWithBus = (float) ((minAllDist / 1.7) + (minAllBusDist[0] / 16.7));
                        float timeWithWalk = (float) (minAllWalkDist[0] / 1.7);

                        if (timeWithBus <= timeWithWalk) {
                            getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                    new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
                                    "walk");

                            addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                            addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                            drawRoute(allBusDist1[0], minAllJ, minEndAllJ);

                            getRouteToMarker(new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
                                    place,
                                    "walk");

                            reserve = true;
                            pickUpI = minAllI;
                            pickUpJ = minAllJ;
                            endUpI = minEndAllI;
                            endUpJ = minEndAllJ;
                        } else {
                            getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), place, "walk");
                            reserve = false;
                        }
                    } else {
                        float AllBusDist = 0f;
                        for (int j = minAllJ; j < minEndAllJ - 1; j++) {
                            allBusDist1 = busStops[minAllI][j].split(",");
                            allBusDist2 = busStops[minEndAllI][j + 1].split(",");

                            final float[] minAllBusDist = new float[10];
                            Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                    Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                    minAllBusDist);

                            AllBusDist += minAllBusDist[0];
                        }

                        final float[] minAllWalkDist = new float[10];
                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                                place.latitude, place.longitude,
                                minAllWalkDist);

                        float timeWithBus = (float) ((minAllDist / 1.7) + (AllBusDist / 16.7));
                        float timeWithWalk = (float) (minAllWalkDist[0] / 1.7);

                        if (timeWithBus <= timeWithWalk) {
                            allBusDist1 = busStops[minAllI][minAllJ].split(",");
                            allBusDist2 = busStops[minEndAllI][minEndAllJ].split(",");

                            getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                    new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
                                    "walk");
                            getRouteToMarker(new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
                                    place,
                                    "walk");

                            for (int j = minAllJ; j < minEndAllJ; j++) {
                                allBusDist1 = busStops[minAllI][j].split(",");
                                allBusDist2 = busStops[minEndAllI][j + 1].split(",");

//                                    getRouteToMarker(new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
//                                            new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
//                                            "route");
                                addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                                addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                                drawRoute(allBusDist1[0], j, j + 1);

                            }
                            reserve = true;
                            pickUpI = minAllI;
                            pickUpJ = minAllJ;
                            endUpI = minEndAllI;
                            endUpJ = minEndAllJ;
                        } else {
                            getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), place, "walk");
                            reserve = false;
                        }
                    }
                } else {
                    float AllBusDist = 0f;

                    if (minAllJ != busStops[minAllI].length - 1) {
                        for (int j = minAllJ; j < busStops[minAllI].length - 1; j++) {
                            allBusDist1 = busStops[minAllI][j].split(",");
                            allBusDist2 = busStops[minAllI][j + 1].split(",");

                            final float[] minAllBusDist = new float[10];
                            Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                    Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                    minAllBusDist);

                            AllBusDist += minAllBusDist[0];
                        }
                    }

                    if (minEndAllJ != 0) {
                        for (int j = 0; j < minEndAllJ - 1; j++) {
                            allBusDist1 = busStops[minAllI][j].split(",");
                            allBusDist2 = busStops[minEndAllI][j + 1].split(",");

                            final float[] minAllBusDist = new float[10];
                            Location.distanceBetween(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2]),
                                    Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2]),
                                    minAllBusDist);

                            AllBusDist += minAllBusDist[0];
                        }
                    }

                    final float[] minAllWalkDist = new float[10];
                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                            place.latitude, place.longitude,
                            minAllWalkDist);

                    float timeWithBus = (float) ((minAllDist / 1.7) + (AllBusDist / 16.7));
                    float timeWithWalk = (float) (minAllWalkDist[0] / 1.7);

                    if (timeWithBus <= timeWithWalk) {
                        allBusDist1 = busStops[minAllI][minAllJ].split(",");
                        allBusDist2 = busStops[minEndAllI][minEndAllJ].split(",");

                        getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                                new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])),
                                "walk");
                        getRouteToMarker(new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])),
                                place,
                                "walk");

                        if (minAllJ != busStops[minAllI].length - 1) {
                            for (int j = minAllJ; j < busStops[minAllI].length - 1; j++) {
                                allBusDist1 = busStops[minAllI][j].split(",");
                                allBusDist2 = busStops[minEndAllI][j + 1].split(",");

                                addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                                addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                                drawRoute(allBusDist1[0], j, j + 1);

                            }
                        }

                        //if (minEndAllJ != 0)
                        //{
                        for (int j = 0; j < minEndAllJ; j++) {
                            allBusDist1 = busStops[minEndAllI][j].split(",");
                            allBusDist2 = busStops[minEndAllI][j + 1].split(",");

                            addBusMarker(allBusDist1[0], new LatLng(Double.parseDouble(allBusDist1[1]), Double.parseDouble(allBusDist1[2])), allBusDist1[3]);
                            addBusMarker(allBusDist2[0], new LatLng(Double.parseDouble(allBusDist2[1]), Double.parseDouble(allBusDist2[2])), allBusDist2[3]);
                            drawRoute(allBusDist1[0], j, j + 1);
                        }

                        reserve = true;
                        pickUpI = minAllI;
                        pickUpJ = minAllJ;
                        endUpI = minEndAllI;
                        endUpJ = minEndAllJ;
                        //}
                    } else {
                        getRouteToMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), place, "walk");
                        reserve = false;
                    }
                }
            }
        });



        // set up grey style for the map
        setMapStyle();

        // move camera to schoolies area
        //moveCameraToSchooliesArea();

        // draw all polylines
        //drawAllPolyLines();

        // set up custom info window for marker's onclick event
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        mMap.setInfoWindowAdapter(customInfoWindow);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // if returning true, nothing will appear when a marker clicked
                // if returning false, default behavior will be executed (info window occurs)
                return marker.getSnippet() == null;
            }
        });

        if (tempGetBus)
        {
            getBus = true;
        }

        // all all markers (bus stops) to the map
        //addAllBusStopMarkers();




        // add all
        //addAllAnnotationsAsMarkers();

        // add all direction arrows
        //addAllDirectionArrowsAsGroundOverlay();
    }

    /**
     * Click handler for clamping to Schoolies bus area.
     */
    public void moveCameraToSchooliesArea() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(BOUND1)
                        .include(BOUND2)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }
        });
    }

    /**
     * Creates a {@link MapStyleOptions} object via loadRawResourceStyle() (or via the
     * constructor with a JSON String), then sets it on the {@link GoogleMap} instance,
     * via the setMapStyle() method.
     */
    private void setMapStyle() {
        // Sets the grayscale style via raw resource JSON.
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_grayscale);
        mMap.setMapStyle(style);
    }


    private void drawRoute(String way, int start, int end)
    {
        mMap.addPolyline(new PolylineOptions()
                .color(getResources().getColor(R.color.colorPolyLineBlue)) // Line color.
                .width(20f) // Line width.
                .clickable(false) // Able to click or not.
                .addAll(readEncodedPolyLinePointsFromCSV(this, "way"+way+"line"+start+"to"+end)));
    }
    private void drawAllPolyLines() {
        // Add a blue Polyline.
        mMap.addPolyline(new PolylineOptions()
                .color(getResources().getColor(R.color.colorPolyLineBlue)) // Line color.
                .width(20f) // Line width.
                .clickable(false) // Able to click or not.
                .addAll(readEncodedPolyLinePointsFromCSV(this, LINE_BLUE))); // all the whole list of lat lng value pairs which is retrieved by calling helper method readEncodedPolyLinePointsFromCSV.

        mMap.addPolyline(new PolylineOptions()
                .color(getResources().getColor(R.color.colorPolyLineGray)) // Line color.
                .width(15f) // Line width.
                .clickable(false) // Able to click or not.
                .addAll(readEncodedPolyLinePointsFromCSV(this, LINE_ORANGE)));

        mMap.addPolyline(new PolylineOptions()
                .color(getResources().getColor(R.color.colorPolyLineGray)) // Line color.
                .width(15f) // Line width.
                .clickable(false) // Able to click or not.
                .addAll(readEncodedPolyLinePointsFromCSV(this, LINE_VIOLET)));
    }

    // helper method to read polyline points from CSV file
    private List<LatLng> readPolyLinePointsFromCSV(String lineKeyword) {
        InputStream is = getResources().openRawResource(R.raw.polylines);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        List<LatLng> latLngList = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");
                // only add the right latlng points to a desired line by color
                if (tokens[0].trim().equals(lineKeyword) && tokens[1].trim().equals(LAT_LNG_POINT)) {
                    latLngList.add(new LatLng(Double.parseDouble(tokens[2].trim()), Double.parseDouble(tokens[3].trim())));
                } else {
                    Log.d("trienPoly", " null");
                }
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }

        for (LatLng lat : latLngList) {
            Log.d("trienPoly" + lineKeyword, lat.latitude + ", " + lat.longitude);
        }
        return latLngList;
    }

    // helper method to read markers lat lng values from CSV file
    private List<LatLng> readMarkersFromCSV(String lineKeyword) {
        InputStream is = getResources().openRawResource(R.raw.polylines);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        List<LatLng> latLngList = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");
                // only add the right latlng points to a desired line by color
                if (tokens[0].trim().equals(lineKeyword) && tokens[1].trim().equals(MARKER)) {
                    latLngList.add(new LatLng(Double.parseDouble(tokens[2].trim()), Double.parseDouble(tokens[3].trim())));
                    Log.d("trienMarker" + lineKeyword, tokens[2].trim() + tokens[3].trim());
                } else {
                    Log.d("trienMarker", " null");
                }
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }

        return latLngList;
    }


    private void addBusMarker(String way, LatLng stopLocation, String stopInformation)
    {
        String snippet = "Маршрут - " + way + ". Остановка - " + stopInformation;
        Bitmap resizedBitmap = resizeMarker(R.drawable.marker_blue_light);
        mMap.addMarker(new MarkerOptions()
                .position(stopLocation)
                .title(" ")
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
    }

    // bitmap resize tool
    private Bitmap resizeMarker(int drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 60, (bitmap.getHeight()* 60)/(bitmap.getWidth()), false);
    }

    // bitmap resize tool
    private Bitmap resizeCommonAnnotation(int drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();

        int newWidth = (int) (bitmapDrawable.getBitmap().getWidth()*0.15);
        int newHeight = (int) (bitmapDrawable.getBitmap().getHeight()*0.15);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

    // bitmap resize tool
    private Bitmap resizeBitmap(int drawable, int width) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, width, (bitmap.getHeight()* 60)/(bitmap.getWidth()), false);
    }

    /* *************************************************************************************
     *************************************************************************************
     *************************************************************************************
     *************************************************************************************
     * Location and permission supporting methods
     */

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mLastLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(MapsActivity.this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Preserved method to get Last known location which can be used in User Preferences later.
     */

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mLastLocation from the Bundle and move the camera to the last
            // known location.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mLastLocation
                // is not null.
                setLastLocation((Location) savedInstanceState.getParcelable(KEY_LOCATION));
                // Move camera to the last known location
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }

        }
    }

    Marker markerName;
    boolean firstMarkerCheck = false;
    private void setLastLocation(Location lastLocation) {

        mLastLocation = lastLocation;

        if (getBus)
        {
            final String[] BusStop1 = busStops[pickUpI][pickUpJ].split(",");
            final String[] BusStop2 = busStops[endUpI][endUpJ].split(",");

            final DatabaseReference ref;
            ref = FirebaseDatabase.getInstance().getReference().child("DriverAvailable");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean flag = false;
                    double busLat = 0;
                    double busLong = 0;
                    for (DataSnapshot ds: dataSnapshot.getChildren())
                    {
                        String driverWayToCheck = ds.child("way").getValue().toString();
                        String ischeck = ds.child("issharing").getValue().toString();
                        busLat = (Double) ds.child("latitude").getValue();;
                        busLong = (Double) ds.child("longitude").getValue();;
                        if (BusStop1[0].equals(driverWayToCheck) && ischeck.equals("true"))
                        {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag)
                    {
                        Toast.makeText(getApplicationContext(), "Водитель в данный момент недоступен", Toast.LENGTH_LONG).show();
                        getBus = false;
                    }
                    else
                    {
                        //Toast.makeText(getApplicationContext(), "Успех 2", Toast.LENGTH_LONG).show();
                        final float[] walkToStop = new float[10];
                        Location.distanceBetween(Double.parseDouble(BusStop1[1]), Double.parseDouble(BusStop1[2]),
                                busLat, busLong,
                                walkToStop);

                        final float[] walkToEnd = new float[10];
                        Location.distanceBetween(Double.parseDouble(BusStop2[1]), Double.parseDouble(BusStop2[2]),
                                busLat, busLong,
                                walkToEnd);

                        if (walkToStop[0] < 50)
                        {
                            DatabaseReference dr = FirebaseDatabase.getInstance().getReference("request").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            dr.removeValue();
                        }

                        Bitmap resizedBitmap = resizeMarker(R.drawable.bus);

                        if (firstMarkerCheck) {
                            markerName.remove();
                        }
                        if (walkToEnd[0] > 50) {
                            markerName = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(busLat, busLong))
                                    .title("Автобус")
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                            firstMarkerCheck = true;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                setLastLocation(locationResult.getLastLocation());
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            }
        };
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This app uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real time location
     * updates.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(LOG_TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(LOG_TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * actions after users choose to or not to change location settings
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(LOG_TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOG_TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(LOG_TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(LOG_TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted (disabled as we
                // use another icon in place of default my location btn)
                enableMyLocation();
            } else {
                // Display the missing permission error dialog when the fragments resume.
                mPermissionDenied = true;
            }
        } else {
            return;
        }
    }


    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(LOG_TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        try {
                            mFusedLocationClient.getLastLocation()
                                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Location> task) {
                                            if (task.isSuccessful() && task.getResult() != null) {

                                                setLastLocation(task.getResult());
                                                // move camera to current location
                                                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                                                // mMap.animateCamera(cameraUpdate);
                                                mMap.moveCamera(cameraUpdate);

                                            } else {
                                                Log.w(LOG_TAG, "Failed to get location.");
                                            }
                                        }
                                    });
                        } catch (SecurityException unlikely) {
                            Log.e(LOG_TAG, "Lost location permission." + unlikely);
                        }


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(LOG_TAG,
                                        "Location settings are not satisfied. Attempting to upgrade " +
                                                "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MapsActivity.this,
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(LOG_TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(LOG_TAG, errorMessage);
                                Toast.makeText(MapsActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }


                    }
                });
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.v(LOG_TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the OnAddSizzleButtonsClickListener item.
     * @param listener         The listener associated with the Snackbar OnAddSizzleButtonsClickListener.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    @Override
    public boolean onMyLocationButtonClick() {

        // We receive location updates if permission has been granted
        if (checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //Для анимации
    ///////////////////////////////////////////////////////////////////////////////////////
//    private void expandMapAnimation(){
//        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
//        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
//                "weight",
//                50,
//                100);
//        mapAnimation.setDuration(800);
//
//        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mDriverListRecyclerView);
//        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
//                "weight",
//                50,
//                0);
//        recyclerAnimation.setDuration(800);
//
//        recyclerAnimation.start();
//        mapAnimation.start();
//    }
//
//    private void contractMapAnimation(){
//        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
//        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
//                "weight",
//                100,
//                50);
//        mapAnimation.setDuration(800);
//
//        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mDriverListRecyclerView);
//        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
//                "weight",
//                0,
//                50);
//        recyclerAnimation.setDuration(800);
//
//        recyclerAnimation.start();
//        mapAnimation.start();
//    }
//
//    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
//    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
//    private int mMapLayoutState = 0;
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_full_screen_map:{
//
//                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
//                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
//                    expandMapAnimation();
//                }
//                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
//                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
//                    contractMapAnimation();
//                }
//                break;
//            }
//
//        }
//    }
    ////////////////////////////////////////////////////////////////////////////////////////
    //Для анимации
    ///////////////////////////////////////////////////////////////////////////////////////


   float RouteWidth;
   int RouteColor;
   AbstractRouting.TravelMode mode = AbstractRouting.TravelMode.WALKING;;
   private void getRouteToMarker(LatLng start, LatLng end, String type)
   {
       if (type.equals("route"))
       {
           RouteWidth = 14f;
           RouteColor = R.color.quantum_googblue;
           mode = AbstractRouting.TravelMode.DRIVING;
       }
       else if (type.equals("walk"))
       {
           RouteWidth = 12f;
           RouteColor = R.color.colorPolyLineGray;
           mode = AbstractRouting.TravelMode.WALKING;
       }

       Routing routing = new Routing.Builder()
               .travelMode(mode)
               .withListener(this)
               .alternativeRoutes(false)
               .waypoints(start, end)
               .key("AIzaSyCyBt7ZLiMLLyM6QxecdREWSUVnSur_STw")
               .build();
       routing.execute();
   }


    @Override
    public void onRoutingFailure(RouteException e)
    {
        // The Routing request failed
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Что-то пошло не так. Попробуйте снова", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart()
    {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        //       if(polylines.size()>0) {
        //           for (Polyline poly : polylines) {
        //               poly.remove();
        //         }
        //      }

        polylines = new ArrayList<>();

        //add route(s) to the map.

        for (int i = 0; i < route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.colorPolyLineGray));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            //Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled()
    {

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onClick(View v) {
                String[] BusStop1;
                String[] BusStop2;
                if (reserve) {

                    BusStop1 = busStops[pickUpI][pickUpJ].split(",");
                    BusStop2 = busStops[endUpI][endUpJ].split(",");

                    final float[] walkToStop = new float[10];
                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                            Double.parseDouble(BusStop1[1]), Double.parseDouble(BusStop1[2]),
                            walkToStop);

                    if (walkToStop[0] > 50) {
                        Toast.makeText(getApplicationContext(), "Вы слишком далеко от остановки", Toast.LENGTH_LONG).show();
                    } else {
                        getBus = true;
                        //Toast.makeText(getApplicationContext(), "Успех", Toast.LENGTH_LONG).show();
                        FirebaseDatabase.getInstance().getReference("request").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("way")
                                .setValue(BusStop1[0]);
                        FirebaseDatabase.getInstance().getReference("request").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("startLat")
                                .setValue(Double.parseDouble(BusStop1[1]));
                        FirebaseDatabase.getInstance().getReference("request").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("startLon")
                                .setValue(Double.parseDouble(BusStop1[2]));
                        FirebaseDatabase.getInstance().getReference("request").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("endLat")
                                .setValue(Double.parseDouble(BusStop2[1]));
                        FirebaseDatabase.getInstance().getReference("request").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("endLon")
                                .setValue(Double.parseDouble(BusStop2[2]));
                        FirebaseDatabase.getInstance().getReference("request").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("id")
                                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                }
                else {
        Toast.makeText(MapsActivity.this, "Задайте маршрут!", Toast.LENGTH_LONG).show();
               }
    }
}
