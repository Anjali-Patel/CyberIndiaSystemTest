package com.kotlin.cybindigoproject.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kotlin.cybindigoproject.Adapter.UserListAdapter;
import com.kotlin.cybindigoproject.Model.UserModel;
import com.kotlin.cybindigoproject.R;
import com.kotlin.cybindigoproject.Utils.Global;
import com.kotlin.cybindigoproject.Utils.SharedPreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
ImageView current_location,logout;
    Dialog dialog;
    SharedPreferenceUtils preferances;
    RecyclerView userList;

    String member_id;
EditText input_search;
    private List<UserModel> UserModelList;
    ArrayList<UserModel> UserModelListTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input_search=findViewById(R.id.input_search);
        preferances = SharedPreferenceUtils.getInstance(this);
        userList=findViewById(R.id.userList);

        UserModelList = new ArrayList<>();

        member_id= preferances.getStringValue("MEMBER_ID","");

        if(!Global.isInternetAvailable(Objects.requireNonNull(this))){
            Toast.makeText(getApplicationContext(),R.string.check_internetConnection,Toast.LENGTH_LONG).show();
        }else{
            getUserList();
        }
        current_location=findViewById(R.id.current_location);
        logout=findViewById(R.id.logout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });
        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,CurrentLocationActivity.class);
                startActivity(intent);
            }
        });

        UserModelListTemp= new ArrayList<>();
        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    UserModelListTemp.clear();
                    for (int i = 0; i < UserModelList.size(); i++) {
                        if (UserModelList.get(i).getName().toLowerCase().startsWith(strText.toLowerCase())||UserModelList.get(i).getEmail().toLowerCase().startsWith(strText.toLowerCase())) {
                            UserModelListTemp.add(UserModelList.get(i));
                        }
                    }
                    UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(), UserModelListTemp);
                    userList.setAdapter(userListAdapter);
                    userListAdapter.notifyDataSetChanged();
                } else {
                    UserListAdapter userListAdapter = new  UserListAdapter(getApplicationContext(), UserModelList);
                    userList.setAdapter(userListAdapter);
                    userListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strText = s.toString().trim();
                if (strText.length() != 0) {
                    UserModelListTemp.clear();
                    for (int i = 0; i < UserModelList.size(); i++) {
                        if (UserModelList.get(i).getName().toLowerCase().startsWith(strText.toLowerCase())||UserModelList.get(i).getEmail().toLowerCase().startsWith(strText.toLowerCase())) {
                            UserModelListTemp.add(UserModelList.get(i));
                        }
                    }
                    UserListAdapter    UserListAdapter  = new UserListAdapter(getApplicationContext(), UserModelListTemp);
                    userList.setAdapter(UserListAdapter);
                    UserListAdapter.notifyDataSetChanged();
                } else {
                    UserListAdapter           UserListAdapter = new UserListAdapter(getApplicationContext(), UserModelList);
                    userList.setAdapter(UserListAdapter);
                    UserListAdapter.notifyDataSetChanged();
                }

            }
        });
    }


    private void logoutDialog() {
        dialog = new Dialog(MainActivity.this, R.style.AlertDialogCustom);
        dialog.setContentView(R.layout.custom_alert);
        dialog.setCancelable(false);
        TextView tv1 = dialog.findViewById(R.id.tv1);
        tv1.setTextSize(12f);
        Button b1 = dialog.findViewById(R.id.b1);
        b1.setText(getString(R.string.yes));
        Button b2 = dialog.findViewById(R.id.b2);
        b2.setText(getString(R.string.no));
        tv1.setText(R.string.want_logout);
        b1.setOnClickListener(v -> {
            if (!preferances.getStringValue("USER_ID","").equalsIgnoreCase("")) {
                preferances.setValue("USER_ID", "");
                Intent normalIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(normalIntent);
                dialog.dismiss();
            }
        });
        b2.setOnClickListener(v -> {
            dialog.dismiss();
        });
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserList() {
        UserModelList.clear();
//        progressBarHolder.setVisibility(View.VISIBLE);
        String url = Global.API_URL+"users?page=2";
        StringRequest jsonRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressBarHolder.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject1= jsonArray.getJSONObject(i);
                                UserModel userModel= new UserModel();
                                    userModel.setName(jsonObject1.getString("first_name")+ jsonObject1.getString("last_name"));
                                    userModel.setEmail(jsonObject1.getString("email"));
                                    userModel.setPicture(jsonObject1.getString("avatar"));

                                    UserModelList.add(userModel);


                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            userList.setLayoutManager(linearLayoutManager);
                            UserListAdapter userListAdapter = new UserListAdapter(getApplicationContext(),UserModelList);

                            userList.setAdapter(userListAdapter);
                            userListAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            progressBarHolder.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressBarHolder.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "session does not exist.", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonRequest);
    }

}