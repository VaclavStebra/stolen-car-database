package cz.muni.fi.a2p06.stolencardatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.fragments.AddCarFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarDetailFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarListFragment;

public class MainActivity extends AppCompatActivity implements
        CarListFragment.OnCarListFragmentInteractionListener,
        CarDetailFragment.OnCarDetailFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    @Nullable
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(loginIntent);
            finish();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAuth.getCurrentUser() != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                doLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(Car car) {
        if (mFragmentContainer != null) {
            manageCarClickOnMobile(car);
        } else {
            updateCarDetailFragment(car);
        }
    }

    private void manageCarClickOnMobile(Car car) {
        CarDetailFragment carDetailFragment = CarDetailFragment.newInstance(car, true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, carDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    public void doLogout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public void onDeleteCar() {
        if (mFragmentContainer != null) {
            manageDeleteCarClickOnMobile();
        } else {
            manageDeleteCarClickOnTablet();
        }
    }

    @Override
    public void onEditCar(Car car) {
        if (mFragmentContainer != null) {
            manageEditCarClickOnMobile(car);
        } else {
            manageEditCarClickOnTablet(car);
        }
    }

    private void manageDeleteCarClickOnMobile() {
        CarListFragment carListFragment = new CarListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, carListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void manageDeleteCarClickOnTablet() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cars");
        Query query = ref.orderByChild("stolen_date").limitToFirst(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Car car = snapshot.getValue(Car.class);
                    updateCarDetailFragment(car);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void manageEditCarClickOnMobile(Car car) {
        AddCarFragment addCarFragment = AddCarFragment.newInstance(car);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addCarFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void manageEditCarClickOnTablet(Car car) {
        // TODO swap fragments
    }
}
