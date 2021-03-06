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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class passbook extends AppCompatActivity {
    TextView wal, t;
    String publicKey;
    private String payments;
    private String TAG = passbook.class.getSimpleName();
    public ListView lv;
    private ProgressDialog pDialog;
    public ArrayList<HashMap<String, String>> history;
    private String phone;

    private String to_id, from_id, amount, timestamp, out;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object
    private HashMap<String, String> entry;

    private JSONArray rec;
    int mRecordCounter = 0;

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
                startActivity(new Intent(getApplicationContext(), Home_page.class));
            }
        });
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

        publicKey = getIntent().getStringExtra("pub");
        payments = "https://horizon-testnet.Stellar.org/accounts/" + publicKey + "/payments?limit=30&order=desc";

        wal = (TextView) findViewById(R.id.wallet);
        String b = getIntent().getStringExtra("balance");
        float wallet = Float.parseFloat(b);
        wal.setText("$ " + String.format("%.2f", wallet));
        history = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        //entry = new HashMap<>();

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
                    rec = obj.getJSONArray("records");

                    rec.remove(rec.length() - 1);

                } catch (final Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            fetchUpdateRecord(mRecordCounter);
            if (pDialog.isShowing())
                pDialog.dismiss();

        }
    }

    private void findphone(String publickey) {

        final String checkfrom = publickey;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }
                for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {

                    if (String.valueOf(mDataSnapshot.child("publicKey").getValue()).contentEquals(checkfrom)) {
                        phone = String.valueOf(mDataSnapshot.child("phone").getValue());
                        entry.put("phone", phone);
                        history.add(entry);
                        mRecordCounter++;
                        fetchUpdateRecord(mRecordCounter);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
               // Failed to read value
            }
        });
    }

    public void fetchUpdateRecord(int pos) {

        if (pos < rec.length()) {

            try {

                JSONObject ob = rec.getJSONObject(pos);
                to_id = ob.getString("to");
                from_id = ob.getString("from");
                amount = ob.getString("amount");
                timestamp = ob.getString("created_at");
                float wallet = Float.parseFloat(amount);
                String money = String.format("%.2f", wallet);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                DateFormat dateFormat2 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                dateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
                Date date = dateFormat.parse(timestamp);
                out = dateFormat2.format(date);

                entry = new HashMap<>();

                if (from_id.equals(publicKey)) {
                    entry.put("caption", "Money Sent");
//                  entry.put("acc_id", to_id);
                    entry.put("time", out);
                    entry.put("funds_transferred", "-" + money);

                    if (pos == rec.length() - 1) {

                        entry.put("phone", to_id);
                        history.add(entry);

                        updateRecordsInList();

                    } else {
                        findphone(to_id);
                    }

                } else {
                    entry.put("caption", "Money Received");
//                            entry.put("acc_id", from_id);
                    entry.put("time", out);
                    entry.put("funds_transferred", "+" + money);

                    if (pos == rec.length() - 1) {

                        entry.put("phone", from_id);
                        history.add(entry);

                        updateRecordsInList();

                    } else {
                        findphone(from_id);
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void updateRecordsInList() {
        ListAdapter adapter = new SimpleAdapter(
                passbook.this, history,
                R.layout.list_item_passbook, new String[]{"caption", "phone", "funds_transferred", "time"},
                new int[]{R.id.title, R.id.rec_id, R.id.balance, R.id.time_stamp});

        lv.setAdapter(adapter);

    }

}