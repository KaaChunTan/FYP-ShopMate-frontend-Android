package com.example.shopmate_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfilePageActivity extends AppCompatActivity {

    LinearLayout profile_info_layout, profile_error_layout;
    TextView name_text_view, email_text_view;
    Button logout_button, link_to_login_screen;
    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        profile_error_layout = findViewById(R.id.profile_error_layout);
        profile_info_layout = findViewById(R.id.profile_info_layout);
        name_text_view = findViewById(R.id.name_textView);
        email_text_view = findViewById(R.id.email_textView);
        logout_button = findViewById(R.id.btnLogout);
        link_to_login_screen = findViewById(R.id.btnLinkToLoginScreen);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        //when it is logged in
        if(SharedPrefManager.getInstans(getApplicationContext()).isLogin()){
            profile_info_layout.setVisibility(View.VISIBLE);
            profile_error_layout.setVisibility(View.GONE);
            name_text_view.setText(SharedPrefManager.getInstans(getApplicationContext()).getUsername());
            email_text_view.setText(SharedPrefManager.getInstans(getApplicationContext()).getUserEmail());
        }
        else{
            profile_info_layout.setVisibility(View.GONE);
            profile_error_layout.setVisibility(View.VISIBLE);

        }

        getFCMtoken();

        //set home selected
        bottom_navigation.setSelectedItemId(R.id.profile);

        //perform itemSelectedListener
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeInterface.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.compare_list:
                        startActivity(new Intent(getApplicationContext(), CompareListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.price_tracker:
                        startActivity(new Intent(getApplicationContext(),PriceTrackerActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstans(getApplicationContext()).logout();

                finish();
                startActivity(getIntent());
            }
        });

        link_to_login_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePageActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

    }
    private void getFCMtoken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM Fetching", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        SharedPrefManager.getInstans(getApplicationContext()).saveFCMtoken(token);
                        Log.e("fcm token string: ", token);
                    }
                });
    }
}