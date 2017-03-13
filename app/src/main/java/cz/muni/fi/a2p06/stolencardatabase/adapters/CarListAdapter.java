package cz.muni.fi.a2p06.stolencardatabase.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;

public class CarListAdapter extends FirebaseRecyclerAdapter<Car, CarListAdapter.CarItemHolder> {

    private final Context mContext;

    public CarListAdapter(Class<Car> modelClass, int modelLayout, Class<CarItemHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
    }

    @Override
    protected void populateViewHolder(CarItemHolder viewHolder, Car model, int position) {
        viewHolder.mManufacturerAndModel.setText(model.getManufacturer() + " " + model.getModel());
        viewHolder.mRegno.setText(mContext.getResources().getString(R.string.spz, model.getRegno()));
        viewHolder.mVin.setText(mContext.getString(R.string.vin, model.getVin()));

        viewHolder.mDistrict.setText(model.getDistrict());

        // TODO: Calendar
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(model.getStolenDate());
        DateFormat format = SimpleDateFormat.getDateInstance();
        viewHolder.mStolenDate.setText(format.format(calendar.getTime()));

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getPhotoUrl());

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .placeholder(R.drawable.car_placeholder)
                .centerCrop()
                .into(viewHolder.mCarImage);
    }

    public static class CarItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.car_image)
        ImageView mCarImage;
        @BindView(R.id.manuf_and_model)
        TextView mManufacturerAndModel;
        @BindView(R.id.regno)
        TextView mRegno;
        @BindView(R.id.vin)
        TextView mVin;
        @BindView(R.id.district)
        TextView mDistrict;
        @BindView(R.id.stolen_date)
        TextView mStolenDate;

        public CarItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
