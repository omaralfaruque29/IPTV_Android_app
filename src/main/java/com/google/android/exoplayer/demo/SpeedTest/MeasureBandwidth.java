package com.google.android.exoplayer.demo.SpeedTest;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Developed by rubayet on 6/6/16.
 */
public class MeasureBandwidth{


    public String sessionFileUrl0;
    public String sessionFileUrl1;
    public String sessionFileUrl2;

    public double fileSize;
    public double speed;
    public double totalSpeed;

    private int counter = 0;

    public MeasureBandwidth(String sessionFileUrl0,String sessionFileUrl1, String sessionFileUrl2,double fileSize){
        this.sessionFileUrl0 = sessionFileUrl0;
        this.sessionFileUrl1 = sessionFileUrl1;
        this.sessionFileUrl2 = sessionFileUrl2;

        this.fileSize = fileSize;
        this.totalSpeed = 0;
    }

    public double getSpeed(){
        return this.speed;
    }
    public void setSpeed(double speed){
        this.speed = speed;
    }

    public double getTotalSpeed() {
        return totalSpeed;
    }

    public void setTotalSpeed(double totalSpeed) {
        this.totalSpeed = totalSpeed;
    }

    public void startMeasureSession(){
        BufferedReader br = null;
        URLConnection uc = null;
        InputStream in =null;
        long startTime = 0;
        try {
            URL fileURL = new URL(sessionFileUrl0);
            startTime = System.currentTimeMillis();
            uc = fileURL.openConnection();
            InputStream rawStream = uc.getInputStream();
            in = new BufferedInputStream(rawStream);

            int contentLength = uc.getContentLength();

            byte[] data = new byte[contentLength];
            int bytesRead = 0;
            int offset = 0;

            System.out.println("start download" + contentLength);
            while (offset < contentLength) {
                try {
                    System.out.println("rest .... " + (contentLength-offset));
                    bytesRead = in.read(data, offset, data.length - offset);
                }catch (IOException e) {
                    System.out.println("could not read");
                    e.printStackTrace();
                }

                if (bytesRead == -1)
                    break;
                offset += bytesRead;
            }

            long endTime = System.currentTimeMillis();

            double speed = SpeedCalculation(startTime,endTime);
            setSpeed(speed);

            System.out.println("download speed "+ speed + "Kbps" + "duration sec"+ (endTime-startTime)/1000 );
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    private double SpeedCalculation(long startTime, long endTime) {
        int duration = (int) (endTime-startTime)/1000;
        long bitsLoad = (long)(fileSize * 1000 * 1000 * 8) ; //assume fileSize in bytes
        double speedBps = (bitsLoad/duration);
        double speedKbs = (speedBps/1024);
        double speedMbs = (speedKbs/1024);
        return speedKbs;
    }

}
