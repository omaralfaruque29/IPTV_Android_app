package com.google.android.exoplayer.demo.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.Model.ChannelLink;
import com.google.android.exoplayer.demo.R;
import com.google.android.exoplayer.demo.materialdesignone.MainActivity;
import com.google.android.exoplayer.demo.materialdesignone.categoryDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by raj on 5/24/16.
 */
public class ChannelParseByCountry {

    private String countryName;
    private Boolean success;
    private JSONObject jsonRootObject;
    private Context context;
    private String root = Environment.getExternalStorageDirectory().toString();;
    private int counter = 0;
    private List<String> logoList;
    private List<String> channelNameList;
    private List<Channel> channels;
    private List<Channel> channelsToStore = new ArrayList<>();
    //private String[] streamlLinks;

    public ChannelParseByCountry(Context context, String countryName) {
        this.context = context;
        this.countryName = countryName;
        createLogoDirectory();
    }

    public void getChannelsByCountry(String response) {
        try {
            channels = new ArrayList<>();
            logoList = new ArrayList<>();
            channelNameList = new ArrayList<>();
            jsonRootObject = new JSONObject(response);
            success = (boolean) jsonRootObject.get("success");
            if (success) {
                JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    Integer channelId = (Integer) obj.get("id");
                    String channelName = (String) obj.get("name");
                    String link = (String) obj.get("link");
                    String logo = (String) obj.get("logo");

                    JSONObject channelLinkObj =  obj.getJSONObject("streamLink");
                    String sboxLink720 = channelLinkObj.getString("sboxLink720");
                    String sboxLink480 = channelLinkObj.getString("sboxLink480");
                    String sboxLink360 = channelLinkObj.getString("sboxLink360");
                    String sboxLink180 = channelLinkObj.getString("sboxLink180");

                    ChannelLink channelLink = new ChannelLink();
                    channelLink.setChannelId(channelId);
                    channelLink.setSboxLink180(sboxLink180);
                    channelLink.setSboxLink360(sboxLink360);
                    channelLink.setSboxLink480(sboxLink480);
                    channelLink.setSboxLink720(sboxLink720);


                    logoList.add(logo);
                    channelNameList.add(channelName);

                    Channel ch = new Channel();
                    ch.setChannelId(channelId);
                    ch.setChannelName(channelName);
                    ch.setChannelLogoName(logo);
                    ch.setChannelLink(channelLink);
                    channelsToStore.add(ch);


                    Bitmap tempBmp = getLogoFromStorage(logo);
                    if(tempBmp != null){
                        Bitmap scaledBmp = Bitmap.createScaledBitmap(tempBmp, 250, 150, false);
                        Channel channel = new Channel();
                        channel.setChannelName(channelNameList.get(counter));
                        channel.setChannelBitMap(scaledBmp);
                        channels.add(channel);
                        counter++;
                        MainActivity.populateNewData(channels);
                    } else {
                        new downloadLogo().execute("http://www.livestreamer.com/images/" + logo);
                    }
                }

                Storage.getInstance().setCurrentGrid(0);
                MainActivity.firstTabFrag.rView.setFocusable(true);
                MainActivity.firstTabFrag.rView.requestFocus();
                MainActivity.currentFocusView = 2;

                Map<String, List<Channel>> channelsByCountry = new HashMap<>();
                channelsByCountry.put(countryName, channelsToStore);
                Storage.getInstance().setChannelsByCountry(channelsByCountry);


                Storage.getInstance().getCurrentChannelList().clear();
                Storage.getInstance().setCurrentChannelList(channelsToStore);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Bitmap getLogoFromStorage(String logo) {
        Bitmap bmp = null;
        try {
            File file = new File(root + "/channel_logo/", logo);
            bmp = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            Log.d("", "file not found in internal storage");
            ex.printStackTrace();
        }
        return bmp;
    }

    private void createLogoDirectory() {
        File logoDirectory = new File(root + "/channel_logo");
        if (!logoDirectory.exists()) {
            logoDirectory.mkdirs();
        }
    }

    private class downloadLogo extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String...logoUrl) {
            Bitmap bitMap = null;
            try {
                URL url = new URL(logoUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitMap = BitmapFactory.decodeStream(input);

            } catch (Exception ex){
                ex.printStackTrace();
            }
            return  bitMap;
        }

        protected void onPostExecute(Bitmap bitMap) {
            try {
                OutputStream fOut = null;
                String logoName = logoList.get(counter);
                String logoPath = root + "/channel_logo/" + logoName;

                File file = new File(logoPath);
                file.createNewFile();
                fOut = new FileOutputStream(file);


                bitMap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitMap, 250, 150, false);

                Channel channel = new Channel();
                channel.setChannelName(channelNameList.get(counter));
                channel.setChannelBitMap(scaledBitmap);
                channels.add(channel);
                counter++;

                MainActivity.populateNewData(channels);

            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
    }
}
