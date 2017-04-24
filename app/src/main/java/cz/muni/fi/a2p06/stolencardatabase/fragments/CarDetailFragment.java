package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarDetailFragment extends Fragment implements Step {
    private Car mCar;

    @BindView(R.id.car_detail_photo)
    ImageView mPhoto;
    @BindView(R.id.car_detail_manufacturer_and_model)
    TextView mManufacturerAndModel;
    @BindView(R.id.car_detail_spz)
    TextView mSPZ;
    @BindView(R.id.car_detail_stolen_date)
    TextView mStolenDate;
    @BindView(R.id.car_detail_color)
    TextView mColor;
    @BindView(R.id.car_detail_vin)
    TextView mVin;
    @BindView(R.id.car_detail_production_year)
    TextView mProductionYear;
    @BindView(R.id.car_detail_engine)
    TextView mEngine;


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
    public static CarDetailFragment newInstance(Car car) {
        CarDetailFragment fragment = new CarDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Car.class.getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_detail, container, false);
        ButterKnife.bind(this, view);
        if (mCar != null) {
            populateCarDetails();
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

    public void updateCarView(Car car) {
        mCar = car;
        populateCarDetails();
    }

    private void populateCarDetails() {
        populateCarImage();
        mManufacturerAndModel.setText(mCar.getManufacturer() + " " + mCar.getModel());
        mSPZ.setText(mCar.getRegno());
        mStolenDate.setText(SimpleDateFormat.getDateInstance().format(new Date(mCar.getStolenDate())));
        mColor.setText(mCar.getColor());
        mVin.setText(mCar.getVin());
        mProductionYear.setText(String.valueOf(mCar.getProductionYear()));
        mEngine.setText(mCar.getEngine());
    }

    private void populateCarImage() {
        if (mCar.getPhotoUrl() != null) {
            Uri photoUri = Uri.parse(mCar.getPhotoUrl());
            DrawableTypeRequest drawableTypeRequest = null;

            if (photoUri.getScheme().equals("content")) {
                drawableTypeRequest = Glide.with(this).load(photoUri);
            } else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(mCar.getPhotoUrl());
                drawableTypeRequest = Glide.with(this).using(new FirebaseImageLoader())
                        .load(storageReference);
            }

            drawableTypeRequest.asBitmap()
                    .placeholder(R.drawable.car_placeholder)
                    .centerCrop()
                    .into(mPhoto);
        }
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
}
