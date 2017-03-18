package cz.muni.fi.a2p06.stolencardatabase.adapters;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

/**
 * Created by robert on 18.3.2017.
 */

public class StepperAdapter extends AbstractFragmentStepAdapter {

    public StepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Step createStep(@IntRange(from = 0L) int position) {
        return null;
    }
}
