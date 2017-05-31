package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Calendar;

import cz.muni.fi.a2p06.stolencardatabase.R;

/**
 * Created by robert on 20.3.2017.
 */

public class YearPickerFragment extends DialogFragment {

    interface OnYearSetListener {
        void onYearSet(int number);

        void onYearDeleted();
    }

    private OnYearSetListener mListener;
    private Integer mCurrentValue;

    public void setOnYearSetListener(OnYearSetListener listener) {
        this.mListener = listener;
    }

    public void setCurrentValue(Integer value) {
        this.mCurrentValue = value;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_year_picker, null);
        final NumberPicker picker = (NumberPicker) layout.findViewById(R.id.number_picker);
        picker.setMinValue(1960);
        picker.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));
        picker.setValue(mCurrentValue == null ? picker.getMaxValue() : mCurrentValue);
        picker.setWrapSelectorWheel(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.hint_production_year)
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onYearSet(picker.getValue());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });

        if (mCurrentValue != null) {
            builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mListener != null) {
                        mListener.onYearDeleted();
                    }
                }
            });
        }

        this.setRetainInstance(true);

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
