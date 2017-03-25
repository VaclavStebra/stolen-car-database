package cz.muni.fi.a2p06.stolencardatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarListFragment;

public class MainActivity extends AppCompatActivity implements CarListFragment.OnCarListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onItemClick(Car car) {
        // TODO: launch DetailFragment
    }

    @Override
    public void onAddCarClick() {
        // TODO: launch AddCar Fragment
    }
}
