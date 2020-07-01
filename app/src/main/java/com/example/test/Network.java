package com.example.test;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

//This class was more or less taken from the android's Volley setup
public class Network {
    private static Network instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    public static String apiKey = "haTdoUWVhnXm5n75u6d0VG67vCCvKjQC";
    public static String url = "https://dev.mobile-api.woolworths.com.au/wow/v1/dpwallet/";
    public static String merchentId = "10001";

    private Network(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized Network getInstance(Context context) {
        if (instance == null) {
            instance = new Network(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
