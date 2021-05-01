package letrungson.com.smartcontroller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import letrungson.com.smartcontroller.R;
import letrungson.com.smartcontroller.RoomViewAdapter;

public class MoreActivity extends Activity {
    private FirebaseAuth mAuth;

    TextView welcome;
    Button logout, changePassword;
    ImageButton homeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        welcome = findViewById(R.id.welcome);
        homeButton = findViewById(R.id.home_btn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this, MainActivity.class));
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        welcome.setText("Welcome "+ user.getEmail());

        logout = findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MoreActivity.this, AccountActivity.class));
                finish();
            }
        });

        changePassword = findViewById(R.id.btn_changePassword);
        changePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this, AccountActivity.class));
            }
        });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        }
    }
    // [END on_start_check_user]

}
