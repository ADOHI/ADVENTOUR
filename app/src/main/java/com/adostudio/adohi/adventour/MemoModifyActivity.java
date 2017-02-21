package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.adostudio.adohi.adventour.db.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemoModifyActivity extends AppCompatActivity {

    private String uid;
    private DatabaseReference mDatabase;
    @BindView(R.id.et_save_memo)EditText saveMemoEditText;
    @BindView(R.id.bt_save_memo)Button saveMemoButton;
    @OnClick(R.id.bt_save_memo)void saveMemoClick() {

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        user.memo = saveMemoEditText.getText().toString();
                        mDatabase.child("users").child(uid).child("memo").setValue(user.memo);
                        finish();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_modify);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(!(intent.getExtras().getString("memo") == "")){
            saveMemoEditText.setText(intent.getExtras().getString("memo"));
        }
        uid = intent.getExtras().getString("uid");
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
}
