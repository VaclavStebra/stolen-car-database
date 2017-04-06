package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.adapters.CarListAdapter;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;


public class CarListFragment extends Fragment implements CarListAdapter.CarItemHolder.OnCarItemClickListener {
    private static final String TAG = "CarListFragment";

    private CarListAdapter mCarListAdapter;

    private OnCarListFragmentInteractionListener mListener;

    private DatabaseReference mRef;

    @BindView(R.id.car_list_view)
    RecyclerView mCarList;

    public CarListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);
        ButterKnife.bind(this, view);

        mRef = FirebaseDatabase.getInstance().getReference("cars");

        mCarList.setHasFixedSize(true);
        mCarList.setLayoutManager(new LinearLayoutManager(getContext()));

        mCarListAdapter = new CarListAdapter(Car.class, R.layout.car_list_item,
                CarListAdapter.CarItemHolder.class, mRef, this);
        mCarList.setAdapter(mCarListAdapter);

        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAddCarClick();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (mCarListAdapter != null) mCarListAdapter.cleanup();

                Query dataQuery = null;

                if (query.length() > 8) {
                    dataQuery = mRef.orderByChild("vin").equalTo(query);
                } else {
                    dataQuery = mRef.orderByChild("regno").equalTo(query);
                }

                mCarListAdapter = new CarListAdapter(Car.class, R.layout.car_list_item,
                        CarListAdapter.CarItemHolder.class, dataQuery, CarListFragment.this);
                mCarList.setAdapter(mCarListAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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

    public void onDataLoaded(Car car) {
        if (mListener != null) {
            mListener.onDataLoaded(car);
        }
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
