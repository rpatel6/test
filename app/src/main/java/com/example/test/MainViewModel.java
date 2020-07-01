package com.example.test;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import androidx.lifecycle.ViewModel;

//I realise I've made this viewmodel messy. Usually, I'd have a class for the action (and call the action from the VM) and write proper pojos instead of having hardcoded objects.
public class MainViewModel extends ViewModel {

    private static String schemaId;

    public String getSchemaId() {
        return schemaId;
    }

    public void setupApi(Context context, final CompletionHandler<String, Void> handler) {
        String setupURLExt = "merchant/schema";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Network.url + setupURLExt, createSchemaJSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                processJSONResponse(response);
                handler.completed(schemaId, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestHeaders();
            }
        };
        Network.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void startTransaction(Context context, final CompletionHandler<Bundle, Void> handler) {
        String transactionURLExt = "merchant/payment/session";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Network.url + transactionURLExt, createPaymentSessionJSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handler.completed(makeBundle(response), null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return requestHeaders();
            }
        };
        Network.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private Map<String, String> requestHeaders() {
        Map<String, String>  header = new HashMap<String, String>();
        header.put("content-type", "application/json");
        header.put("x-api-key", Network.apiKey);
        header.put("x-merchant-id", Network.merchentId);
        return header;
    }

    private void processJSONResponse(JSONObject response) {
        try {
            JSONObject dataJSON = response.getJSONObject("data");
            schemaId = dataJSON.getString("schemaId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bundle makeBundle(JSONObject response) {
        try {
            JSONObject dataJSON = response.getJSONObject("data");
            JSONObject qrJSON = dataJSON.getJSONObject("qr");

            Bundle bundle = new Bundle();
            bundle.putString(TransactionFragment.TransactionKey.CONTENT.name(), qrJSON.getString("content"));
            bundle.putString(TransactionFragment.TransactionKey.IMAGE.name(), qrJSON.getString("image"));
            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject createSchemaJSONObject() {
        try {
            JSONObject dataJSON = new JSONObject();
            dataJSON.put("type", "POS-InStore");
            dataJSON.put("description", "Schema to capture the point of sale information");

            JSONObject storeIdJSON = new JSONObject();
            storeIdJSON.put("type", "string");

            JSONObject laneIdJSON = new JSONObject();
            laneIdJSON.put("type", "string");

            JSONObject rewardsIdJSON = new JSONObject();
            rewardsIdJSON.put("type", "string");

            JSONObject usePointsJSON = new JSONObject();
            usePointsJSON.put("type", "boolean");

            JSONObject propJSON = new JSONObject();
            propJSON.put("storeId", storeIdJSON);
            propJSON.put("laneId", laneIdJSON);
            propJSON.put("rewardsId", rewardsIdJSON);
            propJSON.put("usePoints", usePointsJSON);

            JSONObject schemaJSON = new JSONObject();
            schemaJSON.put("required", new JSONArray());
            schemaJSON.put("type", "object");
            schemaJSON.put("properties", propJSON);

            dataJSON.put("schema", schemaJSON);

            JSONObject metaObject = new JSONObject();

            JSONObject paramObj = new JSONObject();
            paramObj.put("data", dataJSON);
            paramObj.put("meta", metaObject);
            return paramObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject createPaymentSessionJSONObject() {
        try {
            JSONObject payloadJSON = new JSONObject();
            payloadJSON.put("storeId", "12345");
            payloadJSON.put("type", "string");

            JSONObject additionalInfoJSON = new JSONObject();
            additionalInfoJSON.put("schemaId", schemaId);
            additionalInfoJSON.put("payload", payloadJSON);

            JSONObject dataJSON = new JSONObject();
            dataJSON.put("location", "Carrum Downs");
            dataJSON.put("description", "Schema to capture the point of sale information");
            dataJSON.put("additionalInfo", additionalInfoJSON);
            dataJSON.put("generateQR", true);
            dataJSON.put("timeToLiveQR", 60);
            dataJSON.put("timeToLivePaymentSession", 300);

            JSONObject metaObject = new JSONObject();

            JSONObject paramObj = new JSONObject();
            paramObj.put("data", dataJSON);
            paramObj.put("meta", metaObject);
            return paramObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
