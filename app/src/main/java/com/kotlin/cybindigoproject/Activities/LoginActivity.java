package com.kotlin.cybindigoproject.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kotlin.cybindigoproject.R;
import com.kotlin.cybindigoproject.Utils.Global;
import com.kotlin.cybindigoproject.Utils.SharedPreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
EditText email,password;
    Button submit;
    String strEmail,strPassword;
    ProgressDialog progressDialog;
    Context context;
    SharedPreferenceUtils preferances;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login Scree");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        preferances = SharedPreferenceUtils.getInstance(this);
        context = LoginActivity.this;
        progressDialog = new ProgressDialog(context);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        submit=findViewById(R.id.submit);
        ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECEIVE_SMS}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail=email.getText().toString().trim();
                strPassword=password.getText().toString().trim();
                if(strEmail.equalsIgnoreCase("")){
                    email.setError("Please enter your email");
                    email.requestFocus();
                }else if(strPassword.equalsIgnoreCase("")){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }else if(!Global.isInternetAvailable(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this,R.string.check_internetConnection,Toast.LENGTH_LONG).show();
                }  else{
                    mobileVerification();
                }
            }
        });
    }
    private void mobileVerification() {
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        progressDialog.setIndeterminateDrawable(drawable);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", strEmail);
            jsonObject.put("password", strPassword);
        } catch (JSONException e) {

            e.printStackTrace();
        }
        final String mRequestBody = jsonObject.toString();
        Log.i("req_login", mRequestBody);
        StringRequest sr = new StringRequest(Request.Method.POST, Global.API_URL+"login" , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("res_login", response);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();

                    preferances.setValue("USER_ID",jsonObject.getString("token"));
                        Intent intent = new Intent(context, MainActivity.class);
                      startActivity(intent);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context,"Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return mRequestBody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }


        };
        sr.setRetryPolicy(Global.defaultRetryPolicy);
        requestQueue.add(sr);
    }


    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}