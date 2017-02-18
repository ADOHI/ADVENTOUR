package com.example.adohi.adventour;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.adohi.adventour.db.User;
import com.example.adohi.adventour.service.LocationService;
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
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    public static NMapLocationManager mMapLocationManager;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;

    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 9001;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    @BindView(R.id.iv_title)ImageView titleImageView;
    @OnClick(R.id.iv_title)void click() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.hold);
        finish();
        Intent locationIntent = new Intent("location");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(StartActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(StartActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET
                , Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE)
                .check();

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        findViewById(R.id.sign_out_button).setOnClickListener(this);

        findViewById(R.id.disconnect_button).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                      FirebaseUser user = firebaseAuth.getCurrentUser();
                      if (user != null) {
                            // User is signed in
                          Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                          User newUser = new User(user.getDisplayName());
                          mDatabase.child("users").child(user.getUid()).setValue(newUser);
                          Intent intent = new Intent(getApplicationContext(), LocationService.class);
                          startService(intent);
                          IntentFilter intentFilter = new IntentFilter();
                          intentFilter.addAction("loaction");
                          Intent activityStartIntent = new Intent(getApplicationContext(), MainActivity.class);
                          activityStartIntent.putExtra("y", 37);
                          activityStartIntent.putExtra("x", 121);
                          startActivity(activityStartIntent);
                          overridePendingTransition(R.anim.push_left_in, R.anim.hold);
                          finish();
                          /*BroadcastReceiver receiver = new BroadcastReceiver() {
                              @Override
                              public void onReceive(Context context, Intent intent) {
                                  Log.d("whyyyyy", "dhoooo");
                                  Intent activityStartIntent = new Intent(context, MapsActivity.class);
                                  activityStartIntent.putExtra("y", intent.getExtras().getDouble("y"));
                                  activityStartIntent.putExtra("x", intent.getExtras().getDouble("x"));
                                  context.startActivity(activityStartIntent);
                                  overridePendingTransition(R.anim.push_left_in, R.anim.hold);
                                  finish();
                              }

                          };

                          registerReceiver(receiver,intentFilter);*/
                          } else {
                            // User is signed out
                            Log.d(TAG, "onAuthStateChanged:signed_out");
                          }
                      // ...
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

    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {

            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"

            // and the GoogleSignInResult will be available instantly.

            Log.d(TAG, "Got cached sign-in");

            GoogleSignInResult result = opr.get();

            handleSignInResult(result);

        } else {

            // If the user has not previously signed in on this device or the sign-in has expired,

            // this asynchronous branch will attempt to sign in the user silently.  Cross-device

            // single sign-on will occur in this branch.

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

        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

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

    // [END handleSignInResult]



    // [START signIn]

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    // [END signIn]



    // [START signOut]

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

    // [END signOut]



    // [START revokeAccess]

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

    // [END revokeAccess]



    @Override

    public void onConnectionFailed(ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not

        // be available.

        Log.d(TAG, "onConnectionFailed:" + connectionResult);

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

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);

            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

        } else {

            //mStatusTextView.setText(R.string.signed_out);



            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);

            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);

        }

    }



    @Override

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sign_in_button:

                signIn();

                break;

            case R.id.sign_out_button:

                signOut();

                break;

            case R.id.disconnect_button:

                revokeAccess();

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
            Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                  Log.w(TAG, "signInWithCredential", task.getException());
                                  Toast.makeText(StartActivity.this, "Authentication failed.",
                                              Toast.LENGTH_SHORT).show();
                                }
                            // ...
                          }
                });
    }



}

