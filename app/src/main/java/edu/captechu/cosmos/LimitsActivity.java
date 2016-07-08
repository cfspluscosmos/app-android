package edu.captechu.cosmos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LimitsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limits);

        final EditText min_light = (EditText) findViewById(R.id.input_light_min);
        final EditText max_light = (EditText) findViewById(R.id.input_light_max);
        final EditText min_temp = (EditText) findViewById(R.id.input_temp_min);
        final EditText max_temp = (EditText) findViewById(R.id.input_temp_max);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference limitsRef = database.getReference("limits");

        limitsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                min_light.setText(dataSnapshot.child("limit_light_min").getValue().toString());
                max_light.setText(dataSnapshot.child("limit_light_max").getValue().toString());
                min_temp.setText(dataSnapshot.child("limit_temp_min").getValue().toString());
                max_temp.setText(dataSnapshot.child("limit_temp_max").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button button = (Button) findViewById(R.id.btn_change);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (min_light.getText() != null && !(min_light.getText().toString().equals(""))) {
                    limitsRef.child("limit_light_min").setValue(min_light.getText().toString());
                }
                if (max_light.getText() != null && !(max_light.getText().toString().equals(""))) {
                    limitsRef.child("limit_light_max").setValue(max_light.getText().toString());
                }
                if (min_temp.getText() != null && !(min_temp.getText().toString().equals(""))) {
                    limitsRef.child("limit_temp_min").setValue(min_temp.getText().toString());
                }
                if (max_temp.getText() != null && !(max_temp.getText().toString().equals(""))) {
                    limitsRef.child("limit_temp_max").setValue(max_temp.getText().toString());
                }
                onBackPressed();
            }
        });
    }
}
