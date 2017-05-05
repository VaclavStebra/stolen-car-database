package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.adapters.CarListAdapter;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.ocr.OcrActivity;
import cz.muni.fi.a2p06.stolencardatabase.utils.HelperMethods;

import static android.app.Activity.RESULT_OK;


public class CarListFragment extends Fragment implements CarListAdapter.CarItemHolder.OnCarItemClickListener {

    @BindView(R.id.car_list_view)
    RecyclerView mCarList;
    @BindView(R.id.car_list_empty)
    TextView mEmptyStateText;

    private static final String CAR_LIST_QUERY_KEY = "query";
    private static final String CAR_LIST_IS_SEARCH_SUBMITTED_KEY = "submitted_search";

    private CarListAdapter mCarListAdapter;
    private OnCarListFragmentInteractionListener mListener;
    private DatabaseReference mRef;

    private SearchView mSearchView;
    private String mQuery;
    private boolean mIsSearchSubmitted;

    public CarListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(CAR_LIST_QUERY_KEY);
            mIsSearchSubmitted = savedInstanceState.getBoolean(CAR_LIST_IS_SEARCH_SUBMITTED_KEY);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);
        ButterKnife.bind(this, view);

        prepareCarList();

        return view;
    }

    private void prepareCarList() {
        mRef = FirebaseDatabase.getInstance().getReference("cars");

        showEmptyState();
        mCarList.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        mCarList.setLayoutManager(manager);

        mCarListAdapter = new CarListAdapter(Car.class, R.layout.car_list_item,
                CarListAdapter.CarItemHolder.class, mRef, this);
        mCarList.setAdapter(mCarListAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mIsSearchSubmitted = false;

        if (resultCode == RESULT_OK && requestCode == OcrActivity.SCAN_REGNO_REQUEST && data != null) {
            mQuery = data.getStringExtra(OcrActivity.REGNO_QUERY);
            if (mSearchView != null) {
                mSearchView.setQuery(mQuery, mIsSearchSubmitted);
            }
        } else {
            mQuery = "";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSearchView.isShown()) {
            outState.putString(CAR_LIST_QUERY_KEY, mSearchView.getQuery().toString());
            outState.putBoolean(CAR_LIST_IS_SEARCH_SUBMITTED_KEY, mIsSearchSubmitted);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);

        setupSearchView((SearchView) searchViewItem.getActionView());

        if (mQuery != null && mSearchView != null) {
            searchViewItem.expandActionView();
            mSearchView.setQuery(mQuery, mIsSearchSubmitted);
        }
    }

    private void setupSearchView(SearchView searchView) {
        mSearchView = searchView;

        if (mSearchView != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;

            ((LinearLayout) mSearchView.getChildAt(0)).addView(createRegnoScannerButton(), params);

            mSearchView.setQueryHint(getString(R.string.searchview_query_hint));
            mSearchView.setMaxWidth(Integer.MAX_VALUE);
            mSearchView.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

            mSearchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (mCarListAdapter != null) mCarListAdapter.cleanup();

                    mCarListAdapter = new CarListAdapter(Car.class, R.layout.car_list_item,
                            CarListAdapter.CarItemHolder.class, mRef, CarListFragment.this);
                    mCarList.setAdapter(mCarListAdapter);

                    mQuery = null;
                    mIsSearchSubmitted = false;
                }
            });

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (mCarListAdapter != null) mCarListAdapter.cleanup();

                    Query dataQuery = null;

                    if (query.length() > 8) {
                        dataQuery = mRef.orderByChild("vin").equalTo(query);
                    } else {
                        dataQuery = mRef.orderByChild("regno").equalTo(HelperMethods.formatRegnoForDB(query));
                    }

                    mCarListAdapter = new CarListAdapter(Car.class, R.layout.car_list_item,
                            CarListAdapter.CarItemHolder.class, dataQuery, CarListFragment.this);
                    mCarList.setAdapter(mCarListAdapter);

                    mSearchView.clearFocus();
                    mIsSearchSubmitted = true;

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!newText.isEmpty()) {
                        mIsSearchSubmitted = false;
                    }
                    return true;
                }
            });
        }
    }

    private ImageButton createRegnoScannerButton() {
        ImageButton btRegnoScanner = new ImageButton(getActivity());
        btRegnoScanner.setImageResource(R.drawable.ic_photo_camera_black_24dp);
        btRegnoScanner.setBackgroundColor(Color.TRANSPARENT);
        int padding = HelperMethods.convertDptoPx(8, getContext());
        btRegnoScanner.setPadding(padding, padding, padding, padding);
        btRegnoScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OcrActivity.class);
                startActivityForResult(intent, OcrActivity.SCAN_REGNO_REQUEST);
            }
        });

        return btRegnoScanner;
    }

    @Override
    public void onCarListItemClick(View v, int position) {
        Car car = mCarListAdapter.getItem(position);
        if (mListener != null) {
            mListener.onItemClick(car);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCarListFragmentInteractionListener) {
            mListener = (OnCarListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCarListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mCarListAdapter != null) {
            mCarListAdapter.cleanup();
        }
    }

    @OnClick(R.id.car_list_fab)
    public void onFabClick(View view) {
        if (mListener != null) {
            mListener.onAddCarClick();
        }
    }

    public void onDataLoaded(Car car) {
        if (car == null) {
            showEmptyState();
        } else {
            showFilledState();
            if (mListener != null) {
                mListener.onDataLoaded(car);
            }
        }
    }

    private void showEmptyState() {
        mEmptyStateText.setVisibility(View.VISIBLE);
        mCarList.setVisibility(View.GONE);
    }

    private void showFilledState() {
        mEmptyStateText.setVisibility(View.GONE);
        mCarList.setVisibility(View.VISIBLE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCarListFragmentInteractionListener {
        void onItemClick(Car car);

        void onAddCarClick();

        void onDataLoaded(Car car);
    }
}
