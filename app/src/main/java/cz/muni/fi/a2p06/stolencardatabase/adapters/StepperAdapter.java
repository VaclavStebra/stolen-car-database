package cz.muni.fi.a2p06.stolencardatabase.adapters;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import cz.muni.fi.a2p06.stolencardatabase.fragments.BasicCarInfoStepFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarLocationStepFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarPhotoStepFragment;

/**
 * Created by robert on 18.3.2017.
 */

public class StepperAdapter extends AbstractFragmentStepAdapter {


    public StepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Step createStep(@IntRange(from = 0L) int position) {
        Step step = null;

        switch (position) {
            case 0:
                step = new BasicCarInfoStepFragment();
                break;
            case 1:
                step = new CarPhotoStepFragment();
                break;
            case 2:
                step = new CarLocationStepFragment();
                break;
            default:
                throw new IllegalArgumentException("Unsupported position " + position);
        }

        return step;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0L) int position) {
        StepViewModel.Builder builder = new StepViewModel.Builder(context);

        switch (position) {
            case 0:
                builder.setTitle("Basic Car Info");
                break;
            case 1:
                builder.setTitle("Car photo");
                break;
            case 2:
                builder.setTitle("Location");
                break;
            default:
                throw new IllegalArgumentException("Unsupported position " + position);
        }

        return builder.create();
    }
}
