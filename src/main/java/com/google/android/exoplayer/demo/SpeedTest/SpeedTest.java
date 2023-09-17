package com.google.android.exoplayer.demo.SpeedTest;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.android.exoplayer.demo.materialdesignone.MainActivity;

/**
 * Developed by rubayet on 6/6/16.
 */
public class SpeedTest extends AsyncTask {

    //private  ProgressDialog dialog = new ProgressDialog(MainActivity.getAppContex());;


    public String remoteServerId;


    private String remoteUploadSpeed;
    private String remoteDownloadSpeed;
    private String localUploadSpeed;
    private String localdownloadSpeed;

    CallbackInterface callback;

    public String getResQuality() {
        return resQuality;
    }

    public void setResQuality(String resQuality) {
        this.resQuality = resQuality;
    }

    private String resQuality;

    public static String perSessionSpeed;
    public static String totalBndwidth;

    private MeasureBandwidth measure;

    ProgressDialog dialog;

    public String getRemoteServerId() {
        return remoteServerId;
    }
    public void setRemoteServerId(String remoteServerId) {
        this.remoteServerId = remoteServerId;
    }


    public SpeedTest (String remoteServerId){
        this.remoteServerId = remoteServerId;
        this.measure = new MeasureBandwidth("http://wbde01.livestreamer.com/720.ts", "http://wbde01.livestreamer.com/721.ts","http://wbde01.livestreamer.com/722.ts", 4.4);
    }

    public SpeedTest(CallbackInterface callback){
        this.callback = callback;
        this.measure = new MeasureBandwidth("http://wbde01.livestreamer.com/720.ts", "http://wbde01.livestreamer.com/721.ts","http://wbde01.livestreamer.com/722.ts", 4.4);
    }

    public String getRemoteUploadSpeed() {
        return remoteUploadSpeed;
    }
    public String getRemoteDownloadSpeed() {
        return remoteDownloadSpeed;
    }
    public String getLocalUploadSpeed() {
        return localUploadSpeed;
    }
    public String getLocaldownloadSpeed() {
        return localdownloadSpeed;
    }


    @Override
    protected Object doInBackground(Object[] params) {

        measure.startMeasureSession();

        Double remoteSpeedinKB = measure.getSpeed();

        this.remoteDownloadSpeed = remoteSpeedinKB.toString();
        this.remoteUploadSpeed = "fokka";

        System.out.println("Session Bandwidth #"+remoteDownloadSpeed);
        System.out.println("Session Bandwidth #"+remoteDownloadSpeed);
        System.out.println("Session Bandwidth #"+remoteDownloadSpeed);
        System.out.println("Session Bandwidth #"+remoteDownloadSpeed);
        System.out.println("Session Bandwidth #"+remoteDownloadSpeed);

        perSessionSpeed = remoteSpeedinKB.toString();

        String quality = setInitialQuality(remoteSpeedinKB);
        setResQuality(quality);
        return quality;
    }

    private String setInitialQuality(Double remoteSpeedinKB) {
        System.out.println("persession  mm"+ remoteSpeedinKB);
        String quality = null;
        if(remoteSpeedinKB <= 400){
            quality = "180";
        }else if(remoteSpeedinKB > 400 && remoteSpeedinKB <= 900){
            quality = "360";
        }else if(remoteSpeedinKB > 900 && remoteSpeedinKB <= 1800){
            quality = "480";
        }else if(remoteSpeedinKB > 1800){
            quality = "720";
        }
        System.out.println("initial quality " + quality);
        return quality;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog = ProgressDialog.show(MainActivity.getAppContex(), "Checking video quality", "Please wait...", true);
    }

    @Override
    protected void onPostExecute(Object quality) {
        super.onPostExecute(quality);
        callback.run(quality);
        dialog.dismiss();
    }

}
