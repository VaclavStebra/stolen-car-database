package cz.muni.fi.a2p06.stolencardatabase.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.R;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarListFragment;
import cz.muni.fi.a2p06.stolencardatabase.utils.HelperMethods;

public class CarListAdapter extends FirebaseRecyclerAdapter<Car, CarListAdapter.CarItemHolder> {

    private final Fragment mFragment;
    private boolean isDataLoaded = false;

    public CarListAdapter(Class<Car> modelClass, int modelLayout, Class<CarItemHolder> viewHolderClass, Query ref, Fragment fragment) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mFragment = fragment;
    }

    @Override
    protected void populateViewHolder(CarItemHolder viewHolder, Car model, int position) {
        viewHolder.mManufacturerAndModel.setText(model.getManufacturer() + " " + model.getModel());
        viewHolder.mRegno.setText(HelperMethods.formatRegnoFromDB(model.getRegno()));
        viewHolder.mVin.setText(model.getVin());
        viewHolder.mDistrict.setText(model.getDistrict());
        viewHolder.mStolenDate.setText(HelperMethods.formatDate(model.getStolenDate()));

        StorageReference storageReference = model.getPhotoUrl() != null
                ? FirebaseStorage.getInstance().getReferenceFromUrl(model.getPhotoUrl()) : null;

        Glide.with(mFragment)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .placeholder(R.drawable.car_placeholder)
                .centerCrop()
                .into(viewHolder.mCarImage);
    }

    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        if (!isDataLoaded) {
            isDataLoaded = true;
            ((CarListFragment) mFragment).onDataLoaded(getItemCount() != 0 ? getItem(getItemCount() - 1) : null);
        }
    }

    @Override
    public CarItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CarItemHolder holder = super.onCreateViewHolder(parent, viewType);
        holder.setOnItemClickListener((CarListFragment) mFragment);
        return holder;
    }

    public static class CarItemHolder extends RecyclerView.ViewHolder {

        public interface OnCarItemClickListener {
            void onCarListItemClick(View v, int position);
        }

        private OnCarItemClickListener mListener;

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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCarListItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        void setOnItemClickListener(OnCarItemClickListener listener) {
            this.mListener = listener;
        }
    }
}
