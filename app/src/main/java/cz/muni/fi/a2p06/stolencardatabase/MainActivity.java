package cz.muni.fi.a2p06.stolencardatabase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.fragments.AddCarFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarDetailFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarListFragment;

public class MainActivity extends AppCompatActivity implements CarListFragment.OnCarListFragmentInteractionListener {

    @Nullable
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        if (mFragmentContainer != null) {
            manageCarClickOnMobile(car);
        } else {
            manageCarClickOnTablet(car);
        }
    }

    private void manageCarClickOnMobile(Car car) {
        CarDetailFragment carDetailFragment = CarDetailFragment.newInstance(car);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, carDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void manageCarClickOnTablet(Car car) {
        updateCarDetailFragment(car);
    }

    @Override
    public void onAddCarClick() {
        if (mFragmentContainer != null) {
            manageAddCarClickOnMobile();
        } else {
            manageAddCarClickOnTablet();
        }
    }

    private void manageAddCarClickOnMobile() {
        AddCarFragment addCarFragment = AddCarFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addCarFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void manageAddCarClickOnTablet() {
        // TODO swap fragments
    }

    @Override
    public void onDataLoaded(Car car) {
        CarDetailFragment carFragment = (CarDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.car_detail_frag);
        if (carFragment != null && carFragment.getCar() == null) {
            updateCarDetailFragment(car);
        }
    }

    private void updateCarDetailFragment(Car car) {
        CarDetailFragment carFragment = (CarDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.car_detail_frag);
        if (carFragment != null) {
            carFragment.updateCarView(car);
        }
    }
}
