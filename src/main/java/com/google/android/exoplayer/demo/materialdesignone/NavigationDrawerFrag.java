package com.google.android.exoplayer.demo.materialdesignone;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.exoplayer.demo.Model.Category;

import com.google.android.exoplayer.demo.Model.Channel;
import com.google.android.exoplayer.demo.Model.Country;
import com.google.android.exoplayer.demo.Model.Language;
import com.google.android.exoplayer.demo.R;
import com.google.android.exoplayer.demo.adapter.ListRecyclerAdapterForCategory;
import com.google.android.exoplayer.demo.adapter.ListRecyclerAdapterForLanguage;

import com.google.android.exoplayer.demo.adapter.RecyclerViewAdapter;
import com.google.android.exoplayer.demo.adapter.listRecyclerAdapter;
import com.google.android.exoplayer.demo.service.Storage;
import com.google.android.exoplayer.demo.sqLiteDatabase.ChannelModel;
import com.google.android.exoplayer.demo.sqLiteDatabase.DBHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFrag extends Fragment {

    public static RecyclerView recyclerView;
    private ActionBarDrawerToggle  mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    boolean mDrawer = false;
    private View drawerLayout;
    public static List<Country> allCountry;
    public static List<Category> allCategory;
    public static List<Language> allLanguage;

    private listRecyclerAdapter adapter;
    private ListRecyclerAdapterForCategory adapterForCategory;
    private ListRecyclerAdapterForLanguage adapterForLanguage;

    public static boolean byCountry = true;
    public static boolean byCategory = true;
    public static boolean byLanguage = true;
    public static boolean byFavourite = false;

    public static Spinner selectionSpinner;
    public static Button favButton;

    ProgressDialog dialog;

    public NavigationDrawerFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        drawerLayout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) drawerLayout.findViewById(R.id.drawerRecyclerList);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));

        favButton = (Button) drawerLayout.findViewById(R.id.favButton);
        favButton.setBackgroundColor(Color.parseColor("#37474f"));
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFabButton();
            }
        });

        selectionSpinner = (Spinner) drawerLayout.findViewById(R.id.selectionType);
        String[] items = new String[]{"Country", "Category", "Language"};
        ArrayAdapter<String> selectionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        selectionSpinner.setAdapter(selectionAdapter);
        selectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Country")) {
                   // getAllCountry("http://www.livestreamer.com/api/allCountry");
                    new AllCountry().execute("http://www.livestreamer.com/api/0");
                    byCountry = true;
                    byCategory = false;
                    byLanguage = false;
                    byFavourite = false;

                } else if (selectedItem.equals("Category")) {
                    new AllCategory().execute("http://www.livestreamer.com/api/1");
                    //getAllCategory("http://www.livestreamer.com/api/allCategory");
                    byCountry = false;
                    byCategory = true;
                    byLanguage = false;
                    byFavourite = false;
                } else if (selectedItem.equals("Language")) {
                   // getAllLanguage("http://www.livestreamer.com/api/allLanguage");
                    new AllLanguage().execute("http://www.livestreamer.com/api/2");
                    byCountry = false;
                    byCategory = false;
                    byLanguage = true;
                    byFavourite = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return drawerLayout;
    }

    private static Bitmap getLogoBitmapFromSD(String channelLogoName) {
        Bitmap scaledBmp = null;
        String root = Environment.getExternalStorageDirectory().toString();
        try {
            File file = new File(root + "/channel_logo/", channelLogoName);
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file));
            scaledBmp = Bitmap.createScaledBitmap(bmp, 250, 150, false);
        } catch (FileNotFoundException ex) {
            Log.d("", "file not found in internal storage");
            ex.printStackTrace();
        }
        return scaledBmp;
    }

    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                /*if (!mDrawer){
                    mDrawer=true;
                    saveToSharedPref(getActivity(),"drawerState",mDrawer+"");
                }*/

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };


        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

