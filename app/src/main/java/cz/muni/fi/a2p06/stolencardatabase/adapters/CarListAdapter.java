package cz.muni.fi.a2p06.stolencardatabase.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

/**
 * Created by robert on 12.3.2017.
 */

public class CarListAdapter extends FirebaseRecyclerAdapter<Car, CarListAdapter.CarItemHolder> {

    private final RequestManager glide;

    public CarListAdapter(Class<Car> modelClass, int modelLayout, Class<CarItemHolder> viewHolderClass, Query ref, RequestManager glide) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.glide = glide;
    }

    @Override
    protected void populateViewHolder(CarItemHolder viewHolder, Car model, int position) {
        viewHolder.mManufacturerAndModel.setText(model.getManufacturer() + " " + model.getModel());
        viewHolder.mRegno.setText(model.getRegno());
        viewHolder.mVin.setText(model.getVin());
        viewHolder.mStolenDate.setText(model.getVin());
        viewHolder.mDistrict.setText(model.getDistrict());

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getPhotoUrl());

        glide.using(new FirebaseImageLoader())
                .load(storageReference)
                .placeholder(R.drawable.car_placeholder)
                .fitCenter()
                .into(viewHolder.mCarImage);
    }

    public static class CarItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.car_image)
        private ImageView mCarImage;
        @BindView(R.id.manuf_and_model)
        private TextView mManufacturerAndModel;
        @BindView(R.id.regno)
        private TextView mRegno;
        @BindView(R.id.vin)
        private TextView mVin;
        @BindView(R.id.district)
        private TextView mDistrict;
        @BindView(R.id.stolen_date)
        private TextView mStolenDate;

        public CarItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
