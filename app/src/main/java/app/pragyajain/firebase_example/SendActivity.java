package app.pragyajain.firebase_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.validation.constraints.Null;

public class SendActivity extends AppCompatActivity
{   private EditText mcustid;
    private EditText mamount;
    private Button msend;
    private TextView test;
    private String pvtK;
    private String pubK;
    private String mamt;
    private ProgressDialog pDialog;
    private String status;
    private String message;

    private String TAG = SendActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb);
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

        mcustid=(EditText)findViewById(R.id.customer_id);
        mamount=(EditText)findViewById(R.id.payamt);
        test = (TextView)findViewById(R.id.result);
        msend=(Button)findViewById(R.id.send);

        pvtK = getIntent().getStringExtra("pvt");
        pubK =getIntent().getStringExtra("pub");


        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mamt= mamount.getText().toString();
                new SendPostRequest().execute();
            }
        });

    }



    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){
            pDialog = new ProgressDialog(SendActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL("http://192.168.20.40:4000/Payment");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("senderPublicKey", pubK);
                postDataParams.put("senderPrivateKey",pvtK );
                postDataParams.put("receiverPublicKey","GBC4INS4QJXFAXQCCB7JIOQP6T6BNVXNDBZIK4LS7MQ7EIYQVQ2IZT2K");
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
            /*try {
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(result);
                status = obj.getString("scucces");
                message= obj.getString("message");
                test.setText(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }*/
            test.setText(result);
          AlertDialog alertDialog = new AlertDialog.Builder(SendActivity.this).create();
            alertDialog.setTitle("Status");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
}
