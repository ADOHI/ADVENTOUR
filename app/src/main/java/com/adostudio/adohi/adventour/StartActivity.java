package com.adostudio.adohi.adventour;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adostudio.adohi.adventour.db.User;
import com.adostudio.adohi.adventour.service.LocationService;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final String LOGTAG = "StartActivity";

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private static final int RC_SIGN_IN = 9001;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @BindView(R.id.iv_login)ImageView loginButton;
    @OnClick(R.id.iv_login)void loginClick() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("loaction");
        Intent activityStartIntent = new Intent(this, MainActivity.class);
        startActivity(activityStartIntent);
        overridePendingTransition(R.anim.push_left_in, R.anim.hold);
    }
    @BindView(R.id.iv_logout)ImageView logoutButton;
    @OnClick(R.id.iv_logout)void logoutClick() {
        signOut();
        revokeAccess();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(StartActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET
                , Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE)
                .check();

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                      FirebaseUser user = firebaseAuth.getCurrentUser();

                     if (user != null) {
                            // User is signed in

                          final String uid = user.getUid();
                          final String name = user.getDisplayName();
                          final String photoUrl = user.getPhotoUrl().toString();
                            Log.d(LOGTAG, name + "  " + user.getDisplayName());
                          Log.d("currentuser", user.getDisplayName());
                          mDatabase.child("users").child(uid).addValueEventListener(
                                  new ValueEventListener() {
                                      @Override
                                      public void onDataChange(DataSnapshot dataSnapshot) {
                                          if(!dataSnapshot.exists()){
                                              User newUser = new User();
                                              newUser.setUid(uid);
                                              newUser.setUserName(name);
                                              newUser.setPhotoUrl(photoUrl);
                                              Log.d(LOGTAG, name + "  " + newUser.getUserName());
                                             mDatabase.child("users").child(uid).setValue(newUser);
                                          }
                                      }
                                      @Override
                                      public void onCancelled(DatabaseError databaseError) {
                                          Log.d(LOGTAG, "database error : " + databaseError);

                                      }
                                  });
                      }
                      else {
                      }

                }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override

    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {

            Log.d(LOGTAG, "Got cached sign-in");

            GoogleSignInResult result = opr.get();

            handleSignInResult(result);

        } else {

            showProgressDialog();

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {

                @Override

                public void onResult(GoogleSignInResult googleSignInResult) {

                    hideProgressDialog();

                    handleSignInResult(googleSignInResult);

                }

            });

        }

    }



    // [START onActivityResult]

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    // [END onActivityResult]



    // [START handleSignInResult]

    private void handleSignInResult(GoogleSignInResult result) {

        Log.d(LOGTAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true);
            firebaseAuthWithGoogle(acct);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
            FirebaseAuth.getInstance().signOut();
        }

    }


    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(

                new ResultCallback<Status>() {

                    @Override

                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }

                });

    }


    private void revokeAccess() {

        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(

                new ResultCallback<Status>() {

                    @Override

                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }

                });

    }

    @Override

    public void onConnectionFailed(ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not

        // be available.

        Log.d(LOGTAG, "onConnectionFailed:" + connectionResult);

    }

    private void showProgressDialog() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();

    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {

        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        }
    }



    @Override

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sign_in_button:
                signIn();

                break;
        }

    }

    @Override
      public void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                  mAuth.removeAuthStateListener(mAuthListener);
            }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            Log.d(LOGTAG, "firebaseAuthWithGoogle:" + acct.getId());

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(LOGTAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                  Log.w(LOGTAG, "signInWithCredential", task.getException());
                                  Toast.makeText(StartActivity.this, "Authentication failed.",
                                              Toast.LENGTH_SHORT).show();
                                }

                          }
                });
    }



}