//    public void getAllCountry(String url) {
//        allCountry = new ArrayList<>();
//        dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    List<categoryDetail> countryData = new ArrayList<>();
//                    JSONObject jsonRootObject = new JSONObject(response.toString());
//                    Boolean success = (boolean)jsonRootObject.get("success");
//                    if(success) {
//                        JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            int countryId = Integer.parseInt(jsonObject.optString("countryId").toString());
//                            String countryName = jsonObject.optString("countryName").toString();
//                            Country country = new Country();
//                            country.setCounrtyId(countryId);
//                            country.setCountyName(countryName);
//                            allCountry.add(country);
//
//                            categoryDetail current = new categoryDetail();
//                            current.categoryName = countryName;
//                            current.icon = R.drawable.ic_play_arrow;
//                            countryData.add(current);
//                        }
//
//                        adapter = new listRecyclerAdapter(getActivity(), countryData);
//                        recyclerView.setAdapter(adapter);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                dialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
//                error.printStackTrace();
//                Log.i("ERROR..", error.toString());
//                dialog.dismiss();
//            }
//        });
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(getActivity()).add(stringRequest);
//    }

    public static void getAllCategory(String jsonData) {
//        allCategory = new ArrayList<>();
//        dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, categoryUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    List<categoryDetail> categoryData = new ArrayList<>();
//                    JSONObject jsonRootObject = new JSONObject(response.toString());
//                    Boolean success = (boolean)jsonRootObject.get("success");
//                    if(success) {
//                        JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            int categoryId = Integer.parseInt(jsonObject.optString("categoryId").toString());
//                            String categoryName = jsonObject.optString("categoryName").toString();
//                            Category category = new Category();
//                            category.setCategoryId(categoryId);
//                            category.setCategoryName(categoryName);
//                            allCategory.add(category);
//
//                            categoryDetail current = new categoryDetail();
//                            current.categoryName = categoryName;
//                            current.icon = R.drawable.ic_play_arrow;
//                            categoryData.add(current);
//                        }
//
//                        adapterForCategory = new ListRecyclerAdapterForCategory(getActivity(),categoryData);
//                        recyclerView.setAdapter(adapterForCategory);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                dialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
//                error.printStackTrace();
//                Log.i("ERROR..", error.toString());
//                dialog.dismiss();
//            }
//        });
//
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }



