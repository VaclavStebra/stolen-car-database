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

    private static final String CURRENT_STEP_POSITION_KEY = "position";

    private Car mNewCar;

    @BindView(R.id.stepperLayout)
    StepperLayout mStepperLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car, container, false);
        ButterKnife.bind(this, view);

        int currentPosition = 0;

        if (savedInstanceState != null) {
            mNewCar = savedInstanceState.getParcelable(Car.class.getSimpleName());
            currentPosition = savedInstanceState.getInt(CURRENT_STEP_POSITION_KEY);
        } else {
            mNewCar = new Car();
        }

        mStepperLayout.setAdapter(new StepperAdapter(getFragmentManager(), getContext(), mNewCar),
                currentPosition);
        mStepperLayout.setListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Car.class.getSimpleName(), mNewCar);
        outState.putInt(CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
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


    @Override
    public void onCompleted(View completeButton) {
        if (mNewCar.getPhotoUrl() != null) {
            writeNewCarWithPhoto();
        } else {
            writeNewCar();
        }
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
        //
    }
}
