package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.muni.fi.a2p06.stolencardatabase.MainActivity;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.entity.Coordinates;
import cz.muni.fi.a2p06.stolencardatabase.utils.FirebaseRestClient;
import cz.muni.fi.a2p06.stolencardatabase.utils.HelperMethods;
import cz.muni.fi.a2p06.stolencardatabase.views.CustomMapView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarDetailFragment extends Fragment implements Step, OnMapReadyCallback {
    private static final String TAG = "CarDetailFragment";
    private static final String SHOW_BUTTONS = "SHOW_BUTTONS";
    private static final String SHOW_REPORT_LOCATION_BUTTON = "SHOW_REPORT_LOCATION_BUTTON";
    private static final int RC_HANDLE_LOCATION_PERM = 200;

    private Car mCar;
    private OnCarDetailFragmentInteractionListener mListener;
    private boolean mShowButtons;
    private boolean mShowReportLocationButton;

    @BindView(R.id.car_detail_photo)
    ImageView mPhoto;
    @BindView(R.id.car_detail_manufacturer_and_model)
    TextView mManufacturerAndModel;
    @BindView(R.id.car_detail_regno)
    TextView mRegno;
    @BindView(R.id.car_detail_stolen_date)
    TextView mStolenDate;
    @BindView(R.id.car_detail_color)
    TextView mColor;
    @BindView(R.id.car_detail_vin)
    TextView mVin;
    @BindView(R.id.car_detail_production_year)
    TextView mProductionYear;
    @BindView(R.id.car_detail_production_year_text)
    TextView mProductionYearText;
    @BindView(R.id.car_detail_engine)
    TextView mEngine;
    @BindView(R.id.car_detail_engine_text)
    TextView mEngineText;

    @BindView(R.id.car_map_view)
    CustomMapView mMapView;

    @BindView(R.id.report_location)
    Button mReportLocation;
    @BindView(R.id.delete_car)
    Button mDeleteButton;
    @BindView(R.id.edit_car)
    Button mEditButton;
    @BindView(R.id.car_detail_scroll_view)
    ScrollView mScrollView;


    public CarDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param car car to display.
     * @return A new instance of fragment CarDetailFragment.
     */
    public static CarDetailFragment newInstance(Car car, boolean showButtons, boolean showReportLocationButton) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_BUTTONS, showButtons);
        args.putBoolean(SHOW_REPORT_LOCATION_BUTTON, showReportLocationButton);
        args.putParcelable(Car.class.getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCar = getArguments().getParcelable(Car.class.getSimpleName());
            mShowButtons = getArguments().getBoolean(SHOW_BUTTONS);
            mShowReportLocationButton = getArguments().getBoolean(SHOW_REPORT_LOCATION_BUTTON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);
        ButterKnife.bind(this, view);

        mMapView.setViewParent(mScrollView);
        mMapView.onCreate(savedInstanceState);
        mMapView.setVisibility(View.GONE);

        if (mCar != null) {
            populateCarDetails();
            if (mCar.getLocation() != null || (mCar.getReportedLocation() != null && mCar.getReportedLocation().size() > 0)) {
                mMapView.getMapAsync(this);
            }
        }

        if (!mShowReportLocationButton) {
            mReportLocation.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Car car = savedInstanceState.getParcelable(Car.class.getSimpleName());
            updateCarView(car);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCarDetailFragmentInteractionListener) {
            mListener = (OnCarDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCarDetailFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @OnClick(R.id.delete_car)
    public void onDeleteCarClick(View view) {
        if (mListener != null) {
            mListener.onDeleteCar(mCar);
        }
    }

    @OnClick(R.id.edit_car)
    public void onEditCarClick(View view) {
        if (mListener != null) {
            mListener.onEditCar(mCar);
        }
    }

    public void updateCarView(Car car) {
        mCar = car;
        populateCarDetails();
        if (mCar.getLocation() != null || (mCar.getReportedLocation() != null && mCar.getReportedLocation().size() > 0)) {
            mMapView.getMapAsync(this);
        }
    }

    private void populateCarDetails() {
        populateCarImage();
        toggleButtonsVisibility();
        mManufacturerAndModel.setText(mCar.getManufacturer() + " " + mCar.getModel());
        mRegno.setText(HelperMethods.formatRegnoFromDB(mCar.getRegno()));
        mStolenDate.setText(HelperMethods.formatDate(mCar.getStolenDate()));
        mColor.setText(mCar.getColor());
        mVin.setText(mCar.getVin());
        if (mCar.getProductionYear() != null) {
            mProductionYear.setText(String.valueOf(mCar.getProductionYear()));
        } else {
            mProductionYear.setVisibility(View.GONE);
            mProductionYearText.setVisibility(View.GONE);
        }
        if (mCar.getEngine() != null) {
            mEngine.setText(mCar.getEngine());
        } else {
            mEngine.setVisibility(View.GONE);
            mEngineText.setVisibility(View.GONE);
        }
    }

    private void populateCarImage() {
        DrawableTypeRequest drawableTypeRequest;
        if (mCar.getPhotoUrl() != null) {
            Uri photoUri = Uri.parse(mCar.getPhotoUrl());

            if (photoUri.getScheme().equals("content")) {
                drawableTypeRequest = Glide.with(this).load(photoUri);
            } else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(mCar.getPhotoUrl());
                drawableTypeRequest = Glide.with(this).using(new FirebaseImageLoader())
                        .load(storageReference);
            }
        } else {
            drawableTypeRequest = Glide.with(this).load("");
        }
        drawableTypeRequest.asBitmap()
                .placeholder(R.drawable.car_placeholder)
                .centerCrop()
                .into(mPhoto);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(Car.class.getSimpleName(), mCar);
        super.onSaveInstanceState(savedInstanceState);
    }

    public Car getCar() {
        return mCar;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Coordinates carPos = mCar.getLocation();
        HashMap<String, Coordinates> reportedLocation = mCar.getReportedLocation();
        if (googleMap != null) {
            googleMap.clear();
            if (carPos != null) {
                LatLng pos = new LatLng(carPos.getLat(), carPos.getLon());
                googleMap.addMarker(new MarkerOptions().position(pos).title(mCar.getRegno()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            }
            if (reportedLocation != null && reportedLocation.size() > 0) {
                addReportedLocationMarkers(googleMap, reportedLocation);
                if (carPos == null) {
                    Coordinates position = mCar.getReportedLocation().values().iterator().next();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(position.getLat(), position.getLon())));
                }
            }
            mMapView.setVisibility(View.VISIBLE);
        }
    }

    private void addReportedLocationMarkers(GoogleMap map, HashMap<String, Coordinates> locations) {
        if (map == null) {
            return;
        }
        int count = 1;
        for (Coordinates coords : locations.values()) {
            LatLng pos = new LatLng(coords.getLat(), coords.getLon());
            map.addMarker(new MarkerOptions().position(pos).title("#" + count).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            count++;
        }
    }

    private void toggleButtonsVisibility() {
        if (!mShowButtons) {
            mDeleteButton.setVisibility(View.GONE);
            mEditButton.setVisibility(View.GONE);
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userUid = user.getUid();
            if (userUid.equals(mCar.getUserUid())) {
                mDeleteButton.setVisibility(View.VISIBLE);
                mEditButton.setVisibility(View.VISIBLE);
            } else {
                mDeleteButton.setVisibility(View.GONE);
                mEditButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_LOCATION_PERM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted - get location");
                reportLocation();
            } else {
                Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Location permission is needed")
                        .setMessage(R.string.no_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //getActivity().finish();
                                // Do not do anything
                            }
                        })
                        .setNegativeButton(R.string.grant_access, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestLocationPermission();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }
    }

    private void requestLocationPermission() {
        Log.w(TAG, "Location permission is not granted. Requesting permission");

        if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                || this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            this.requestPermissions(new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    RC_HANDLE_LOCATION_PERM);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", MainActivity.class.getPackage().getName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    @OnClick(R.id.report_location)
    public void onReportLocationClick() {
        int coarseLocationPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocationPerm = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermissions(coarseLocationPerm, fineLocationPerm)) {
            reportLocation();
        } else {
            this.requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_HANDLE_LOCATION_PERM
            );
        }
    }

    private boolean hasLocationPermissions(int coarseLocationPermResult, int fineLocationPermResult) {
        return coarseLocationPermResult == PackageManager.PERMISSION_GRANTED
                && fineLocationPermResult == PackageManager.PERMISSION_GRANTED;
    }

    private void reportLocation() throws SecurityException {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Log.d(TAG, String.valueOf(location.getLatitude()));
            Log.d(TAG, String.valueOf(location.getLongitude()));
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cars");
            Query query = ref.orderByChild("regno").equalTo(mCar.getRegno());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        ref.child(key).child("reported_location").push().setValue(new Coordinates(location.getLatitude(), location.getLongitude()));
                    }
                    Toast.makeText(getContext(), "Location reported", Toast.LENGTH_SHORT).show();
                    try {
                        FirebaseRestClient.sendMessageToTopic(getActivity(), mCar.getUserUid(), mCar.getRegno() + " was spotted");
                    } catch (UnsupportedEncodingException | JSONException e) {
                        Log.e(TAG, "onDataChange", e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        } else {
            Toast.makeText(getContext(), "Location could not be determined. Please enable location.", Toast.LENGTH_LONG).show();
        }
    }

    public interface OnCarDetailFragmentInteractionListener {
        void onDeleteCar(Car car);

        void onEditCar(Car car);
    }
}
