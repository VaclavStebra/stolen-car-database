package cz.muni.fi.a2p06.stolencardatabase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            doSignIn();
        } else {
            loginUser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == ResultCodes.OK) {
                loginUser();
                return;
            }
            if (response == null || response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                finish();
                return;
            }
            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void doSignIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                                ))
                        .build(),
                RC_SIGN_IN
        );
    }

    private void loginUser() {
        FirebaseMessaging.getInstance().subscribeToTopic(mAuth.getCurrentUser().getUid());
        finish();
    }
}
