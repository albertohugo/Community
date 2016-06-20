package hugo.alberto.community.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hugo.alberto.community.Activities.PostActivity;
import hugo.alberto.community.Application.Application;
import hugo.alberto.community.Model.CommunityPost;
import hugo.alberto.community.R;

public class Map_Fragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private Integer[]images={R.mipmap.ic_happy,R.mipmap.ic_angel,R.mipmap.ic_confused_2,R.mipmap.ic_blablabla,R.mipmap.ic_blank_face,R.mipmap.ic_blushing,R.mipmap.ic_closing_eyes,R.mipmap.ic_confused,R.mipmap.ic_angry,R.mipmap.ic_cool,R.mipmap.ic_crying,R.mipmap.ic_crying_laugh,R.mipmap.ic_dancing,R.mipmap.ic_devil,R.mipmap.ic_disgust,R.mipmap.ic_dissapointed,R.mipmap.ic_dreaming,R.mipmap.ic_epic_win,R.mipmap.ic_eurica,R.mipmap.ic_evil_laugh,R.mipmap.ic_eye_roll,R.mipmap.ic_eyes_closed,R.mipmap.ic_eyes_hearts,R.mipmap.ic_forgot,R.mipmap.ic_hand_smack,R.mipmap.ic_hello,R.mipmap.ic_hug,R.mipmap.ic_drop,R.mipmap.ic_joke,R.mipmap.ic_kiss,R.mipmap.ic_like,R.mipmap.ic_like_2,R.mipmap.ic_lol,R.mipmap.ic_love,R.mipmap.ic_mad,R.mipmap.ic_nerd,R.mipmap.ic_overworked,R.mipmap.ic_poop,R.mipmap.ic_pour,R.mipmap.ic_rock,R.mipmap.ic_scared,R.mipmap.ic_secret,R.mipmap.ic_sleeping,R.mipmap.ic_success,R.mipmap.ic_wink};
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final int FAST_CEILING_IN_SECONDS = 1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;
    private static final float METERS_PER_FEET = 0.3048f;
    private static final int METERS_PER_KILOMETER = 1000;
    private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;
    private static final int MAX_POST_SEARCH_RESULTS = 1111111111; //20
    private static final int MAX_POST_SEARCH_DISTANCE = 1111111111; //100
    private GoogleMap mapFragment;
    private Circle mapCircle;
    private float radius;
    private float lastRadius;
    private final Map<String, Marker> mapMarkers = new HashMap<>();
    private int mostRecentMapUpdate;
    private boolean hasSetUpInitialLocation;
    private String selectedPostObjectId;
    private Location lastLocation;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private GoogleApiClient locationClient;
    private ParseQueryAdapter<CommunityPost> postsQueryAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView;
        rootView = inflater.inflate(R.layout.map_layout, container, false);

        radius = Application.getSearchDistance();
        lastRadius = radius;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        locationClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        ParseQueryAdapter.QueryFactory<CommunityPost> factory =
                new ParseQueryAdapter.QueryFactory<CommunityPost>() {
                    public ParseQuery<CommunityPost> create() {
                        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
                        ParseQuery<CommunityPost> query = CommunityPost.getQuery();
                        query.include("user");
                        query.orderByDescending("createdAt");
                        query.whereWithinKilometers("location", geoPointFromLocation(myLoc), radius
                                * METERS_PER_FEET / METERS_PER_KILOMETER);
                        query.setLimit(MAX_POST_SEARCH_RESULTS);
                        return query;
                    }
                };

        postsQueryAdapter = new ParseQueryAdapter<CommunityPost>(getActivity(), factory) {

            @Override
            public View getItemView(CommunityPost post, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getActivity(), R.layout.anywall_post_item, null);
                }
                TextView contentView = (TextView) view.findViewById(R.id.content_view);
                TextView usernameView = (TextView) view.findViewById(R.id.username_view);
                contentView.setText(post.getText());
                usernameView.setText(post.getUser().getUsername());
                return view;
            }
        };

        postsQueryAdapter.setAutoload(false);
        postsQueryAdapter.setPaginationEnabled(false);

        mapFragment = ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        mapFragment.setMyLocationEnabled(true);
        mapFragment.setOnCameraChangeListener(new OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                doMapQuery();
            }
        });

        rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
                if (myLoc == null) {
                    Snackbar.make(rootView, "Please try again after your location appears on the map.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(Application.INTENT_EXTRA_LOCATION, myLoc);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Application.getConfigHelper().fetchConfigIfNeeded();
        radius = Application.getSearchDistance();
        if (lastLocation != null) {
            LatLng myLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            if (lastRadius != radius) {
                updateZoom(myLatLng);
            }
            updateCircle(myLatLng);
        }
        lastRadius = radius;
        doMapQuery();
        doListQuery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (Application.APPDEBUG) {
                            Log.d(Application.APPTAG, "Connected to Google Play services");
                        }
                        break;
                    default:
                        if (Application.APPDEBUG) {
                            Log.d(Application.APPTAG, "Could not connect to Google Play services");
                        }
                        break;
                }

            default:
                if (Application.APPDEBUG) {
                    Log.d(Application.APPTAG, "Unknown request code received for the activity");
                }
                break;
        }
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Application.APPDEBUG) {
                Log.d(Application.APPTAG, "Google play services available");
            }
            return true;
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), Application.APPTAG);
            }
            return false;
        }
    }

    public void onConnected(Bundle bundle) {
        if (Application.APPDEBUG) {
            Log.d("Connected to location services", Application.APPTAG);
        }
        currentLocation = getLocation();
        startPeriodicUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Application.APPTAG, "GoogleApiClient connection has been suspend");
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                if (Application.APPDEBUG) {
                    Log.d(Application.APPTAG, "An error occurred when connecting to location services.", e);
                }
            }
        } else {
            showErrorDialog(connectionResult.getErrorCode());
        }
    }

    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (lastLocation != null
                && geoPointFromLocation(location)
                .distanceInKilometersTo(geoPointFromLocation(lastLocation)) < 0.01) {
            return;
        }
        lastLocation = location;
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (!hasSetUpInitialLocation) {
            updateZoom(myLatLng);
            hasSetUpInitialLocation = true;
        }
        updateCircle(myLatLng);
        doMapQuery();
        doListQuery();
    }

    private void startPeriodicUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
    }

    private void stopPeriodicUpdates() {
        locationClient.disconnect();
    }

    private Location getLocation() {
        if (servicesConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }

    private void doListQuery() {
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        if (myLoc != null) {
            postsQueryAdapter.loadObjects();
        }
    }

    private void doMapQuery() {
        final int myUpdateNumber = ++mostRecentMapUpdate;
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        if (myLoc == null) {
            cleanUpMarkers(new HashSet<String>());
            return;
        }
        final ParseGeoPoint myPoint = geoPointFromLocation(myLoc);

        ParseQuery<CommunityPost> mapQuery = CommunityPost.getQuery();

        mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);

        mapQuery.findInBackground(new FindCallback<CommunityPost>() {
            @Override
            public void done(List<CommunityPost> objects, ParseException e) {
                if (e != null) {
                    if (Application.APPDEBUG) {
                        Log.d(Application.APPTAG, "An error occurred while querying for map posts.", e);
                    }
                    return;
                }
                if (myUpdateNumber != mostRecentMapUpdate) {
                    return;
                }
                Set<String> toKeep = new HashSet<>();

                for (CommunityPost post : objects) {
                    toKeep.add(post.getObjectId());
                    Marker oldMarker = mapMarkers.get(post.getObjectId());
                    MarkerOptions markerOpts =
                            new MarkerOptions().position(new LatLng(post.getLocation().getLatitude(), post
                                    .getLocation().getLongitude()));

                    if (oldMarker != null) {
                        if (oldMarker.getSnippet() != null) {
                            continue;
                        } else {
                            oldMarker.remove();
                        }
                    }
                    markerOpts =
                            markerOpts.title(post.getText()).snippet(String.valueOf(post.getCreatedAt()))
                                    .icon(BitmapDescriptorFactory.fromResource(images[Integer.parseInt(post.getFeeling())]));

                        Marker marker = mapFragment.addMarker(markerOpts);
                        mapMarkers.put(post.getObjectId(), marker);
                        if (post.getObjectId().equals(selectedPostObjectId)) {
                            marker.showInfoWindow();
                            selectedPostObjectId = null;
                        }
                }
                cleanUpMarkers(toKeep);
            }
        });
    }

    private void cleanUpMarkers(Set<String> markersToKeep) {
        for (String objId : new HashSet<>(mapMarkers.keySet())) {
            if (!markersToKeep.contains(objId)) {
                Marker marker = mapMarkers.get(objId);
                marker.remove();
                mapMarkers.get(objId).remove();
                mapMarkers.remove(objId);
            }
        }
    }

    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    private void updateCircle(LatLng myLatLng) {
        if (mapCircle == null) {
            mapCircle =
                    mapFragment.addCircle(
                            new CircleOptions().center(myLatLng).radius(radius * METERS_PER_FEET));
            int baseColor = Color.DKGRAY;
            mapCircle.setStrokeColor(baseColor);
            mapCircle.setStrokeWidth(2);
            mapCircle.setFillColor(Color.argb(50, Color.red(baseColor), Color.green(baseColor),
                    Color.blue(baseColor)));
        }
        mapCircle.setCenter(myLatLng);
        mapCircle.setRadius(radius * METERS_PER_FEET); // Convert radius in feet to meters.
    }

    private void updateZoom(LatLng myLatLng) {
        LatLngBounds bounds;
        bounds = calculateBoundsWithCenter(myLatLng);
        mapFragment.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
    }

    private double calculateLatLngOffset(LatLng myLatLng, boolean bLatOffset) {
        double latLngOffset = OFFSET_CALCULATION_INIT_DIFF;
        float desiredOffsetInMeters = radius * METERS_PER_FEET;
        float[] distance = new float[1];
        boolean foundMax = false;
        double foundMinDiff = 0;
        do {
            if (bLatOffset) {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude
                        + latLngOffset, myLatLng.longitude, distance);
            } else {
                Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, myLatLng.latitude,
                        myLatLng.longitude + latLngOffset, distance);
            }
            float distanceDiff = distance[0] - desiredOffsetInMeters;
            if (distanceDiff < 0) {
                if (!foundMax) {
                    foundMinDiff = latLngOffset;
                    latLngOffset *= 2;
                } else {
                    double tmp = latLngOffset;
                    latLngOffset += (latLngOffset - foundMinDiff) / 2;
                    foundMinDiff = tmp;
                }
            } else {
                latLngOffset -= (latLngOffset - foundMinDiff) / 2;
                foundMax = true;
            }
        } while (Math.abs(distance[0] - desiredOffsetInMeters) > OFFSET_CALCULATION_ACCURACY);
        return latLngOffset;
    }

    private LatLngBounds calculateBoundsWithCenter(LatLng myLatLng) {

        LatLngBounds.Builder builder = LatLngBounds.builder();
        double lngDifference = calculateLatLngOffset(myLatLng, false);
        LatLng east = new LatLng(myLatLng.latitude, myLatLng.longitude + lngDifference);
        builder.include(east);
        LatLng west = new LatLng(myLatLng.latitude, myLatLng.longitude - lngDifference);
        builder.include(west);

        double latDifference = calculateLatLngOffset(myLatLng, true);
        LatLng north = new LatLng(myLatLng.latitude + latDifference, myLatLng.longitude);
        builder.include(north);
        LatLng south = new LatLng(myLatLng.latitude - latDifference, myLatLng.longitude);
        builder.include(south);

        return builder.build();
    }

    private void showErrorDialog(int errorCode) {
        Dialog errorDialog =
                GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
        if (errorDialog != null) {
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(getFragmentManager(), Application.APPTAG);
        }
    }

    private static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
