package edu.pe.pucp.purpleperu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import javax.xml.transform.Result;


public class LoginActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        firebaseDatabase = FirebaseDatabase.getInstance();




    }

  /*  private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(), result -> {
                onSignInResult(result);
            }
    );


   */
   /* private void onSignInResult(FirebaseAuthUIActivityResult result) {
        IdpResponse response = result.getIdpResponse();
        if(result.getResultCode()== RESULT_OK){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d("msg","Firebase uid: "+ user.getUid());
        }else{
            Log.d("msg", "Cancelo log-in");
        }
    }


    */


    /*Intent sigInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            ))
            .build();
                signInLauncher.launch(sigInIntent);

     */



}