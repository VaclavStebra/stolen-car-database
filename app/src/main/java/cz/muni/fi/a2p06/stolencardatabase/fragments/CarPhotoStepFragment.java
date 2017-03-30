package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarPhotoStepFragment extends Fragment implements BlockingStep {
    // TODO: Check if any permissions are needed

    private static final int PICK_IMAGE_REQUEST = 1;

    private Car mCar;
    private Uri mPhotoUri;

    @BindView(R.id.add_photo_btn)
    Button mAddPhotoBtn;
    @BindView(R.id.car_photo_view)
    ImageView mCarPhoto;
    @BindView(R.id.photo_text)
    TextView mPhotoText;

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
        View view = inflater.inflate(R.layout.fragment_car_photo_step, container, false);
        ButterKnife.bind(this, view);

        mCarPhoto.setVisibility(View.GONE);
        loadData();

        mAddPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(photoIntent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            mPhotoUri = data.getData();
            showPhoto();
        }
    }

    private void showPhoto() {
        if (mPhotoUri != null) {
            mPhotoText.setVisibility(View.GONE);
            mCarPhoto.setVisibility(View.VISIBLE);
            mCarPhoto.setImageURI(mPhotoUri);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveData();
        super.onSaveInstanceState(outState);
    }

    public static CarPhotoStepFragment newInstance(Car car) {
        CarPhotoStepFragment fragment = new CarPhotoStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(Car.class.getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

    private void saveData() {
        if (mCar != null && mPhotoUri != null) {
            // Temporarily save the photo uri into the photoUrl parameter of the Car object
            mCar.setPhotoUrl(mPhotoUri.toString());
        }
    }

    private void loadData() {
        if (mCar != null) {
            String uri = mCar.getPhotoUrl();
            if (uri != null) {
                mPhotoUri = Uri.parse(uri);
                showPhoto();
            }
        }
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        //
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        //
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
