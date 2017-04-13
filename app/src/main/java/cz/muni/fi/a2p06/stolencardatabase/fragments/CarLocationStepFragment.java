package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.entity.Coordinates;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarLocationStepFragment extends Fragment implements BlockingStep, OnMapReadyCallback {
    // TODO: Check if any permissions are needed - try to delete location permissions

    // TODO REFACTOR

    private GoogleMap mMap;
    private LatLng mLocation;

    private Car mCar;

    private static final int PLACE_PICKER_REQUEST = 1;

    @BindView(R.id.location_btn)
    Button mSetLocationBtn;
    @BindView(R.id.place_text)
    TextView mPlaceText;
    @BindView(R.id.mapView)
    MapView mMapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCar = getArguments().getParcelable(Car.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_location_step, container, false);
        ButterKnife.bind(this, view);
        mMapView.onCreate(savedInstanceState);

        mSetLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        loadData();

        mMapView.setVisibility(View.GONE);
        mMapView.getMapAsync(this);
        mMapView.setClickable(false);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            mLocation = getPlace(getContext(), data).getLatLng();
            showMap();
        }
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
    public void onSaveInstanceState(Bundle outState) {
        saveData();
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
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

    public static CarLocationStepFragment newInstance(Car car) {
        CarLocationStepFragment fragment = new CarLocationStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(Car.class.getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showMap();
    }

    private void showMap() {
        if (mMap != null && mLocation != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(mLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));

            mPlaceText.setText(String.format("Location: %s", formatLatLng(mLocation)));
            mPlaceText.setTypeface(Typeface.DEFAULT);

            mMapView.setVisibility(View.VISIBLE);
        }
    }

    private String formatLatLng(LatLng location) {
        if (location == null) {
            return "";
        }

        String[] lat = Location.convert(mLocation.latitude, Location.FORMAT_SECONDS).replace(',', '.').split(":");
        String latitude = String.format(Locale.getDefault(), "%s°%s'%.1f\"%c", lat[0], lat[1], Float.valueOf(lat[2]),
                (location.latitude > 0 ? 'N' : 'S'));

        String[] lon = Location.convert(mLocation.longitude, Location.FORMAT_SECONDS).replace(',', '.').split(":");
        String longitude = String.format(Locale.getDefault(), "%s°%s'%.1f\"%c", lon[0], lon[1], Float.valueOf(lon[2]),
                (location.longitude > 0 ? 'E' : 'W'));

        return latitude + " " + longitude;
    }

    private void saveData() {
        if (mCar != null && mLocation != null) {
            mCar.setLocation(new Coordinates(mLocation.latitude, mLocation.longitude));
        }
    }

    private void loadData() {
        if (mCar != null) {
            Coordinates cord = mCar.getLocation();
            if (cord != null) {
                mLocation = new LatLng(cord.getLat(), cord.getLon());
            }
        }
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
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        saveData();
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        saveData();
        callback.goToPrevStep();
    }
}
