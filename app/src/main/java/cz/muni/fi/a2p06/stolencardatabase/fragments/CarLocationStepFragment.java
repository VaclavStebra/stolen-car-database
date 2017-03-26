package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.entity.Coordinates;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

// api key AIzaSyCaEdJBh3oi65FiwFhDvqmNixRjvs9T01I

/**
 * A simple {@link Fragment} subclass.
 */
public class CarLocationStepFragment extends Fragment implements BlockingStep, OnMapReadyCallback {
    // TODO: Check if any permissions are needed

    private static final String TAG = "CarLocationStepFragment";

    @BindView(R.id.location_btn)
    Button mSetLocationBtn;
    @BindView(R.id.place_text)
    TextView mPlaceTxt;
    @BindView(R.id.mapView)
    MapView mMapView;

    private GoogleMap mMap;
    private Marker mMarker;

    private Car mCar;

    private final int PLACE_PICKER_REQUEST = 1;

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

        mMapView.setVisibility(View.GONE);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mMapView.setClickable(false);

        return view;
    }

    public static CarLocationStepFragment newInstance(Car car) {
        CarLocationStepFragment fragment = new CarLocationStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(Car.class.getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            final Place place = getPlace(getContext(), data);

            mPlaceTxt.setText(String.format("Location: %s", place.getName()));
            mPlaceTxt.setTypeface(Typeface.DEFAULT);

            LatLng carPosition = place.getLatLng();
            if (mMarker == null) {
                mMarker = mMap.addMarker(new MarkerOptions().position(carPosition).title("Car position"));
            } else {
                mMarker.setPosition(carPosition);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(carPosition));

            mMapView.setVisibility(View.VISIBLE);
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
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
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
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        if (mCar != null && mMarker != null) {
            Coordinates coordinates = new Coordinates();
            coordinates.setLat(mMarker.getPosition().latitude);
            coordinates.setLon(mMarker.getPosition().longitude);
            mCar.setLocation(coordinates);
        }
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        Log.d(TAG, "onCompleteClicked: " + mCar);
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }
}
