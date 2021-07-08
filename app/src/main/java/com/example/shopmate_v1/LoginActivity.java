package com.example.shopmate_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String login_uri = "http://192.168.43.102/shopmate_v1/login.php";
    private static final String updateFCMtoken_uri = "http://192.168.43.102/shopmate_v1/updateFCMtoken.php";

    ImageButton cancel_button;
    EditText email_edit_text, password_edit_text;
    Button login_button;
    TextView link_to_register;
    LinearLayout email_error_msg_layout;

    boolean emailExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cancel_button = findViewById(R.id.cancel_button);
        email_edit_text = findViewById(R.id.email);
        password_edit_text = findViewById(R.id.password);
        login_button = findViewById(R.id.btnLogin);
        link_to_register = findViewById(R.id.btnLinkToRegisterScreen);
        email_error_msg_layout = findViewById(R.id.email_error_msg_layout);

        disableLoginButton();

        email_edit_text.addTextChangedListener(watcher);
        password_edit_text.addTextChangedListener(watcher);

        if (SharedPrefManager.getInstans(this).isLogin()){
            finish();
            Intent intent = new Intent(LoginActivity.this,ProfilePageActivity.class);
            startActivity(intent);
        }

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.super.onBackPressed();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = email_edit_text.getText().toString().trim();
                String password = password_edit_text.getText().toString().trim();
                hideKeyboard(LoginActivity.this);
                login(email,password);
           }
        });

        link_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");

                    if (!error) {
                        JSONObject user = object.getJSONObject("user");
                        SharedPrefManager.getInstans(getApplicationContext()).userLogin(object.getString("uid"),
                                user.getString("name"), user.getString("email"));
                        updateFCMtoken(); //get the latest FCM token for the user when they are logged in
                        finish();

                        Intent intent = new Intent(LoginActivity.this, ProfilePageActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "User Login Unsucessful", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "User Login Unsucessful", Toast.LENGTH_LONG).show();

                    email_edit_text.getText().clear();
                    password_edit_text.getText().clear();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void updateFCMtoken() {
        String user_id = SharedPrefManager.getInstans(getApplicationContext()).getUserId();
        String token = SharedPrefManager.getInstans(getApplicationContext()).getFCMtoken();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateFCMtoken_uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");

                    if (!error) {
                        String msg = object.getString("msg");
                        Log.e("FCM token update", msg);
                    } else {
                        String msg = object.getString("error_msg");
                        Log.e("FCM token update", msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void disableLoginButton(){
        login_button.setEnabled(false);
        login_button.setAlpha(0.5f);
    }

    private void enableLoginButton(){
        login_button.setEnabled(true);
        login_button.setAlpha(1f);
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //check if the email is valid
            if(email_edit_text.getText().toString().trim().length() != 0&&
                    isEmailValid(email_edit_text.getText().toString().trim())==false){
                //show error message
                email_error_msg_layout.setVisibility(View.VISIBLE);
                emailExist = false;
            }
            else{
                //set invisible error msg
                email_error_msg_layout.setVisibility(View.GONE);
                emailExist = true;
            }
            if (email_edit_text.getText().toString().trim().length() == 0 ||
                    password_edit_text.getText().toString().trim().length() == 0) {
                disableLoginButton();
            } else {
                if(emailExist){
                    enableLoginButton();
                }

            }
        }
        boolean isEmailValid(CharSequence email) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches();
        }
    };

    //hide keyboard after user submitting query at the same page
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}