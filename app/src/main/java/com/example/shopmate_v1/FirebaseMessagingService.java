package com.example.shopmate_v1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String updateFCMtoken_uri = "http://192.168.43.102/shopmate_v1/updateFCMtoken.php";

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        SharedPrefManager.getInstans(getApplicationContext()).saveFCMtoken(token);
        if(SharedPrefManager.getInstans(getApplicationContext()).isLogin()){
            updateFCMtoken();
        }

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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, PriceTrackerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "priceDrop_fcm")
                .setSmallIcon(R.drawable.ic_push_notification)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());


        //showNotification(remoteMessage.getData().get("message"));
    }

    private void showNotification(String message) {

//        Intent i = new Intent(this,ProfilePageActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"priceDrop_fcm")
                .setAutoCancel(true)
                .setContentTitle("Price Drop Alerts!")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_push_notification);


        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        manager.notify(0,builder.build());
    }

}
