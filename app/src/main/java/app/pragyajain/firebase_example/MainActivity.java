package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

        private EditText mEmailField;
        private EditText mPasswordField;

        private Button mLogin;
        private Button mSign;

        private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
        private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object

        private ProgressDialog mProgressDialog;
        private final String TAG=MainActivity.class.getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

//            Toolbar toolbar=(Toolbar)findViewById(R.id.my_toolbar);
//            toolbar.setTitle("Stellar");

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setCancelable(false);

            mEmailField =(EditText)findViewById(R.id.mail_id);
            mPasswordField=(EditText)findViewById(R.id.password);

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.d(TAG,"User is Already sign in");
                    } else {
                        Log.d(TAG,"User is not sign in");
                    }

                }
            };

            mLogin=(Button)findViewById(R.id.log_in);
            mSign=(Button)findViewById(R.id.signup);

            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn(mEmailField.getText().toString(),mPasswordField.getText().toString());
                                   }
            });

            mSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent success = new Intent(MainActivity.this,Profile.class);
                    startActivity(success);
                                 }
            });
        }

        @Override
        public void onStart() {
            super.onStart();
            //setting auth listener for Authentication object
            mAuth.addAuthStateListener(mAuthListener);
        }
        @Override
        public void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                //removing auth listener for Authentication object
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }
        /**
         * Method to show progress dialog
         */
        private void showProgressDialog() {
            if (mProgressDialog != null && !mProgressDialog.isShowing())
                mProgressDialog.show();
        }
        /**
         * Method to hide progress dialog
         */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
               mProgressDialog.dismiss();
    }

    private void signIn(String email, String password) {

        if (!validateForm()) {
              return;
    }
         showProgressDialog();
         mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG,"Sign in Successfully");

                                Intent success = new Intent(MainActivity.this,Home_page.class);
                                startActivity(success);
                                finish();
                            }else
                                {
                                Log.e(TAG,"Sign in Failed with error :"+task.getException());
                                AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this).create();
                                alertDialog2.setTitle("Failed");
                                alertDialog2.setMessage("Invalid Credentials");
                                alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog2.show();
                            }
                            hideProgressDialog();
                        }
                    });
        }


        private boolean validateForm() {
            boolean valid = true;

            String email = mEmailField.getText().toString();

            if (TextUtils.isEmpty(email)) {
                mEmailField.setError("Required.");
                valid = false;
            } else {
                mEmailField.setError(null);
            }

            String password = mPasswordField.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError("Required.");
                valid = false;
            } else {
                mPasswordField.setError(null);
            }

            return valid;
        }
}//end