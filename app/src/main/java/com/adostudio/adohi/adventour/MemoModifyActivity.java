package com.adostudio.adohi.adventour;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.adostudio.adohi.adventour.appInit.MyApplication;
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

    private static final String LOGTAG = "MemoModifyActivity";

    private DatabaseReference appDatabase;
    @BindView(R.id.et_save_memo)EditText saveMemoEditText;
    @BindView(R.id.bt_save_memo)Button saveMemoButton;
    @OnClick(R.id.bt_save_memo)void saveMemoClick() {

        appDatabase.child("users").child(MyApplication.getMyUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        user.setMemo(saveMemoEditText.getText().toString());
                        appDatabase.child("users").child(MyApplication.getMyUid()).setValue(user);
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
        appDatabase = FirebaseDatabase.getInstance().getReference();

        ActionBar ab = getSupportActionBar();
        ab.setTitle("메모 수정");
    }
}
