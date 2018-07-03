package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.Intent;
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
                    createAccount(mEmailField.getText().toString(),mPasswordField.getText().toString());
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


    /**
     * Method to create account on Firebase
     * @param email or user id of user
     * @param password password
     */
    private void createAccount(String email, String password) {
        // Toast.makeText(MainActivity.this, "creating account with :" + email, Toast.LENGTH_SHORT).show();
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        Log.d(TAG,"Create Account process complete with false ");
                        if (task.isSuccessful()) {
                            Log.d(TAG,"Sign Up Successfully");
                            Toast.makeText(MainActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                            Intent success = new Intent(MainActivity.this,Home_page.class);
                            startActivity(success);
                        }else{
                            Log.d(TAG,"Sign Up Failed");
                            Toast.makeText(MainActivity.this, "Sign Up Failed.", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

        /**
         * Method to sign in with user id and password
         * @param email  email of user
         * @param password passworrd of user
         */
        private void signIn(String email, String password) {
           // Toast.makeText(MainActivity.this, "Signing in with account :" + email, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                                Intent success = new Intent(MainActivity.this,Home_page.class);
                                startActivity(success);
                            }else{
                                Log.d(TAG,"Sign in Failed");
                                Toast.makeText(MainActivity.this, "Sign Up Failed.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"Sign in Failed with error :"+task.getException());
                                Toast.makeText(MainActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                            }
                            hideProgressDialog();
                        }
                    });
        }



//        /**
//         * Method to sign out
//         */
//        private void signOut() {
//            mAuth.signOut();
//
//        }

        /**
         * Method to check all validation is success or not
         * @return true is validation true else false
         */
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


    }

