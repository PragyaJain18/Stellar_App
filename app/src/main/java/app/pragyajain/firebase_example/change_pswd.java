package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class change_pswd extends AppCompatActivity {

    private EditText mCurrent;
    private EditText mNewpass;
    private EditText mconfirm;

    private Button change;

    private FirebaseAuth mAuth;


    private ProgressDialog mProgressDialog;
    private final String TAG=change_pswd.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_change);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home_page.class));
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Checking....");
        mProgressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        mCurrent = (EditText)findViewById(R.id.curr_pswd);
        mNewpass = (EditText)findViewById(R.id.new_password);
        mconfirm = (EditText)findViewById(R.id.re_password);

        change = (Button)findViewById(R.id.change);


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(mCurrent.getText().toString(),mNewpass.getText().toString(),mconfirm.getText().toString());
                Intent i= new Intent(change_pswd.this,MainActivity.class);
                startActivity(i);
            }
        });

    }

    private void changePassword (String currPass,String newPass, String conPass){
        if (!validateForm()) {
            return;
        }
        //updating the password of the account in the firebase ;
    }


    private boolean validateForm() {
        boolean valid = true;

        String CPass = mCurrent.getText().toString();

        if (TextUtils.isEmpty(CPass)) {
            mCurrent.setError("Required.");
            valid = false;
        } else {
            mCurrent.setError(null);
        }

        String password = mNewpass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mNewpass.setError("Required.");
            valid = false;
        } else {
            mNewpass.setError(null);
        }
        String con_pass = mconfirm.getText().toString();
        if (TextUtils.isEmpty(con_pass)) {
            mconfirm.setError("Required.");
            valid = false;
        } else {
            mconfirm.setError(null);
        }
//        if(true) {
//            if (con_pass == password) {
//                return valid;
//            }
//        }
        return false;
    }


}
