package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Profile extends AppCompatActivity {

    private EditText mfname;
    private EditText mLname;
    private EditText mphone;
    private EditText mpin;
    private EditText mail;
    private EditText pswd;
    //Strings
    private String username;
    private String mmail;
    private String mpswd;
    private String pin;
    private String publicKey;
    private String privateKey;
    private String uid;
    private String phoneno;
    private ProgressDialog pDialog;
    private String status;
    public String message;
    public String check;

    private boolean flag;

    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object
    private final String TAG = Profile.class.getSimpleName();

    private Button insert;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mfname = (EditText) findViewById(R.id.first_name);
        mLname = (EditText) findViewById(R.id.last_name);
        mphone = (EditText) findViewById(R.id.phone_no);
        mpin = (EditText) findViewById(R.id.mpin);

        mail = (EditText)findViewById(R.id.email_id);
        pswd = (EditText)findViewById(R.id.password);

        OnFocusChange(mfname);
        OnFocusChange(mLname);
        OnFocusChange(mphone);
        OnFocusChange(mpin);
        OnFocusChange(mail);
        OnFocusChange(pswd);

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

        insert= (Button)findViewById(R.id.bnext);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(!validateForm()){
                return;
            }
                createAccount(mail.getText().toString(), pswd.getText().toString());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        //setting auth listener for Authentication object
        if (mAuthListener != null)
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

    private void showProgressDialog() {
        //if (pDialog != null && !pDialog.isShowing()) {
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
    //    }
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void createAccount(String email, String password) {

        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "Sign Up Successfully");
                    hideProgressDialog();
                    CheckPhone();

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
                    alertDialog.setTitle("Failed");
                    alertDialog.setMessage("Email already exists!!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    Log.d(TAG, "Sign Up Failed");
                    hideProgressDialog();
                }
            }
        });

    }

    public void CheckPhone(){
        showProgressDialog();
        flag = false;
        phoneno = mphone.getText().toString();
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot == null) {
                    return;
                }
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                    check= (String) String.valueOf(mDataSnapshot.child("phone").getValue());
                    if(check.equals( phoneno)){
                        flag=true;
                        AlertDialog alertDialog2 = new AlertDialog.Builder(Profile.this).create();
                        alertDialog2.setTitle("Failed");
                        alertDialog2.setMessage("Phone number is already registered");
                        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User account deleted.");
                                                        }
                                                    }
                                                });
                                        dialog.dismiss();
                                        hideProgressDialog();
                                    }
                                });
                        alertDialog2.show();
                        break;
                    }
                }
                if(flag==false) {    //getting keypiars
                    GetKeyPairs k = new GetKeyPairs();
                    String[] key = k.pairofkeys();
                    privateKey = key[0];
                    publicKey = key[1];
                    new SendPostRequest().execute();
                }
                Log.d(TAG, "Value is: " + check);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void writeNewUser() {
        //showProgressDialog();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        pin = mpin.getText().toString();
        mmail=mail.getText().toString();
        mpswd = pswd.getText().toString();
        username = mfname.getText().toString() + " " + mLname.getText().toString();
        phoneno = mphone.getText().toString();
        User user1 = new User(phoneno, username, mmail, mpswd, pin, publicKey, privateKey);
        showProgressDialog();
        myRef.child("users").child(uid).setValue(user1, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                 hideProgressDialog();
                    if (databaseError != null) {
                        //Toast.makeText(Profile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog1 = new AlertDialog.Builder(Profile.this).create();
                        alertDialog1.setTitle("Failed");
                        alertDialog1.setMessage(databaseError.getMessage());
                        alertDialog1.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User account deleted.");
                                                        }
                                                    }
                                                });
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog1.show();
                    } else {
                        //Toast.makeText(Profile.this, "Insert successfully", Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog2 = new AlertDialog.Builder(Profile.this).create();
                        alertDialog2.setTitle("Succesfully");
                        alertDialog2.setMessage("Account Created successfully");
                        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(Profile.this, Home_page.class);
                                        startActivity(i);
                                        finish();
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog2.show();
                    }
                    hideProgressDialog();
                }
            });
    }

     private class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            pDialog = new ProgressDialog(Profile.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL("http://192.168.12.248:4000/createAccount");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("senderPublicKey", publicKey);//public key of sender
                postDataParams.put("senderPrivateKey", privateKey);// private key of sender

                Log.e("params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(30000 /* milliseconds */);
                conn.setConnectTimeout(30000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                JSONObject obj = new JSONObject(result);
                status = obj.getString("success");
                message = obj.getString("message");
                if (status.equals("1")) {
                    writeNewUser();
                }else{
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
                    alertDialog.setTitle("Failed");
                    alertDialog.setMessage("Try Again!!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User account deleted.");
                                    }
                                }
                            });
                }

            } catch (Exception e) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                }
                            }
                        });
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    private boolean validateForm() {
        boolean valid = true;

        String firstname = mfname.getText().toString();

        if (TextUtils.isEmpty(firstname)) {
            mfname.setError("Required.");
            valid = false;
        } else {
            mfname.setError(null);
        }

        String lastname = mLname.getText().toString();

        if (TextUtils.isEmpty(lastname)) {
            mLname.setError("Required.");
            valid = false;
        } else {
            mLname.setError(null);
        }


        String password = pswd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pswd.setError("Required.");

        } else {
            if((password.length()<8)){

            valid = false;
            AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
            alertDialog.setTitle("Failed");
            alertDialog.setMessage("MIN Password 8 char");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
            pswd.setError(null);
        }

        String email = mail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mail.setError("Required.");
            valid = false;
        } else {
            mail.setError(null);
        }
        String phone = mphone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            mphone.setError("Required.");
            valid = false;
        } else {
            if((phone.length()!=10)){
                valid = false;
                AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
                alertDialog.setTitle("Failed");
                alertDialog.setMessage("Phone length should be 10");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            mphone.setError(null);
        }
        String pin = mpin.getText().toString();
        if ((TextUtils.isEmpty(pin)) ) {
            mpin.setError("Required.");

        } else {
            if((pin.length()!=6)){
            valid = false;
            AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create();
            alertDialog.setTitle("Failed");
            alertDialog.setMessage("PIN Length should 6");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
            mpin.setError(null);
        }
       return valid;
    }

    public void OnFocusChange(EditText view) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Open keyboard
                    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(v, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }


}//end of class
