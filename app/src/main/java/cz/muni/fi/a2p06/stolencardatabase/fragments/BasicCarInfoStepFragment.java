package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicCarInfoStepFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, Step, NumberPickerFragment.OnNumberSetListener {

    private Calendar mCalendar;

    @BindView(R.id.input_date)
    EditText mDate;
    @BindView(R.id.input_production_year)
    EditText mYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_car_info, container, false);
        ButterKnife.bind(this, view);

        mCalendar = Calendar.getInstance();

        mDate.setInputType(InputType.TYPE_NULL);
        mDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showDatePicker();
                }
                return false;
            }
        });

        mYear.setInputType(InputType.TYPE_NULL);
        mYear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showNumberPicker();
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        mDate.setText(dateFormat.format(mCalendar.getTime()));
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setListener(this);
        datePickerFragment.setCalendar(mCalendar);
        datePickerFragment.show(getActivity().getFragmentManager(), "DatePicker");
    }

    @Override
    public void onNumberSet(int number) {
        mYear.setText(Integer.toString(number));
    }

    private void showNumberPicker() {
        NumberPickerFragment numberPickerFragment = new NumberPickerFragment();
        numberPickerFragment.setOnNumberSetListener(this);
        numberPickerFragment.show(getActivity().getFragmentManager(), "NumberPicker");
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
