package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.adapters.StepperAdapter;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

public class AddCarFragment extends Fragment implements StepperLayout.StepperListener {

    private static final String ADD_CAR_CURRENT_STEP_POSITION_KEY = "position";
    private static final String ADD_CAR_MODE_KEY = "mode";
    private static final int ADD_CAR_MODE_CREATE = 1;
    private static final int ADD_CAR_MODE_UPDATE = 2;


    private Car mNewCar;
    private int mMode;

    @BindView(R.id.stepperLayout)
    StepperLayout mStepperLayout;

    public AddCarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car, container, false);
        ButterKnife.bind(this, view);

        int currentPosition = 0;

        if (savedInstanceState != null) {
            mNewCar = savedInstanceState.getParcelable(Car.class.getSimpleName());
            mMode = savedInstanceState.getInt(ADD_CAR_MODE_KEY);
            currentPosition = savedInstanceState.getInt(ADD_CAR_CURRENT_STEP_POSITION_KEY);
        } else if (getArguments() != null) {
            mMode = getArguments().getInt(ADD_CAR_MODE_KEY);
            if (mMode == ADD_CAR_MODE_UPDATE) {
                mNewCar = getArguments().getParcelable(Car.class.getSimpleName());
            } else {
                mNewCar = new Car();
            }
        }

        mStepperLayout.setAdapter(new StepperAdapter(getChildFragmentManager(), getContext(), mNewCar),
                currentPosition);
        mStepperLayout.setListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Car.class.getSimpleName(), mNewCar);
        outState.putInt(ADD_CAR_CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        outState.putInt(ADD_CAR_MODE_KEY, mMode);
        super.onSaveInstanceState(outState);
    }

    private void writeNewCar() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("cars").push();
        databaseReference.setValue(mNewCar);
    }

    private void writeNewCarWithPhoto() {
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("car_photos/" + mNewCar.getRegno());
            UploadTask uploadTask = storageReference.putStream(getActivity().getContentResolver().openInputStream(Uri.parse(mNewCar.getPhotoUrl())));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "The upload of the photo was not successful", Toast.LENGTH_SHORT).show();
                    mNewCar.setPhotoUrl(null);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") StorageMetadata metadata = taskSnapshot.getMetadata();
                    if (metadata != null) {
                        mNewCar.setPhotoUrl(metadata.getDownloadUrl().toString());
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    writeNewCar();
                }
            });
        } catch (FileNotFoundException ex) {
            Toast.makeText(getActivity(), "File not found: " + mNewCar.getPhotoUrl(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment for updating the provided car object
     * details.
     *
     * @param car provided car object.
     * @return A new instance of fragment AddCarFragment.
     */
    public static AddCarFragment newInstance(Car car) {
        Bundle args = new Bundle();
        args.putParcelable(Car.class.getSimpleName(), car);
        args.putInt(ADD_CAR_MODE_KEY, ADD_CAR_MODE_UPDATE);
        return AddCarFragment.newInstance(args);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment for adding a new car to a database.
     *
     * @return A new instance of fragment AddCarFragment.
     */
    public static AddCarFragment newInstance() {
        Bundle args = new Bundle();
        args.putInt(ADD_CAR_MODE_KEY, ADD_CAR_MODE_CREATE);
        return AddCarFragment.newInstance(args);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided arguments.
     *
     * @param args Bundle object with arguments.
     * @return A new instance of fragment AddCarFragment.
     */
    private static AddCarFragment newInstance(Bundle args) {
        AddCarFragment fragment = new AddCarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCompleted(View completeButton) {
        if (mMode == ADD_CAR_MODE_CREATE) {
            if (mNewCar.getPhotoUrl() != null) {
                writeNewCarWithPhoto();
            } else {
                writeNewCar();
            }
        } else if (mMode == ADD_CAR_MODE_UPDATE) {
            // update car details
        }

        getFragmentManager().popBackStack();
    }

    @Override
    public void onError(VerificationError verificationError) {
        //
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        //
    }

    @Override
    public void onReturn() {
    }
}
