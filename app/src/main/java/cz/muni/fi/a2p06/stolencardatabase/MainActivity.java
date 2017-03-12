package cz.muni.fi.a2p06.stolencardatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import cz.muni.fi.a2p06.stolencardatabase.entity.Car;
import cz.muni.fi.a2p06.stolencardatabase.entity.Coordinates;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.text_view);
        //this.testWrite();
        this.testRead(textView);
        //this.addCar();
    }

    private void testWrite() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");

    }

    private void testRead(final TextView view) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                view.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Just an example of how to write to firebase "list"
    private void addCar() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // note the call to push, https://firebase.google.com/docs/database/web/lists-of-data#append_to_a_list_of_data
        DatabaseReference cars = database.getReference("cars").push();

        // prepare car object
        Car car = new Car();
        car.setColor("modra tmava");
        car.setEngine("2.0 TDI");
        car.setManufacturer("Volkswagen");
        car.setPhoto("gs://stolen-car-database.appspot.com/PAS0.jpg");
        car.setProductionYear(2002);
        car.setRegno("1XY1234");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 13);
        car.setStolenDate(cal.getTimeInMillis());
        car.setModel("PASSAT VARIANT");
        car.setVin("WVWZZZ12345624");
        Coordinates coords = new Coordinates();
        coords.setLat(49.210019);
        coords.setLon(16.598789);
        car.setLocation(coords);

        // save to database
        cars.setValue(car);
    }
}
