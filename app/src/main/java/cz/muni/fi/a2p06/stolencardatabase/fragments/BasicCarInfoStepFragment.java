package cz.muni.fi.a2p06.stolencardatabase.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.utils.HelperMethods;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BasicCarInfoStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class BasicCarInfoStepFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener, BlockingStep, YearPickerFragment.OnYearSetListener {

    @BindView(R.id.input_manufacturer)
    AutoCompleteTextView mManufacturer;
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
    @BindView(R.id.input_district)
    AutoCompleteTextView mDistrict;
    @BindView(R.id.layout_input_district)
    TextInputLayout mLayoutDistrict;
    @BindView(R.id.input_production_year)
    EditText mProductionYear;
    @BindView(R.id.input_engine)
    EditText mEngine;
    private Calendar mCalendar;
    private Car mCar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCar = getArguments().getParcelable(Car.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_car_info, container, false);
        ButterKnife.bind(this, view);

        mCalendar = Calendar.getInstance();
        prepareUi();

        return view;
    }

    private void prepareUi() {
        mStolenDate.setInputType(InputType.TYPE_NULL);
        mStolenDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard(v);
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
                    hideKeyboard(v);
                    showYearPicker();
                }
                return false;
            }
        });

        mManufacturer.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.car_manufacturers)));
        mManufacturer.setThreshold(1);

        mDistrict.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.districts)));

        setTextWatchers();
    }

    private void setTextWatchers() {
        mManufacturer.addTextChangedListener(new DefaultTextWatcher(mLayoutManufacturer));
        mModel.addTextChangedListener(new DefaultTextWatcher(mLayoutModel));
        mRegno.addTextChangedListener(new DefaultTextWatcher(mLayoutRegno) {
            private boolean delete;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                delete = count > after;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    if (delete) {
                        s.delete(3, 4);
                    } else {
                        s.insert(3, " ");
                    }
                } else if (s.length() == 10) {
                    s.delete(9, 10);
                }
            }
        });
        mVin.addTextChangedListener(new DefaultTextWatcher(mLayoutVin) {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 18) {
                    s.delete(17, 18);
                }
            }
        });
        mStolenDate.addTextChangedListener(new DefaultTextWatcher(mLayoutStolenDate));
        mColor.addTextChangedListener(new DefaultTextWatcher(mLayoutColor));
        mDistrict.addTextChangedListener(new DefaultTextWatcher(mLayoutDistrict));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        mStolenDate.setText(SimpleDateFormat.getDateInstance().format(mCalendar.getTime()));
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
        boolean isValid = true;

        if (mManufacturer.getText().length() == 0) {
            mLayoutManufacturer.setError(getString(R.string.manufacturer_error_message));
            isValid = false;
        }

        if (mModel.getText().length() == 0) {
            mLayoutModel.setError(getString(R.string.model_error_message));
            isValid = false;
        }

        String temp = mRegno.getText().toString().trim().toUpperCase();

        if (!HelperMethods.isValidRegno(temp)) {
            mLayoutRegno.setError(getString(R.string.regno_error_message));
            isValid = false;
        }

        temp = mVin.getText().toString().trim().toUpperCase();

        if (temp.isEmpty()) {
            mLayoutVin.setError(getString(R.string.vin_error_empty_message));
            isValid = false;
        } else if (!HelperMethods.isValidVin(temp)) {
            mLayoutVin.setError(getString(R.string.vin_error_invalid_message));
            isValid = false;
        }

        if (mColor.getText().length() == 0) {
            mLayoutColor.setError(getString(R.string.color_error_message));
            isValid = false;
        }

        if (mStolenDate.getText().length() == 0) {
            mLayoutStolenDate.setError(getString(R.string.stolen_date_error_message));
            isValid = false;
        }

        if (mDistrict.getText().length() == 0) {
            mLayoutDistrict.setError(getString(R.string.district_error_message));
            isValid = false;
        }

        return isValid;
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param car shared car object.
     * @return A new instance of fragment BasicCarInfoStepFragment.
     */
    public static BasicCarInfoStepFragment newInstance(Car car) {
        BasicCarInfoStepFragment fragment = new BasicCarInfoStepFragment();
        Bundle args = new Bundle();
        args.putParcelable(Car.class.getSimpleName(), car);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public VerificationError verifyStep() {
        return isValidInput() ? null : new VerificationError("Invalid input");
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        saveData();
        callback.goToNextStep();
    }

    private void saveData() {
        if (mCar != null) {
            mCar.setManufacturer(mManufacturer.getText().toString());
            mCar.setModel(mModel.getText().toString());
            mCar.setRegno(HelperMethods.formatRegnoForDB(mRegno.getText().toString()));
            mCar.setVin(mVin.getText().toString().toUpperCase());
            mCar.setColor(mColor.getText().toString());
            mCar.setStolenDate(mCalendar.getTimeInMillis());
            mCar.setDistrict(mDistrict.getText().toString());
            if (mProductionYear.getText().length() != 0) {
                mCar.setProductionYear(Integer.valueOf(mProductionYear.getText().toString()));
            }
            if (mEngine.getText().length() != 0) {
                mCar.setEngine(mEngine.getText().toString());
            }
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        callback.complete();
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    private class DefaultTextWatcher implements TextWatcher {

        private TextInputLayout mInputLayout;

        DefaultTextWatcher(TextInputLayout inputLayout) {
            mInputLayout = inputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mInputLayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
