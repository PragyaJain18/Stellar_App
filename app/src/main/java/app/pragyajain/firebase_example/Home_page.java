package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Home_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object
    private String uid;
    private String u;
    private String v;
    public String accno, bal;
    //String userId = "GCNE242XH5PQ7JPS7EII52HPV6HDBYDZWO57WFEQJG5G737DOF4ZAAYM";
    String userId;

    private String profile;
    private String TAG = Home_page.class.getSimpleName();
    TextView accountID, current_balance;
    private ProgressDialog pDialog;
    GridLayout gridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridLayout=(GridLayout)findViewById(R.id.mainGrid);
        setSingleEvent(gridLayout);
        accountID = (TextView)findViewById(R.id.user_id);
        current_balance = (TextView)findViewById(R.id.balance);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();

     getData(uid);
    }
    private void setSingleEvent(GridLayout gridLayout) {
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(Home.this,"Clicked at index "+ finalI, Toast.LENGTH_SHORT).show();
                    if(finalI == 0)
                    {
                        Intent intent = new Intent(Home_page.this, passbook.class);
                        Bundle wallet = new Bundle();
                        wallet.putString("balance", bal);
                        intent.putExtras(wallet);
                        intent.putExtra("pub",u);
                        startActivity(intent);
                    }
                    else if (finalI == 1)
                    {
                        Intent intent = new Intent(Home_page.this, SendActivity.class);
                        intent.putExtra("pub",u);
                        intent.putExtra("pvt",v);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void showProgressDialog() {
            if (pDialog != null && !pDialog.isShowing())
            pDialog .show();
    }
    /**
     * Method to hide progress dialog
     */
    private void hideProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
    private class updateText extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Home_page.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String p = arg0[0];
            String file = sh.makeServiceCall(p);
            Log.e(TAG, "Response from url: " + file);
            if (file != null)
            {
                return file;
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                return null;
            }
        }
        protected void onPostExecute(String file) {

            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                JSONObject obj = new JSONObject(file);
                accno = obj.getString("id");
                JSONArray balance = obj.getJSONArray("balances");
                for(int i=0;i<balance.length();i++)
                {
                    JSONObject b = balance.getJSONObject(i);
                    String asset = b.getString("asset_type");
                    if(asset.equals("native"))
                    {
                        bal = b.getString("balance");
                        accountID.setText(accno);
                        current_balance.setText(bal);
                        break;
                    }
                    else
                        accountID.setText("Faulty");
                }

            } catch (JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home)
        {
            Intent intent = new Intent(this, Home_page.class);
            startActivity(intent);
        } else if (id == R.id.change)
        {
            Intent intent = new Intent(this, change_pswd.class);
            startActivity(intent);
        } else if (id == R.id.logout)
        {   mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getData(String uid)
    {
        showProgressDialog();
        myRef= myRef.child("users").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot == null) {
                    return;
                }

                HashMap<String,String> user =(HashMap<String, String>) dataSnapshot.getValue();
//                HashMap<String,String> user = (HashMap<String, String>) dataSnapshot.getValue();
//              String u =dataSnapshot.child("publicKey").getValue().toString();
                u = user.get("publicKey");
                v= user.get("privateKey");
                profile = "https://horizon-testnet.stellar.org/accounts/" + u;
                hideProgressDialog();
                new  updateText().execute(profile);
               // Log.d(TAG, "Value is: " + value);
        }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

}
