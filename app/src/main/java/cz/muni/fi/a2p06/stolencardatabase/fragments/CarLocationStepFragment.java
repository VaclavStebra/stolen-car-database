package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.content.Intent;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.location.places.ui.PlacePicker.getPlace;

// api key AIzaSyCaEdJBh3oi65FiwFhDvqmNixRjvs9T01I

/**
 * A simple {@link Fragment} subclass.
 */
public class CarLocationStepFragment extends Fragment implements Step, OnMapReadyCallback {

    @BindView(R.id.location_btn)
    Button mSetLocationBtn;
    @BindView(R.id.textView)
    TextView mLocation;
    @BindView(R.id.mapView)
    MapView mMapView;

    private GoogleMap mMap;
    private final int PLACE_PICKER_REQUEST = 1;

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

//        mMapView.setVisibility(View.GONE);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            Place place = getPlace(getContext(), data);
            mLocation.setText(String.format("Place: %s", place.getName()));
            LatLng carPosition = place.getLatLng();
            mMap.addMarker(new MarkerOptions().position(carPosition).title("Car position"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(carPosition));
            mMapView.onResume();
            mMapView.setVisibility(View.VISIBLE);
        }
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
}