//    public void getAllLanguage(String url) {
//        allLanguage = new ArrayList<>();
//        dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    List<categoryDetail> languageData = new ArrayList<>();
//                    JSONObject jsonRootObject = new JSONObject(response.toString());
//                    Boolean success = (boolean)jsonRootObject.get("success");
//                    if(success) {
//                        JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            int languageId = Integer.parseInt(jsonObject.optString("languageId").toString());
//                            String languageName = jsonObject.optString("languageName").toString();
//                            Language language = new Language();
//                            language.setLanguageId(languageId);
//                            language.setLanguageName(languageName);
//                            allLanguage.add(language);
//
//                            categoryDetail current = new categoryDetail();
//                            current.categoryName = languageName;
//                            current.icon = R.drawable.ic_play_arrow;
//                            languageData.add(current);
//                        }
//
//                        adapterForLanguage = new ListRecyclerAdapterForLanguage(getActivity(),languageData);
//                        recyclerView.setAdapter(adapterForLanguage);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                dialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
//                error.printStackTrace();
//                Log.i("ERROR..", error.toString());
//                dialog.dismiss();
//            }
//        });
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
//                3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(getActivity()).add(stringRequest);
//    }

    public static void clickFabButton(){
        Log.d("Tag", "fav button clicked");
        DBHandler db = new DBHandler(MainActivity.getAppContex());
        List<ChannelModel> favChannels = new ArrayList<ChannelModel>();
        favChannels = db.getAllChannels();

        List<Channel> channels = new ArrayList<>();
        if(!RecyclerViewAdapter.channelIds.isEmpty())
            RecyclerViewAdapter.channelIds.clear();

        if(!favChannels.isEmpty()){
            for(ChannelModel favChannel : favChannels){
                Channel channel = new Channel();
                channel.setChannelName(favChannel.getChannelName());
                channel.setChannelId(favChannel.getChannelId());
                channel.setChannelLogoName(favChannel.getChannelLogo());

                Bitmap logo = getLogoBitmapFromSD(favChannel.getChannelLogo());
                channel.setChannelBitMap(logo);
                channels.add(channel);
                RecyclerViewAdapter.channelIds.add(favChannel.getChannelId());
            }
        }

        Storage.getInstance().setCurrentChannelList(channels);
        System.out.println("fav channel size:"+channels.size());
        if(channels.size() > 0){
            MainActivity.populateNewData(channels);
        }
        byFavourite = true;
        MainActivity.favButtonClick = true;
    }

    public class AllCountry extends AsyncTask<String, Void, Void> {

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
            allCountry = new ArrayList<>();
            dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);

            try {
                List<categoryDetail> countryData = new ArrayList<>();
                JSONObject jsonRootObject = new JSONObject(buffer.toString());
                Boolean success = (boolean) jsonRootObject.get("success");
                if (success) {
                    JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int countryId = Integer.parseInt(jsonObject.optString("id").toString());
                        String countryName = jsonObject.optString("name").toString();
                        Country country = new Country();
                        country.setId(countryId);
                        country.setName(countryName);
                        allCountry.add(country);

                        categoryDetail current = new categoryDetail();
                        current.categoryName = countryName;
                        current.icon = R.drawable.ic_play_arrow;
                        countryData.add(current);
                    }

                    adapter = new listRecyclerAdapter(getActivity(), countryData);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }

    public class AllCategory extends AsyncTask<String, Void, Void> {

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
                while ((line = reader.readLine()) !=null){
                    buffer.append(line);
                }
                System.out.println("buffer.toString****************" + buffer.toString());

            } catch (Exception ex){
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
                allCategory = new ArrayList<>();
                dialog = ProgressDialog.show(MainActivity.getAppContex(), "Loading..", "Please wait...", true);

                List<categoryDetail> categoryData = new ArrayList<>();
                JSONObject jsonRootObject = new JSONObject(buffer.toString());
                Boolean success = (boolean) jsonRootObject.get("success");
                if (success) {
                    JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int categoryId = Integer.parseInt(jsonObject.optString("id").toString());
                        String categoryName = jsonObject.optString("name").toString();
                        Category category = new Category();
                        category.setId(categoryId);
                        category.setName(categoryName);
                        allCategory.add(category);

                        categoryDetail current = new categoryDetail();
                        current.categoryName = categoryName;
                        current.icon = R.drawable.ic_play_arrow;
                        categoryData.add(current);
                    }

                    adapterForCategory = new ListRecyclerAdapterForCategory(getActivity(), categoryData);
                    recyclerView.setAdapter(adapterForCategory);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                }
                dialog.dismiss();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class AllLanguage extends AsyncTask<String, Void, Void> {

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
            allLanguage = new ArrayList<>();
            try {
                List<categoryDetail> languageData = new ArrayList<>();
                JSONObject jsonRootObject = new JSONObject(buffer.toString());
                Boolean success = (boolean) jsonRootObject.get("success");
                if (success) {
                    JSONArray jsonArray = jsonRootObject.optJSONArray("datarows");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int languageId = Integer.parseInt(jsonObject.optString("id").toString());
                        String languageName = jsonObject.optString("name").toString();
                        Language language = new Language();
                        language.setId(languageId);
                        language.setName(languageName);
                        allLanguage.add(language);

                        categoryDetail current = new categoryDetail();
                        current.categoryName = languageName;
                        current.icon = R.drawable.ic_play_arrow;
                        languageData.add(current);
                    }

                    adapterForLanguage = new ListRecyclerAdapterForLanguage(getActivity(), languageData);
                    recyclerView.setAdapter(adapterForLanguage);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }
}
