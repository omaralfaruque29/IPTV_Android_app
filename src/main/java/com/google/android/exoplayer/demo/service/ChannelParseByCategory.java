package com.google.android.exoplayer.demo.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.Model.ChannelLink;
import com.google.android.exoplayer.demo.materialdesignone.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

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
 * Created by shams on 6/13/16.
 */
public class ChannelParseByCategory {

    private String categoryName;
    private Boolean success;
    private JSONObject jsonRootObject;
    private Context context;
    private String root;
    private int counter = 0;
    private List<String> logoList = new ArrayList<>();
    private List<String> channelNameList = new ArrayList<>();
    private List<Channel> channels;
    private List<Channel> channelsToStore = new ArrayList<>();

    public ChannelParseByCategory(Context context, String categoryName) {
        this.context = context;
        this.categoryName = categoryName;
        createLogoDirectory();
    }

    public List<Channel> getData(String response) {
        try {
            channels = new ArrayList<>();
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
                        Channel channel = new Channel();
                        channel.setChannelName(channelNameList.get(counter));
                        channel.setChannelBitMap(tempBmp);
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

                Map<String, List<Channel>> channelsByCategory = new HashMap<>();
                channelsByCategory.put(categoryName, channelsToStore);
                Storage.getInstance().setChannelsByCategory(channelsByCategory);

                Storage.getInstance().getCurrentChannelList().clear();
                Storage.getInstance().setCurrentChannelList(channelsToStore);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return channels;
    }

    private Bitmap getLogoFromStorage(String logo) {
        Bitmap scaledBmp = null;
        try {
            File file = new File(root + "/channel_logo/", logo);
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file));
            scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 150, false);
        } catch (FileNotFoundException ex) {
            Log.d("", "file not found in internal storage");
            ex.printStackTrace();
        }
        return scaledBmp;
    }

    private void createLogoDirectory() {
        root = Environment.getExternalStorageDirectory().toString();
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
