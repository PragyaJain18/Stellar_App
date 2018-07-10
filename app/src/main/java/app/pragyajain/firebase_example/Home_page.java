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
import android.widget.ImageButton;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Home_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference myRef;
    private FirebaseAuth mAuth; //FirebaseAuth object for Authentication
    private FirebaseAuth.AuthStateListener mAuthListener; //Listener for FirebaseAuth object
    private String uid;
    private String pub;
    private String v;
    private String email;
    private String name;
    private String bal;

    private String profile;
    private String TAG = Home_page.class.getSimpleName();
    private TextView accountHolder;
    private TextView current_balance;

    private ImageButton passbook;
    private ImageButton send;

    private TextView nav_user;
    private TextView nav_mail;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // gridLayout=(GridLayout)findViewById(R.id.mainGrid);
        //setSingleEvent(gridLayout);
        accountHolder = (TextView)findViewById(R.id.user_id);
        current_balance = (TextView)findViewById(R.id.balance);
        passbook=(ImageButton)findViewById(R.id.passbook);
        send=(ImageButton)findViewById(R.id.transfer);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hview= navigationView.getHeaderView(0);
        nav_user = (TextView)hview.findViewById(R.id.textv);
        nav_mail =(TextView)hview.findViewById(R.id.memail);

        getData();

        passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home_page.this,passbook.class);
                i.putExtra("balance",bal);
                i.putExtra("pub",pub);
                startActivity(i);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j =new Intent(Home_page.this,SendActivity.class);
                j.putExtra("balance",bal);
                startActivity(j);
            }
        });
    }
    protected void onDestroy()
    {
        super.onDestroy();
    }
    private void showProgressDialog() {
            //if (pDialog != null && !pDialog.isShowing())
            //pDialog .show();
        pDialog = new ProgressDialog(Home_page.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
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
            String profile_url = arg0[0];
            String file = sh.makeServiceCall(profile_url);
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
                //String mfile = file.substring(0, file.indexOf(" "));
                //String mname = file.substring(file.indexOf(" "));
                JSONObject obj = new JSONObject(file);
                JSONArray balance = obj.getJSONArray("balances");
                for(int i=0;i<balance.length();i++)
                {
                    JSONObject b = balance.getJSONObject(i);
                    String asset = b.getString("asset_type");
                    if(asset.equals("native"))
                    {
                        bal = b.getString("balance");
                        current_balance.setText(bal);
                        break;
                    }
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
        }
        else if(id == R.id.change_pin)  {
            Intent intent = new Intent(this, ChangePin.class);
            startActivity(intent);

        } else if (id == R.id.logout)
        {   mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   public void getData()
    {
        showProgressDialog();
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        myRef= myRef.child("users").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot == null) {
                    return;
                }
               // HashMap<String,String> user =(HashMap<String, String>) dataSnapshot.child(uid).getChildren();
                 HashMap<String,String> user = (HashMap<String, String>) dataSnapshot.getValue();
//              String u =dataSnapshot.child("publicKey").getValue().toString();
                pub = user.get("publicKey");
                email = user.get("email");
                name = user.get("username");
                accountHolder.setText(name);

                nav_user.setText(name);
                nav_mail.setText(email);

                profile = "https://horizon-testnet.Stellar.org/accounts/" + pub;
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
}//end of class