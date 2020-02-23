package com.ohuseynov.app.facematch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ohuseynov.app.facematch.utils.myUtils;


public class MainActivity extends AppCompatActivity {

    SignInButton signInButton,signInButton2;
    public GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private static final int RC_SIGN_IN = 9001;
    private Button intent,r_reg,LoginB;
    private EditText r_e,r_p,r_nn,r_srn,l_pass,l_email;
    private LinearLayout s_login,s_reg;
    private TabLayout tbli;
    private TextView err_l,err_r;
    InputMethodManager inputManager;
    myUtils myU;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        getSupportActionBar().setElevation(0);
        init(); // init all ui staff
//        firebase auth
        mAuth = FirebaseAuth.getInstance();
        //google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("480791607419-lf4etfamp5avnjrq86md4gul8hgv73e7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Listenrss();

    }

    private void signInWithEmail(String email, String password, final String displayName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest update=
                                    new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                            user.updateProfile(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        UpdateUI(user);
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            err_r.setText(task.getException().getMessage());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                err_r.setText(e.getMessage());
            }
        });
    }

    private void init() {
        signInButton = (SignInButton) findViewById(R.id.Gsign_in_b);
        r_reg=(Button) findViewById(R.id.r_reg);
        r_e=(EditText) findViewById(R.id.r_email);
        r_p=(EditText) findViewById(R.id.r_pass);
        r_nn=(EditText) findViewById(R.id.r_nn);
        r_srn=(EditText) findViewById(R.id.r_srn);
        s_login=(LinearLayout) findViewById(R.id.s_login);
        s_reg=(LinearLayout) findViewById(R.id.s_reg);
        tbli=(TabLayout) findViewById(R.id.s_tabl);
        LoginB=(Button) findViewById(R.id.s_loginB);
        l_email=(EditText) findViewById(R.id.l_email);
        l_pass=(EditText) findViewById(R.id.l_pass);
        err_l=(TextView) findViewById(R.id.err_l);
        err_r=(TextView) findViewById(R.id.err_r);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        myU=new myUtils();

    }

    private void Listenrss(){

        LoginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKey();
                if(isEmptyE(l_email)){
                    l_email.setError("Email can not be empty");
                }else if(isEmptyE(l_pass)){
                    l_pass.setError("Password can not be empty");
                }else {
                    progressDialog= new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Wait a second");
                    progressDialog.show();
                    SininWithEmail(l_email.getText().toString(), l_pass.getText().toString());
                }
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKey();
                SignIn(v);
            }
        });

        r_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKey();

                if (isEmptyE(r_nn)) {
                    r_nn.setError("Name can not be empty");
                } else if (isEmptyE(r_srn)) {
                    r_srn.setError("Surname can not be empty");
                }else if (isEmptyE(r_e)) {
                    r_e.setError("Email can not be empty");
                }else if (isEmptyE(r_p)) {
                    r_p.setError("Password can not be empty");
                }else{

                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Wait a second ...");
                    progressDialog.show();
                    signInWithEmail(myU.cleanString(r_e.getText().toString()), myU.cleanString(r_p.getText().toString()), myU.cleanString(r_nn.getText().toString() + " " + r_srn.getText().toString()));
                }
            }
        });

        tbli.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    s_login.setVisibility(View.VISIBLE);
                    s_reg.setVisibility(View.GONE);

                }else if(tab.getPosition()==1){
                    s_login.setVisibility(View.GONE);
                    s_reg.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void SininWithEmail(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    FirebaseUser user=mAuth.getCurrentUser();
                    UpdateUI(user);
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                err_l.setText(e.getMessage());
                err_l.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            UpdateUI(currentUser);
        }
    }


    private void SignIn(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
            progressDialog.setTitle("Wait a second");
            progressDialog.setMessage("Logging in...");
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            err_r.setText("Authentication  failed : "+result.getStatus().getStatusMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "signInWithCredential:success");
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            UpdateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.;
                            err_r.setText(task.getException().getMessage());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                err_r.setText(e.getMessage());
            }
        });

    }

    private void UpdateUI(FirebaseUser account) {
        Intent change = new Intent(MainActivity.this, FaceMatch.class);
        startActivity(change);
    }

    private void ToastSuccess(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void SignOUt() {
        Intent change2 = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(change2);
    }

    private boolean isEmptyE(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    private void closeKey(){
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
