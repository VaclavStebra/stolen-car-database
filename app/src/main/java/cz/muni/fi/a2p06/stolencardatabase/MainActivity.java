package cz.muni.fi.a2p06.stolencardatabase;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarDetailFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarListFragment;

public class MainActivity extends FragmentActivity implements CarListFragment.OnCarListFragmentInteractionListener {

    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (mFragmentContainer!= null) {
            if (savedInstanceState != null) {
                return;
            }

            CarListFragment carListFragment = new CarListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, carListFragment).commit();
        }
    }

    @Override
    public void onItemClick(Car car) {
        CarDetailFragment carDetailFragment = CarDetailFragment.newInstance(car);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, carDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onAddCarClick() {
        // TODO: launch AddCar Fragment
    }
}
