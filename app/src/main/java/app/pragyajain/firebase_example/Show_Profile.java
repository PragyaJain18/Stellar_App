package app.pragyajain.firebase_example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Show_Profile extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView phone;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object
    private String uid;

    private String mphone;
    private String memail;
    private String mname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Profile");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home_page.class));
                finish();
            }
        });

        name =(TextView)findViewById(R.id.setname);
       phone =(TextView)findViewById(R.id.setphone);
        email =(TextView)findViewById(R.id.setmail);

        getData();
    }
    public void getData()
    {
//        showProgressDialog();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        myRef= myRef.child("users").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot == null) {
                    return;
                }

                HashMap<String,String> user = (HashMap<String, String>) dataSnapshot.getValue();
                mphone = String.valueOf(user.get("phone"));
                memail = user.get("email");
                mname = user.get("username");


               name.setText(mname);
               email.setText(memail);
               phone.setText(mphone);

            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
