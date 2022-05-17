package com.project.mbqs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.project.mbqs.utils.*;
import com.project.mbqs.host.*;
import com.project.mbqs.join.*;

import static com.project.mbqs.MyVariables.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private static final int RC_SIGN_IN = 1;
    private TextView tv_status;
    private Button b_host, b_join;
    private FirebaseAuth auth;
    private Intent intent;
    private Toolbar toolbar;

    private void initilizeSharedPrefs(){
        if(null==sharedPreferences) {
            Log.i(TAG,"Initializing Shared Preferences..");
            sharedPreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);
        toolbar.setLogo(R.mipmap.queue_round);
        setSupportActionBar(toolbar);
        initilizeSharedPrefs();
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        tv_status = (TextView) findViewById(R.id.tv_status);

        auth = FirebaseAuth.getInstance();

        b_host = (Button) findViewById(R.id.b_host);
        b_join = (Button) findViewById(R.id.b_join);

        b_host.setOnClickListener(this);
        b_join.setOnClickListener(this);

    }

    private void signIn(){

    }


    //Sign-out Code
    private void signOut(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG,"Signing Off...");
                editor.putString(MyVariables.MYEMAIL,null);
                editor.putString(HOSTQID,null);
                editor.putString(MyVariables.MYUSERNAME,null);
                editor.commit();
                startActivity(intent);
                finish();
            }
        });

    }

    public void setSignInLauncher() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // [START auth_fui_theme_logo]
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.queue_round)      // Set logo drawable
                .setTheme(R.style.Theme_MBQS)      // Set theme
                .build();

        //startActivity(signInIntent);
        signInLauncher.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                onSignInResult(result);
            }
    );

    // [START auth_fui_result]
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            /*
            auth.signInWithEmailAndPassword(new OnCompleteListener<>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){

                    }
                }
            });

             */
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            response.getError().getErrorCode();
        }
    }

    //Hosting code
    private void host(){
        Intent intent = new Intent(this, HostingDetailsActivity.class);
        startActivity(intent);
    }

    //Join code
    private void join(){
        Intent intent = new Intent(this, ScanTabsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(auth.getCurrentUser() != null){
            //Already Signed In
            editor.putString(HOSTQID,MyUtilsOperation.encodeEmail(auth.getCurrentUser().getEmail()));
            editor.putString(MYEMAIL,auth.getCurrentUser().getEmail());
            editor.putString(MYUSERNAME,auth.getCurrentUser().getDisplayName());
            editor.commit();
            tv_status.setText(getResources().getText(R.string.welcome) + auth.getCurrentUser().getDisplayName());
        }
        else{
            //Yet to sign in
            // Choose authentication providers
            signIn();

        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.b_host : host();  break;

            case R.id.b_join : join();  break;

            default://Nothing
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK){
                Toast.makeText(this,"Login email : " + auth.getCurrentUser().getEmail(),Toast.LENGTH_SHORT).show();
                editor.putString(HOSTQID, MyUtilsOperation.encodeEmail(auth.getCurrentUser().getEmail()));
                editor.putString(MyVariables.MYEMAIL,auth.getCurrentUser().getEmail());
                editor.putString(MyVariables.MYUSERNAME,auth.getCurrentUser().getDisplayName());
                editor.commit();
                Log.d(TAG,"QID : "+auth.getCurrentUser().getEmail().replaceAll("[^a-z0-9]", ""));
                tv_status.setText(getResources().getText(R.string.welcome) + auth.getCurrentUser().getDisplayName());
            }else{
                Log.d(TAG,"User Not Authenticated");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_signOut :
                signOut();
                return true;
            case R.id.action_AboutUs :
                MyUtilsOperation.showAboutUsDialog(this);
                return true;
        }

        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"No Network !\nPlease Check Your Internet Connection", Toast.LENGTH_LONG).show();
    }

}
