package cz.muni.fi.a2p06.stolencardatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.fragments.AddCarFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarDetailFragment;
import cz.muni.fi.a2p06.stolencardatabase.fragments.CarListFragment;
import cz.muni.fi.a2p06.stolencardatabase.utils.HelperMethods;

public class MainActivity extends AppCompatActivity implements
        CarListFragment.OnCarListFragmentInteractionListener,
        CarDetailFragment.OnCarDetailFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private FragmentManager mFragmentManager;

    @Nullable
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;
    @BindView(R.id.main_activity_root_view)
    CoordinatorLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();

        if (mFragmentContainer != null) {
            if (savedInstanceState != null) {
                return;
            }

            CarListFragment carListFragment = new CarListFragment();
            mFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, carListFragment).commit();
        } else {
            CarDetailFragment carDetailFragment = CarDetailFragment.newInstance(null, true, true);
            mFragmentManager.beginTransaction()
                    .add(R.id.large_fragment_container, carDetailFragment)
                    .addToBackStack("initTransaction")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            inflater.inflate(R.menu.menu, menu);
            return true;
        } else {
            inflater.inflate(R.menu.not_logged_menu, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                doLogout();
                return true;
            case R.id.log_in:
                Intent loginIntent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(loginIntent);
                finish();
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
        CarDetailFragment carDetailFragment = CarDetailFragment.newInstance(car, true, true);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
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
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, addCarFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void manageAddCarClickOnTablet() {
        AddCarFragment addCarFragment = AddCarFragment.newInstance();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.large_fragment_container, addCarFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onDataLoaded(Car car) {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.large_fragment_container);
        if (fragment != null && fragment instanceof CarDetailFragment
                && ((CarDetailFragment) fragment).getCar() == null) {
            updateCarDetailFragment(car);
        }
    }

    private void updateCarDetailFragment(Car car) {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.large_fragment_container);
        if (!(fragment instanceof CarDetailFragment)) {
            mFragmentManager.popBackStack("initTransaction", 0);
        }

        fragment = mFragmentManager.findFragmentById(R.id.large_fragment_container);
        if (fragment instanceof CarDetailFragment) {
            ((CarDetailFragment) fragment).updateCarView(car);
        }
    }

    public void doLogout() {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(uid);
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public void onDeleteCar(final Car car) {
        if (mFragmentContainer != null) {
            manageDeleteCarClickOnMobile();
        } else {
            manageDeleteCarClickOnTablet();
        }
        Snackbar snackbar = Snackbar
                .make(mRootView, "Car is deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(mRootView, "Car is restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                }).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int dismissType) {
                        super.onDismissed(transientBottomBar, dismissType);
                        if (dismissType == DISMISS_EVENT_TIMEOUT || dismissType == DISMISS_EVENT_SWIPE
                                || dismissType == DISMISS_EVENT_MANUAL) {
                            deleteCar(car);
                        }
                    }
                });
        snackbar.show();
    }


    private void deleteCar(Car car) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cars");
        Query query = ref.orderByChild("regno").equalTo(car.getRegno());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Car car = snapshot.getValue(Car.class);
                    HelperMethods.removePhotoFromDb(car.getPhotoUrl());
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
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
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
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
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, addCarFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void manageEditCarClickOnTablet(Car car) {
        AddCarFragment addCarFragment = AddCarFragment.newInstance(car);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.large_fragment_container, addCarFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
