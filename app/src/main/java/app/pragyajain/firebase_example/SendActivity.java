package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.*;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.constraints.Null;

public class SendActivity extends AppCompatActivity
{
    private EditText mcustid;
    private EditText mamount;
    private Button msend;
    private TextView wal;
    private String mid;
    private String pin;
    private String pubK;
    private String pvtK;
    private String rpubK;
    private String mamt;
    private  String phone;
    private String mphone;
    private ProgressDialog pDialog;
    private String status;
    private String message;
    private String balance;
    private boolean flag = false;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object
    private String uid;

    private AlertDialog.Builder builder;
    private String TAG = SendActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Fund Transfer");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home_page.class));
                finish();
            }
        });

              wal=(TextView)findViewById(R.id.wallet);
        balance=getIntent().getStringExtra("balance");
        wal.setText(balance);

        builder = new AlertDialog.Builder(SendActivity.this);
        mcustid=(EditText)findViewById(R.id.customer_id);
        mamount=(EditText)findViewById(R.id.payamt);

        msend=(Button)findViewById(R.id.send);

        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateForm())
                {
                    return ;
                }
//                mid = mcustid.getText().toString();
                mamt= mamount.getText().toString();
                if(Integer.parseInt(mamt)>0)
                {
                    pDialog = new ProgressDialog(SendActivity.this);
                    pDialog.setMessage("Checking Phone no is valid or Not");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    getData();
                    pDialog.dismiss();
                }
                else
                {
                    AlertDialog alertDialog2 = new AlertDialog.Builder(SendActivity.this).create();
                    alertDialog2.setTitle("Failed");
                    alertDialog2.setMessage("ReEnter amount");
                    alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog2.show();
                }

            }
        });
    }

    public void onBackPressed()
    {
        finish();
    }
    public void alertbox(){
        builder.setMessage("Are You Sure ! \n You want to Transfer"+"\n\n To: "+mid+"\n\n Amount: "+mamt)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        hello();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.dismiss();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Confirmation");
        alert.show();
    }

    private void hello()
    {
        LayoutInflater li = LayoutInflater.from(SendActivity.this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendActivity.this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(userInput.getText().toString().equals(pin)){
                                    new SendPostRequest().execute();
                                }
                                else{
                                    AlertDialog alertDialog2 = new AlertDialog.Builder(SendActivity.this).create();
                                    alertDialog2.setTitle("Failed");
                                    alertDialog2.setMessage("ReEnter your pin");
                                    alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    hello();
                                                }
                                            });
                                    alertDialog2.show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               // stats.setText("");
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void getCustDet(){
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        mid = mcustid.getText().toString();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot == null) {
                    return;
                }

                // HashMap<String,String> user =(HashMap<String, String>) dataSnapshot.child(uid).getChildren();
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {

//                    HashMap<String,String> user = (HashMap<String, String>) mDataSnapshot.child(bup).getValue();
                    phone= (String) String.valueOf(mDataSnapshot.child("phone").getValue());
                   // phone =user.get("phone");
                    if(phone.contentEquals(mid)){
                        flag = true;
                        rpubK= (String) mDataSnapshot.child("publicKey").getValue();
                        alertbox();
                        break;
                    }

                }
                if(flag==false){
                    AlertDialog alertDialog2 = new AlertDialog.Builder(SendActivity.this).create();
                    alertDialog2.setTitle("Failed");
                    alertDialog2.setMessage("Invalid Number");
                    alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog2.show();
                    return;
                }

                Log.d(TAG, "Value is: " + rpubK);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void getData()
    {
        myRef = FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        myRef= myRef.child(uid);
        myRef.addValueEventListener(new ValueEventListener() {

            String check = mcustid.getText().toString();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot == null) {
                    return;
                }

                // HashMap<String,String> user =(HashMap<String, String>) dataSnapshot.child(uid).getChildren();
                HashMap<String,String> user = (HashMap<String, String>) dataSnapshot.getValue();
                mphone = user.get("phone");
                pin=user.get("pin");
                pubK = user.get("publicKey");
                pvtK= user.get("privateKey");
                if(!(mphone.equals(check)))
                    getCustDet();
                else{
                    AlertDialog alertDialog2 = new AlertDialog.Builder(SendActivity.this).create();
                    alertDialog2.setTitle("Failed");
                    alertDialog2.setMessage("Invalid Number");
                    alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog2.show();
                    return;
                }
                 Log.d(TAG, "Value is: " + pubK);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){
            pDialog = new ProgressDialog(SendActivity.this);
            pDialog.setMessage("Processing...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... arg0) {

            try{
                //hashMap function to be called
                //HashMap(mid);
                URL url = new URL("http://192.168.13.58:4000/Payment");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("senderPublicKey", pubK);//public key of sender
                postDataParams.put("senderPrivateKey",pvtK );// private key of sender
                postDataParams.put("receiverPublicKey",rpubK);//public key of receiver

                postDataParams.put("amount",mamt);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
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

                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";
                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e) {
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
                if(status=="1")
                {
                    //go to transaction_success activity
                }

                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
           // test.setText(result);
            AlertDialog alertDialog = new AlertDialog.Builder(SendActivity.this).create();
            alertDialog.setTitle("Status");
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent k = new Intent(SendActivity.this,Home_page.class);
                            startActivity(k);
                            finish();
                        }
                    });
            alertDialog.show();

        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while(itr.hasNext()){
            String key= itr.next();
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

        String phone = mcustid.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            mcustid.setError("Required.");
            valid = false;
        } else { if((phone.length()!=10)){

            valid = false;
            AlertDialog alertDialog = new AlertDialog.Builder(SendActivity.this).create();
            alertDialog.setTitle("Failed");
            alertDialog.setMessage("phone number of incorrect length");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Try Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
            mcustid.setError(null);
        }

        String amount = mamount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            mamount.setError("Required.");
            valid = false;
        } else {
            mamount.setError(null);
        }

        return valid;
    }
}//end of class

