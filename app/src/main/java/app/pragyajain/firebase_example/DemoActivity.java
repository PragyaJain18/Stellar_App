package app.pragyajain.firebase_example;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class User {
    String username;
    String email;
    String password;
    String publicKey;
    String privateKey;

    User() {
    }

    ;

    //    private String gets(String username){
//        this.username = username;
//        return this.username;
//    }
    User(String username, String email, String password, String publicKey, String privateKey) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.publicKey = publicKey;
        this.privateKey = privateKey;

    }
}


public class DemoActivity extends AppCompatActivity {

    private ListView mLvData;
    private ArrayList<User> mListData = new ArrayList<>();
    private DatabaseReference myRef;
    private final String TAG = DemoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mLvData = (ListView) findViewById(R.id.list);
        myRef = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(DemoActivity.this, "error doesnot happen here ", Toast.LENGTH_SHORT).show();


        writeNewUser(SystemClock.currentThreadTimeMillis() + "", "Siddhant8", "siddhant@gmail.com98", "siddhan8t", "dfuagfla789tbvluybvlyuav", "hjvfsvcsl7897uvsbvlshbv");

    }

    private void writeNewUser(String userId, String name, String email, String password, String publicKey, String privateKey) {
        User user = new User(name, email, password, publicKey, privateKey);
//        showProgressDialog();
        myRef.child("users").child(userId).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                hideProgressDialog();
                if (databaseError != null)
                    Toast.makeText(DemoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(DemoActivity.this, "Insert successfully", Toast.LENGTH_SHORT).show();
                    getDatFromDataBase();
                }
            }
        });

    }


    public class ListAdapter extends BaseAdapter {

        List<User> mList;
        Context mContext;
        ListAdapter(Context context, List<User> objects) {
            mContext = context;
            mList = objects;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, null


            );

            TextView mName = view.findViewById(R.id.name);
            TextView mEmail = view.findViewById(R.id.email);
            TextView mPassword = view.findViewById(R.id.password);
            TextView mPublickey = view.findViewById(R.id.publickey);
            TextView mPreivateKey = view.findViewById(R.id.privatekey);


            mName.setText(mList.get(position).username);
            mEmail.setText(mList.get(position).email);
            mPassword.setText(mList.get(position).password);
            mPublickey.setText(mList.get(position).publicKey);
            mPreivateKey.setText(mList.get(position).privateKey);

            return view;
        }
    }

    public void getDatFromDataBase() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }

                List<User> mUserList = new ArrayList<>();
                for (DataSnapshot mDataSnapshot : dataSnapshot.child("users").getChildren()) {
                    mUserList.add(mDataSnapshot.getValue(User.class));
                }

//                Collections.reverse(mListData);
                mLvData.setAdapter(new ListAdapter(DemoActivity.this,  mUserList));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

}


