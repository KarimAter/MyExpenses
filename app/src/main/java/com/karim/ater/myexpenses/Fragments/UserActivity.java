package com.karim.ater.myexpenses.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karim.ater.myexpenses.AppController;
import com.karim.ater.myexpenses.Helpers.MySharedPrefs;
import com.karim.ater.myexpenses.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ater on 8/5/2018.
 */

public class UserActivity extends AppCompatActivity {

    Spinner profileSpinner;
    Spinner accountsSpinner;
    Button profileBu, profileSignInBu;
    private static final int RC_SIGN_IN = 444;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        profileSpinner = findViewById(R.id.profileSpinner);
        accountsSpinner = findViewById(R.id.accountSpinner);

        profileSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                AppController.getProfiles()));
        accountsSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                AppController.getAccounts()));

        // saving activity_user & account to AppController
        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppController.setCurrentProfile(AppController.getProfiles().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        accountsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppController.setCurrentAccount(AppController.getAccounts().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        profileBu = findViewById(R.id.profileBu);
        profileSignInBu = findViewById(R.id.profileSiginInBu);

        profileSignInBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAndOut();
            }
        });
        profileBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MySharedPrefs.getAuthenticationStatus(this))
            profileSignInBu.setText("Sign Out");
        else profileSignInBu.setText("Sign In");
    }

    private void signInAndOut() {
//        Intent signInIntent = new Intent(this, Authentication.class);
        if (profileSignInBu.getText().toString().equalsIgnoreCase("Sign in")) {
//            startActivity(signInIntent);
            createSignInIntent();

        } else {
            signOut();
        }
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        MySharedPrefs.setAuthenticationStatus(UserActivity.this, false);
                        profileSignInBu.setText("Sign In");
                    }
                });
        // [END auth_fui_signout]
    }

    private void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                String userId = null;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                    userId = user.getUid();
                MySharedPrefs.setUserId(this, userId);
                MySharedPrefs.setAuthenticationStatus(this, true);
//                finish();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                if (response != null)
                    Toast.makeText(this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    // [END auth_fui_result]
    public void delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_delete]
    }

    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.common_google_signin_btn_icon_dark)      // Set logo drawable
                        .setTheme(R.style.Theme_AppCompat_DayNight_Dialog)      // Set theme
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        // [START auth_fui_pp_tos]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_pp_tos]
    }
}
