package letrungson.com.smartcontroller.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import letrungson.com.smartcontroller.R;

public class AccountActivity extends Activity {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        login();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    // [END on_start_check_user]

    private void createAccount() {
        setContentView(R.layout.activity_register);
        TextView inputEmail, inputPassword, inputPasswordConfirm;
        Button btnLogin, btnSignup;
        // No user is signed in

        inputEmail = findViewById(R.id.email_edt_text);
        inputPassword = findViewById(R.id.pass_edt_text);
        inputPasswordConfirm = findViewById(R.id.pass_edt_text_confirm);
        btnLogin = findViewById(R.id.login_btn);
        btnSignup = findViewById(R.id.signup_btn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                final String passwordConfirm = inputPasswordConfirm.getText().toString();

                // [START create_user_with_email]
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AccountActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                                Toast.makeText(AccountActivity.this, getString(R.string.error_field_required), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AccountActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                updateUI(null);
                            }
                            Log.w(TAG, "signUpWithEmail:failure", task.getException());
                        } else {
                            startActivity(new Intent(AccountActivity.this, MainActivity.class));
                            Log.d(TAG, "signUpWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            finish();
                        }
                    }
                });
                // [END create_user_with_email]
            }
        });
    }

    private void login() {
        setContentView(R.layout.activity_login);
        TextView inputEmail, inputPassword;
        Button btnLogin, btnSignup;
        // No user is signed in

        inputEmail = findViewById(R.id.email_edt_text);
        inputPassword = findViewById(R.id.pass_edt_text);
        btnLogin = findViewById(R.id.login_btn);
        btnSignup = findViewById(R.id.signup_btn);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                // [START sign_in_with_email]
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AccountActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(AccountActivity.this, getString(R.string.error_field_required), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AccountActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                updateUI(null);
                            }
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        } else {
                            startActivity(new Intent(AccountActivity.this, MainActivity.class));
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            finish();
                        }
                    }
                });
                // [END sign_in_with_email]
            }
        });
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }
}