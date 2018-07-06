package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class passbook extends AppCompatActivity {
    TextView wal, t;
    String publicKey;
   private String payments;
    private String TAG = passbook.class.getSimpleName();
    public ListView lv;
    private ProgressDialog pDialog;
    public ArrayList<HashMap<String, String>> history;
    private String phone;
    private String rpubK;
    private String to_id, from_id, amount, timestamp;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object
    private HashMap<String,String> entry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook);

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
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        publicKey=getIntent().getStringExtra("pub");
        payments="https://horizon-testnet.Stellar.org/accounts/"+publicKey+"/payments?limit=30&order=desc";

        wal = (TextView) findViewById(R.id.wallet);
        String b = getIntent().getStringExtra("balance");
        wal.setText(b);
        history= new ArrayList<>();
        lv= (ListView)findViewById(R.id.list);

        new passbook.display_history().execute();
    }

    private class display_history extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(passbook.this);
            pDialog.setMessage("Loading....");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            // String t = sh.makeServiceCall(transactions);
            String p = sh.makeServiceCall(payments);
            //Log.e(TAG, "Response from url: " + jsonStr);
            if (p != null) {
                try {
                    //JSONObject trans = new JSONObject(t);
                    JSONObject pay = new JSONObject(p);
                    JSONObject obj = pay.getJSONObject("_embedded");
                    JSONArray rec = obj.getJSONArray("records");
                    for (int i=0;i<rec.length();i++)
                    {
                        //loop for multiple records --- PRAGYA
                        JSONObject ob = rec.getJSONObject(i);
                        to_id = ob.getString("to");
                        from_id = ob.getString("from");
                        amount = ob.getString("amount");
                        timestamp = ob.getString("created_at");
                         entry = new HashMap<>();
                        // adding each child node to HashMap key => value

                        if(from_id.equals(publicKey)){

                         /*   myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    if (dataSnapshot == null) {
                                        return;
                                    }
                                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                                       rpubK= (String) String.valueOf(mDataSnapshot.child("publicKey").getValue());
                                        //rpubK= (String) mDataSnapshot.child("publicKey").getValue();
                                        // phone =user.get("phone");
                                        if( rpubK == to_id ){
                                            phone= (String)  String.valueOf(mDataSnapshot.child("phone").getValue());
                                            entry.put("acc_id", "To: "+phone);
                                            entry.put("funds_transferred", "-"+amount);
                                            entry.put("time", "At: "+ timestamp);
                                            history.add(entry);
                                            break;
                                        }
                                    }

                                    Log.d(TAG, "Value is: " + rpubK);
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });*/
                            entry.put("acc_id", "To: "+to_id);
                            entry.put("funds_transferred", "-"+amount);

                        }
                        else{
                           /* myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    if (dataSnapshot == null) {
                                        return;
                                    }
                                    for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                                        rpubK= (String) String.valueOf(mDataSnapshot.child("publicKey").getValue());
                                        // phone =user.get("phone");
                                        if(rpubK.contentEquals(from_id)){
                                            phone= (String) String.valueOf(mDataSnapshot.child("phone").getValue());
                                            entry.put("acc_id", "From: " +phone);
                                            entry.put("funds_transferred", "+"+amount);
                                            entry.put("time", "At: "+timestamp);
                                            history.add(entry);
                                            break;
                                        }
                                    }
//                                    history.add(entry);
                                    Log.d(TAG, "Value is: " + rpubK);
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });*/

                            entry.put("acc_id", "From: " +from_id);
                            entry.put("funds_transferred", "+"+amount);
                        }
                        // adding contact to contact list
                        entry.put("time", "At: "+ timestamp);
                        history.add(entry);
                    }
                }
                catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            } return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(
                    passbook.this, history,
                    R.layout.list_item_passbook, new String[]{"acc_id", "funds_transferred", "time"},
                    new int[]{R.id.rec_id, R.id.balance, R.id.time_stamp});

            lv.setAdapter(adapter);
        }
    }


}