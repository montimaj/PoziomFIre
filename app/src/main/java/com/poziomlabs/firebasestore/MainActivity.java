package com.poziomlabs.firebasestore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mMyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText title = (EditText) findViewById(R.id.reviewTitle);
        final EditText body = (EditText) findViewById(R.id.reviewBody);
        Button save = (Button) findViewById(R.id.saveReview);
        Button get = (Button) findViewById(R.id.seeReview);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mMyRef = database.getReference().child("Users");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Review review = new Review(title.getText().toString(), body.getText().toString());
                Toast.makeText(getApplicationContext(), review.toString(), Toast.LENGTH_LONG).show();
                mMyRef.child(""+ review.getReviewId()).setValue(review);
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d("JSON: ", "" + snapshot.getValue());
                            Review review = snapshot.getValue(Review.class);
                            Log.d("Review Obj Val: ", review.toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }
        });
    }
}
