package cz.muni.fi.a2p06.stolencardatabase.utils;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class FirebaseRestClient {

    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/";
    private static final String TAG = FirebaseRestClient.class.getName();
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(Context context, String url, HttpEntity params, String type, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), params, type, responseHandler);
    }

    public static void sendMessageToTopic(Context context, String topic, String text) throws UnsupportedEncodingException, JSONException {
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("to", "/topics/" + topic);
        JSONObject data = new JSONObject();
        data.put("text", text);
        jsonParams.put("notification", data);
        client.addHeader("Authorization", "key=AAAAuj0s-iw:APA91bHr4VDubDtypi9XydtWgTtCJql2JiNS4iOIprx_uDe1fZyVTu0lztoKwBrJdKeej75TQhJ7dR5sARsLsUqp_WVzWubJMUSFXrmviJJma4WPN289hCIm56yKEPmtTPERUezv8hAu");
        client.addHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(jsonParams.toString());

        FirebaseRestClient.post(context, "send", entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, throwable.getMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, throwable.getMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString);
            }

        });
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
