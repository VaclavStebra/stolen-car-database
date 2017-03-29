package cz.muni.fi.a2p06.stolencardatabase.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.adapters.CarListAdapter;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;


public class CarListFragment extends Fragment implements CarListAdapter.CarItemHolder.OnCarItemClickListener {

    private CarListAdapter mCarListAdapter;

    private OnCarListFragmentInteractionListener mListener;

    public CarListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("cars");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.car_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCarListAdapter = new CarListAdapter(Car.class, R.layout.car_list_item,
                CarListAdapter.CarItemHolder.class, mRef, this);
        recyclerView.setAdapter(mCarListAdapter);

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
    public void onCarListItemClick(View v, int position) {
        Car car = mCarListAdapter.getItem(position);
        Log.d(TAG, "onCarListItemClick: " + car);
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
        mCarListAdapter.cleanup();
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
