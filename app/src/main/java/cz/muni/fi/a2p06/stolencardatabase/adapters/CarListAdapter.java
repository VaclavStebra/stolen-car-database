package cz.muni.fi.a2p06.stolencardatabase.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

/**
 * Created by robert on 12.3.2017.
 */

public class CarListAdapter extends FirebaseRecyclerAdapter<Car, CarListAdapter.CarItemHolder> {

    public CarListAdapter(Class<Car> modelClass, int modelLayout, Class<CarItemHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(CarItemHolder viewHolder, Car model, int position) {

    }

    static class CarItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.car_image)
        private ImageView mCarImage;

        public CarItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
