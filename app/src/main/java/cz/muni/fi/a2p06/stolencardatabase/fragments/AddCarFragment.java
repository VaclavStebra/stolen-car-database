package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.StepperLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.adapters.StepperAdapter;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

public class AddCarFragment extends Fragment {

    private static final String CURRENT_STEP_POSITION_KEY = "position";

    @BindView(R.id.stepperLayout)
    StepperLayout mStepperLayout;

    private Car mNewCar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_car, container, false);
        ButterKnife.bind(this, view);

        StepperAdapter stepperAdapter = new StepperAdapter(getFragmentManager(), getContext(), mNewCar);

        if (savedInstanceState != null) {
            mNewCar = savedInstanceState.getParcelable(Car.class.getSimpleName());
            mStepperLayout.setAdapter(stepperAdapter, savedInstanceState.getInt(CURRENT_STEP_POSITION_KEY));
        } else {
            mNewCar = new Car();
            mStepperLayout.setAdapter(stepperAdapter);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Car.class.getSimpleName(), mNewCar);
        outState.putInt(CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        super.onSaveInstanceState(outState);
    }
}
