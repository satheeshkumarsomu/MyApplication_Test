package com.example.admin.myapplication;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * @author Satheeshkumar
 **/
public class VolleyHttpRequest extends Request<String> {

    private static String TAG = "Request";
    private AsyncTaskCompleteListener listener;
    private Map<String, String> params;
    private int serviceCode;

    public VolleyHttpRequest(int method, Map<String, String> params,
                             int serviceCode, AsyncTaskCompleteListener reponseListener,
                             ErrorListener errorListener) {
        super(method, params.get("url"), errorListener);

        Log.d("ttt", "api=" + params.get("url"));
        params.remove("url");
        setRetryPolicy(new DefaultRetryPolicy(600000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.listener = reponseListener;
        this.params = params;
        this.serviceCode = serviceCode;
    }

    @Override
    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        Log.d("WebService", "Request=" + params);
        return params;
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onTaskCompleted(response, serviceCode);
        Log.d("WebService", "API=" + response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(jsonString, getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
