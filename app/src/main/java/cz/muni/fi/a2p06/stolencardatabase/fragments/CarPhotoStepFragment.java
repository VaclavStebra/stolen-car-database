package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
 * Use the {@link CarPhotoStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarPhotoStepFragment extends Fragment implements BlockingStep {
    private static final String TAG = "CarPhotoStepFragment";

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RC_HANDLE_STORAGE_PERM = 2;
    private static final String CAR_PHOTO_PERMISSION_REQUESTED = "permission";

    private Car mCar;
    private Uri mPhotoUri;
    private boolean mStoragePermissionAlreadyRequested;

    @BindView(R.id.car_photo_add_btn)
    Button mAddPhotoBtn;
    @BindView(R.id.car_photo_delete_btn)
    Button mDeletePhotoButton;
    @BindView(R.id.car_photo_view)
    ImageView mCarPhoto;
    @BindView(R.id.car_photo_text)
    TextView mPhotoText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mStoragePermissionAlreadyRequested = savedInstanceState.getBoolean(CAR_PHOTO_PERMISSION_REQUESTED);
        }
        if (getArguments() != null) {
            mCar = getArguments().getParcelable(Car.class.getSimpleName());
            loadData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_photo_step, container, false);
        ButterKnife.bind(this, view);

        prepareUi();

        return view;
    }

    private void prepareUi() {
        mCarPhoto.setVisibility(View.GONE);
        mDeletePhotoButton.setVisibility(View.GONE);
        showPhoto();

        mAddPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rc = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    photoIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(photoIntent, "Select Picture"), PICK_IMAGE_REQUEST);
                } else {
                    if (mStoragePermissionAlreadyRequested) {
                        requestPermission();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_HANDLE_STORAGE_PERM);
                    }
                }
            }
        });

        mDeletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCarPhoto.getDrawable() != null) {
                    mPhotoUri = null;
                    mCarPhoto.setImageResource(R.drawable.car_placeholder);
                    mCarPhoto.setVisibility(View.GONE);
                    mDeletePhotoButton.setVisibility(View.GONE);
                    mPhotoText.setVisibility(View.VISIBLE);
                }
            }
        });
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
            mDeletePhotoButton.setVisibility(View.VISIBLE);

            DrawableTypeRequest drawableTypeRequest;

            if (mPhotoUri.getScheme().equals("content")) {
                drawableTypeRequest = Glide.with(this).load(mPhotoUri);
            } else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(mCar.getPhotoUrl());
                drawableTypeRequest = Glide.with(this).using(new FirebaseImageLoader())
                        .load(storageReference);
            }

            drawableTypeRequest.asBitmap()
                    .placeholder(R.drawable.car_placeholder)
                    .fitCenter()
                    .into(mCarPhoto);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveData();
        outState.putBoolean(CAR_PHOTO_PERMISSION_REQUESTED, mStoragePermissionAlreadyRequested);
        super.onSaveInstanceState(outState);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param car shared car object.
     * @return A new instance of fragment CarPhotoStepFragment.
     */
    public static CarPhotoStepFragment newInstance(Car car) {
        CarPhotoStepFragment fragment = new CarPhotoStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(Car.class.getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

    private void saveData() {
        if (mCar != null) {
            // Temporarily save the photo uri into the photoUrl parameter of the Car object
            mCar.setPhotoUrl(mPhotoUri == null ? null : mPhotoUri.toString());
        }
    }

    private void loadData() {
        if (mCar != null) {
            String uri = mCar.getPhotoUrl();
            if (uri != null) {
                mPhotoUri = Uri.parse(uri);
            }
        }
    }

    private void requestPermission() {
        Log.w(TAG, "WRITE_EXTERNAL_STORAGE permission is not granted. Requesting permission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_HANDLE_STORAGE_PERM);
        } else {
            Snackbar.make(getView(), R.string.car_photo_permission_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.grant_access, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_STORAGE_PERM) {
            mStoragePermissionAlreadyRequested = true;
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mAddPhotoBtn.performClick();
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
