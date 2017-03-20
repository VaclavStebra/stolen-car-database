package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import cz.muni.fi.a2p06.stolencardatabase.R;

/**
 * Created by robert on 20.3.2017.
 */

public class NumberPickerFragment extends DialogFragment {

    interface OnNumberSetListener {
        void onNumberSet(int number);
    }

    private OnNumberSetListener mListener;

    public void setOnNumberSetListener(OnNumberSetListener listener) {
        this.mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View layout = getActivity().getLayoutInflater().inflate(R.layout.fragment_number_picker, null);
        final NumberPicker picker = (NumberPicker) layout.findViewById(R.id.number_picker);
        picker.setMinValue(0); // TODO: Set min and max value
        picker.setMaxValue(2017);
        picker.setValue(2017);
        picker.setWrapSelectorWheel(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Production Year")
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onNumberSet(picker.getValue());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });

        return builder.create();
    }
}
