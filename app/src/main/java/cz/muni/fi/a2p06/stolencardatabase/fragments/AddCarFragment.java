package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private static final String TAG = "AddCarFragment";

    private static final String ADD_CAR_CURRENT_STEP_POSITION_KEY = "position";
    private static final String ADD_CAR_MODE_KEY = "mode";
    private static final String ADD_CAR_CAR_KEY = "car";
    private static final int ADD_CAR_MODE_CREATE = 1;
    private static final int ADD_CAR_MODE_UPDATE = 2;

    private Car mCar;
    private int mMode;
    private DatabaseReference mDatabaseReference;

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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("cars");

        int currentPosition = 0;

        if (savedInstanceState != null) {
            mCar = savedInstanceState.getParcelable(Car.class.getSimpleName());
            mMode = savedInstanceState.getInt(ADD_CAR_MODE_KEY);
            currentPosition = savedInstanceState.getInt(ADD_CAR_CURRENT_STEP_POSITION_KEY);
        } else if (getArguments() != null) {
            mMode = getArguments().getInt(ADD_CAR_MODE_KEY);
            if (mMode == ADD_CAR_MODE_UPDATE) {
                mCar = getArguments().getParcelable(Car.class.getSimpleName());
                if (mCar != null) {
                    obtainCarKey(mCar.getRegno());
                } else {
                    throw new IllegalArgumentException("Car was not provided");
                }
            } else {
                mCar = new Car();
            }
        }

        mStepperLayout.setAdapter(new StepperAdapter(getChildFragmentManager(), getContext(), mCar),
                currentPosition);
        mStepperLayout.setListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Car.class.getSimpleName(), mCar);
        outState.putInt(ADD_CAR_CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        outState.putInt(ADD_CAR_MODE_KEY, mMode);
        super.onSaveInstanceState(outState);
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

    private void obtainCarKey(String regno) {
        Log.d(TAG, "obtainCarKey: called");
        mDatabaseReference.orderByChild("regno").equalTo(regno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        getArguments().putString(ADD_CAR_CAR_KEY, ds.getRef().getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void writeNewCar() {
        mDatabaseReference.push().setValue(mCar);
    }

    private void writeNewCarWithPhoto() {
        try {
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReference().child("car_photos/" + mCar.getRegno() + "-" + System.currentTimeMillis());
            UploadTask uploadTask = storageReference.putStream(getContext().getContentResolver().openInputStream(Uri.parse(mCar.getPhotoUrl())));
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "The upload of the photo was not successful", Toast.LENGTH_SHORT).show();
                    mCar.setPhotoUrl(null);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") StorageMetadata metadata = taskSnapshot.getMetadata();
                    if (metadata != null) {
                        mCar.setPhotoUrl(metadata.getDownloadUrl().toString());
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    writeNewCar();
                }
            });
        } catch (FileNotFoundException ex) {
            Toast.makeText(getActivity(), "Car photo not found: " + mCar.getPhotoUrl(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCar() {
        String carKey = getArguments().getString(ADD_CAR_CAR_KEY);
        Log.d(TAG, "updateCar: called with carKey: " + carKey);
        if (carKey != null) {
            mDatabaseReference.child(carKey).addListenerForSingleValueEvent(
                    new UpdateValueEventListener(getContext(), mDatabaseReference.child(carKey)));
        } else {
            Toast.makeText(getActivity(),
                    "Update is not possible. Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCompleted(View completeButton) {
        if (mMode == ADD_CAR_MODE_CREATE) {
            if (mCar.getPhotoUrl() != null) {
                writeNewCarWithPhoto();
            } else {
                writeNewCar();
            }
        } else if (mMode == ADD_CAR_MODE_UPDATE) {
            updateCar();
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

    private class UpdateValueEventListener implements ValueEventListener {

        private DatabaseReference mReference;
        private Context mContext;

        UpdateValueEventListener(@NonNull Context context, @NonNull DatabaseReference reference) {
            mContext = context;
            mReference = reference;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Car oldCar = dataSnapshot.getValue(Car.class);
            if (oldCar != null) {
                updateCarDetails(oldCar);
            }
        }

        private void updateCarDetails(Car oldCar) {
            if (!oldCar.getManufacturer().equals(mCar.getManufacturer())) {
                mReference.child("manufacturer").setValue(mCar.getManufacturer());
            }
            if (!oldCar.getModel().equals(mCar.getModel())) {
                mReference.child("model").setValue(mCar.getModel());
            }
            if (!oldCar.getRegno().equals(mCar.getRegno())) {
                mReference.child("regno").setValue(mCar.getRegno());
            }
            if (!oldCar.getVin().equals(mCar.getVin())) {
                mReference.child("vin").setValue(mCar.getVin());
            }
            if (!oldCar.getModel().equals(mCar.getModel())) {
                mReference.child("model").setValue(mCar.getModel());
            }
            if (!oldCar.getColor().equals(mCar.getColor())) {
                mReference.child("color").setValue(mCar.getColor());
            }
            if (oldCar.getStolenDate() != mCar.getStolenDate()) {
                mReference.child("stolen_date").setValue(mCar.getStolenDate());
            }
            if (mCar.getLocation() == null && oldCar.getLocation() != null) {
                mReference.child("location").removeValue();
            }
            if (mCar.getLocation() != null && !mCar.getLocation().equals(oldCar.getLocation())) {
                mReference.child("location").setValue(mCar.getLocation());
            }
            if (mCar.getEngine() == null && oldCar.getEngine() != null) {
                mReference.child("engine").removeValue();
            }
            if (mCar.getEngine() != null && !mCar.getEngine().equals(oldCar.getEngine())) {
                mReference.child("engine").setValue(mCar.getEngine());
            }
            if (mCar.getProductionYear() == null && oldCar.getProductionYear() != null) {
                mReference.child("production_year").removeValue();
            }
            if (mCar.getProductionYear() != null && !mCar.getProductionYear().equals(oldCar.getProductionYear())) {
                mReference.child("production_year").setValue(mCar.getProductionYear());
            }
            if (mCar.getPhotoUrl() == null && oldCar.getPhotoUrl() != null) {
                removePhotoFromDb(oldCar.getPhotoUrl());
                mReference.child("photo_url").removeValue();
            }
            if (mCar.getPhotoUrl() != null && !mCar.getPhotoUrl().equals(oldCar.getPhotoUrl())) {
                removePhotoFromDb(oldCar.getPhotoUrl());
                setNewPhoto();
            }
        }

        private void removePhotoFromDb(String photoUrl) {
            if (photoUrl != null && !photoUrl.isEmpty()) {
                StorageReference storageReference =
                        FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
                storageReference.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "An exception was thrown during removing the old photo of the car: ", e);
                    }
                });
            }

        }

        private void setNewPhoto() {
            if (mCar.getPhotoUrl() != null) {
                try {
                    StorageReference storageReference =
                            FirebaseStorage.getInstance().getReference().child("car_photos/" + mCar.getRegno() + "-" + System.currentTimeMillis());
                    UploadTask uploadTask = storageReference.putStream(mContext.getContentResolver().openInputStream(Uri.parse(mCar.getPhotoUrl())));
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "The upload of the photo was not successful", Toast.LENGTH_SHORT).show();
                            mCar.setPhotoUrl(null);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") StorageMetadata metadata = taskSnapshot.getMetadata();
                            if (metadata != null) {
                                mCar.setPhotoUrl(metadata.getDownloadUrl().toString());
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            mReference.child("photo_url").setValue(mCar.getPhotoUrl());
                        }
                    });
                } catch (FileNotFoundException ex) {
                    Toast.makeText(getActivity(), "Car photo not found: " + mCar.getPhotoUrl(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
