package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

/**
 * A simple {@link Fragment} subclass.
 */
public class BasicCarInfoStepFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, BlockingStep, YearPickerFragment.OnYearSetListener {

    private Calendar mCalendar;

    // TODO textinputedittext
    @BindView(R.id.input_manufacturer)
    EditText mManufacturer;
    @BindView(R.id.layout_input_manufacturer)
    TextInputLayout mLayoutManufacturer;

    @BindView(R.id.input_model)
    EditText mModel;
    @BindView(R.id.layout_input_model)
    TextInputLayout mLayoutModel;

    @BindView(R.id.input_regno)
    EditText mRegno;
    @BindView(R.id.layout_input_regno)
    TextInputLayout mLayoutRegno;

    @BindView(R.id.input_vin)
    EditText mVin;
    @BindView(R.id.layout_input_vin)
    TextInputLayout mLayoutVin;

    @BindView(R.id.input_color)
    EditText mColor;
    @BindView(R.id.layout_input_color)
    TextInputLayout mLayoutColor;

    @BindView(R.id.input_date)
    EditText mStolenDate;
    @BindView(R.id.layout_input_date)
    TextInputLayout mLayoutStolenDate;

    @BindView(R.id.input_production_year)
    EditText mProductionYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_car_info, container, false);
        ButterKnife.bind(this, view);

        mCalendar = Calendar.getInstance();

        mStolenDate.setInputType(InputType.TYPE_NULL);
        mStolenDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showDatePicker();
                }
                return false;
            }
        });

        mProductionYear.setInputType(InputType.TYPE_NULL);
        mProductionYear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showYearPicker();
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
        mStolenDate.setText(dateFormat.format(mCalendar.getTime()));
    }

    private void showDatePicker() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setListener(this);
        datePickerFragment.setCalendar(mCalendar);
        datePickerFragment.show(getActivity().getFragmentManager(), "DatePicker");
    }

    @Override
    public void onYearSet(int number) {
        mProductionYear.setText(String.valueOf(number));
    }

    private void showYearPicker() {
        YearPickerFragment yearPickerFragment = new YearPickerFragment();
        yearPickerFragment.setOnYearSetListener(this);
        yearPickerFragment.show(getActivity().getFragmentManager(), "YearPicker");
    }

    private boolean isValidInput() {
        if (mManufacturer.getText().length() == 0) {
            mLayoutManufacturer.setError(getString(R.string.required_field));
            return false;
        }

        if (mModel.getText().length() == 0) {
            mLayoutModel.setError(getString(R.string.required_field));
            return false;
        }

        String temp = mRegno.getText().toString().replace(" ", "");

        if (temp.isEmpty()) {
            mLayoutRegno.setError(getString(R.string.required_field));
            return false;
        }

        if (temp.length() > 8 || temp.length() < 7) {
            mLayoutRegno.setError(getString(R.string.invalid_input));
            return false;
        }

        temp = mVin.getText().toString().trim();

        if (temp.isEmpty()) {
            mLayoutVin.setError(getString(R.string.required_field));
            return false;
        }

        if (!Pattern.matches("[a-zA-Z0-9]{17}", temp)) {
            mLayoutVin.setError(getString(R.string.invalid_input));
            return false;
        }

        if (mColor.getText().length() == 0) {
            mLayoutColor.setError(getString(R.string.required_field));
            return false;
        }

        if (mStolenDate.getText().length() == 0) {
            mLayoutStolenDate.setError(getString(R.string.required_field));
            return false;
        }

        return true;
    }

    public static BasicCarInfoStepFragment newInstance(Car car) {
        BasicCarInfoStepFragment fragment = new BasicCarInfoStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(car.getClass().getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public VerificationError verifyStep() {
//TODO uncomment this
//        return isValidInput() ? null : new VerificationError("Invalid input");
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }
}
