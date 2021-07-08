package com.example.shopmate_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String register_uri = "http://192.168.43.102/shopmate_v1/register.php";

    ImageButton cancel_button;
    EditText name_edit_text, email_edit_text, password_edit_text;
    Button signup_button;
    TextView link_to_login;
    LinearLayout email_error_msg_layout;

    boolean emailExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cancel_button = findViewById(R.id.cancel_button);
        name_edit_text = findViewById(R.id.name);
        email_edit_text = findViewById(R.id.email);
        password_edit_text = findViewById(R.id.password);
        signup_button = findViewById(R.id.btnSignup);
        link_to_login = findViewById(R.id.btnLinkToLoginScreen);
        email_error_msg_layout = findViewById(R.id.email_error_msg_layout);

        disableSignUpButton();


        name_edit_text.addTextChangedListener(watcher);
        email_edit_text.addTextChangedListener(watcher);
        password_edit_text.addTextChangedListener(watcher);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.super.onBackPressed();
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_edit_text.getText().toString().trim();
                String email = email_edit_text.getText().toString().trim();
                String password = password_edit_text.getText().toString().trim();
                String token = SharedPrefManager.getInstans(getApplicationContext()).getFCMtoken();

                hideKeyboard(RegisterActivity.this);
                signup(name, email, password,token);

            }
        });
        link_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void disableSignUpButton(){
        signup_button.setEnabled(false);
        signup_button.setAlpha(0.5f);
    }

    private void enableSignUpButton(){
        signup_button.setEnabled(true);
        signup_button.setAlpha(1f);
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
            if (name_edit_text.getText().toString().trim().length() == 0 ||
                    email_edit_text.getText().toString().trim().length() == 0 ||
                    password_edit_text.getText().toString().trim().length() == 0) {
                disableSignUpButton();
            } else {
                if(emailExist){
                    enableSignUpButton();
                }

            }
        }

        boolean isEmailValid(CharSequence email) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches();
        }
    };

    private void signup( String name, String email, String password, String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, register_uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject(response);
                            boolean error = object.getBoolean("error");

                            if (!error) {
                                JSONObject user = object.getJSONObject("user");
                                Toast.makeText(getApplicationContext(), "Signed up successful!", Toast.LENGTH_LONG).show();

                                //Launch profile activity
                                SharedPrefManager.getInstans(getApplicationContext()).userLogin(object.getString("uid"),
                                        user.getString("name"), user.getString("email"));
                                Intent intent = new Intent(RegisterActivity.this, ProfilePageActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String errorMsg = object.getString("error_msg");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("token",token);

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