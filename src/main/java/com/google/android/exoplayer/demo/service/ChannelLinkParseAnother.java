package com.google.android.exoplayer.demo.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.google.android.exoplayer.demo.Model.ChannelLink;
import com.google.android.exoplayer.demo.StreamingPlayer;
import com.google.android.exoplayer.demo.interfaces.CallbackHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by raj on 5/25/16.
 */
public class ChannelLinkParseAnother {
    private Context context;
    private int channelId;

    public String sboxLink360;
    public String sboxLink720;
    public String sboxLink180;
    public String sboxLink480;
    public CallbackHandler callBackHandler;
    public static ChannelLink AnotherChannelLink;


    public ChannelLinkParseAnother(Context context, int channelId,CallbackHandler callBackHandler){
        this.channelId = channelId;
        this.context = context;
        this.callBackHandler = callBackHandler;
        String url = "http://www.livestreamer.com/api/channelLink/" + channelId;
        new ChannelLinkParseAnotherAsync().execute(url);
        //init();
    }

    private void init() {
//        String url = "http://www.livestreamer.com/api/channelLink/" + channelId;
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonRootObject = new JSONObject(response);
//                    Boolean success = (boolean)jsonRootObject.get("success");
//                    if(success) {
//                        JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                            int channelId = Integer.parseInt(jsonObject.optString("channelId").toString());
//                            sboxLink180 = jsonObject.optString("sboxLink180").toString();
//                            sboxLink360 = jsonObject.optString("sboxLink360").toString();
//                            sboxLink480 = jsonObject.optString("sboxLink480").toString();
//                            sboxLink720 = jsonObject.optString("sboxLink720").toString();
//                            ChannelLink channelLink = new ChannelLink();
//                            channelLink .setChannelId(channelId);
//                            channelLink .setSboxLink180(sboxLink180);
//                            channelLink .setSboxLink360(sboxLink360);
//                            channelLink .setSboxLink480(sboxLink480);
//                            channelLink .setSboxLink720(sboxLink720);
//                            AnotherChannelLink = channelLink;
//                            StreamingPlayer.channelId = channelId;
//                            callBackHandler.notyfy();
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Log.i("ERROR..", error.toString());
//                if(StreamingPlayer.dialog != null){
//                    StreamingPlayer.dialog.dismiss();
//                }
//            }
//        });
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(context).add(stringRequest);
    }

    public class ChannelLinkParseAnotherAsync extends AsyncTask<String, Void, Void> {

        StringBuffer buffer = null;

        @Override
        protected Void doInBackground(String... params) {

            HttpURLConnection httpURLConnection;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                buffer = new StringBuffer("");
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                System.out.println("buffer.toString****************" + buffer.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONObject jsonRootObject = new JSONObject(buffer.toString());
                Boolean success = (boolean) jsonRootObject.get("success");
                if (success) {
                    JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int channelId = Integer.parseInt(jsonObject.optString("channelId").toString());
                        sboxLink180 = jsonObject.optString("sboxLink180").toString();
                        sboxLink360 = jsonObject.optString("sboxLink360").toString();
                        sboxLink480 = jsonObject.optString("sboxLink480").toString();
                        sboxLink720 = jsonObject.optString("sboxLink720").toString();
                        ChannelLink channelLink = new ChannelLink();
                        channelLink.setChannelId(channelId);
                        channelLink.setSboxLink180(sboxLink180);
                        channelLink.setSboxLink360(sboxLink360);
                        channelLink.setSboxLink480(sboxLink480);
                        channelLink.setSboxLink720(sboxLink720);
                        AnotherChannelLink = channelLink;
                        StreamingPlayer.channelId = channelId;
                        callBackHandler.notyfy();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}