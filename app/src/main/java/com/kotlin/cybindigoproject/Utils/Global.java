package com.kotlin.cybindigoproject.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.DefaultRetryPolicy;

public class Global {
    public static final String API_URL="https://reqres.in/api/";
    public static DefaultRetryPolicy defaultRetryPolicy = new DefaultRetryPolicy(50000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    public static boolean isInternetAvailable(Context context)
    {
        NetworkInfo info = (NetworkInfo)((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info == null)
        {
            return false;
        }
        else
        {
            if(info.isConnected())
            {
                return true;
            }
            else
            {
                return true;
            }

        }
    }
}
