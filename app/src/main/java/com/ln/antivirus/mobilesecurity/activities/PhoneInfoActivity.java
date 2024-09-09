package com.ln.antivirus.mobilesecurity.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.liulishuo.magicprogresswidget.MagicProgressBar;
import com.ln.antivirus.mobilesecurity.base.BaseToolbarActivity;
import com.ln.antivirus.mobilesecurity.util.RootUtil;
import com.ln.antivirus.mobilesecurity.util.TypeFaceUttils;
import com.ln.antivirus.mobilesecurity.util.Utils;
import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.studioninja.antivirus.mobilesecurity.R;
import com.unity3d.ads.UnityAds;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

public class PhoneInfoActivity extends BaseToolbarActivity {
    public static final int PHONE_INFO_REQUEST_CODE = 2345;
    private Handler handler = new Handler();
    @BindView(R.id.pb_cpu)
    MagicProgressBar pb_cpu;
    @BindView(R.id.pb_ram)
    MagicProgressBar pb_ram;
    @BindView(R.id.pb_storage)
    MagicProgressBar pb_storage;
    private Timer timer;
    private TimerTask timerTask;
    private TextView tvInfoCPU;
    private TextView tvInforRam;
    private TextView tvInforStorage;
    @BindView(R.id.tv_basic_information)
    TextView tv_basic_information;
    @BindView(R.id.tv_title_cpu)
    TextView tv_title_cpu;
    @BindView(R.id.tv_title_imei)
    TextView tv_title_imei;
    @BindView(R.id.tv_title_ram)
    TextView tv_title_ram;
    @BindView(R.id.tv_title_root_state)
    TextView tv_title_root_state;
    @BindView(R.id.tv_title_storage)
    TextView tv_title_storage;
    @BindView(R.id.tv_title_system_os_version)
    TextView tv_title_system_os_version;

    private StartAppNativeAd startAppNativeAd;
    private NativeAdDetails nativeAd;
    private StartAppAd startAppAd;

    private AdRequest adRequest ;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int random_no=0;



    public int getLayoutId() {
        return R.layout.activity_phone_info;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.phone_info));
        initView();
        startTimer();


        startAppAd = new StartAppAd(this);
        startAppNativeAd = new StartAppNativeAd(this);
        nativeAd = null;
        //StartApp Ads
        startAppNativeAd.loadAd(
                new NativeAdPreferences()
                        .setAdsNumber(1)
                        .setAutoBitmapDownload(true)
                        .setImageSize(NativeAdPreferences.NativeAdBitmapSize.SIZE150X150),
                nativeAdListener);

        mAdView = (AdView) this.findViewById(R.id.adView1);

        adRequest = new AdRequest.Builder()
                .addTestDevice("33BE2250B43518CCDA7DE426D04EE232").build();

        mAdView.loadAd(adRequest);

        random_no=getRandomNumberInRange(1,3);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.intersetial_ad_unit_id));
        mInterstitialAd.loadAd(adRequest);
        if (random_no == 1) {
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        } else if (random_no == 2) {
            if (UnityAds.isReady("rewardedVideo")) { //Make sure a video is available & the placement is valid.
                UnityAds.show(this, "rewardedVideo");
            }
        } else if (random_no==3){
            startAppAd.loadAd();
            startAppAd.showAd();
        } else {
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        }

    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private AdEventListener nativeAdListener = new AdEventListener() {

        @Override
        public void onReceiveAd(Ad ad) {

            // Get the native ad
            ArrayList<NativeAdDetails> nativeAdsList = startAppNativeAd.getNativeAds();
            if (nativeAdsList.size() > 0) {
                nativeAd = nativeAdsList.get(0);
            }

            // Verify that an ad was retrieved
            if (nativeAd != null) {

                // When ad is received and displayed - we MUST send impression
                nativeAd.sendImpression(PhoneInfoActivity.this);

            }
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {

            // Error occurred while loading the native ad
        }
    };

    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    protected void onResume() {
        if (mAdView != null) {
            mAdView.resume();
        }
        if (random_no==1) {
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });
        } else if (random_no==2) {
            if (UnityAds.isReady("rewardedVideo")) { //Make sure a video is available & the placement is valid.
                UnityAds.show(this, "rewardedVideo");
            }

        } else if (random_no==3) {
            startAppAd.onResume();
        } else{
            startAppAd.onResume();
        }

        super.onResume();
    }

    @SuppressLint({"MissingPermission", "HardwareIds", "WrongConstant"})
    private void initView() {
        TextView tvSystOSVersion = (TextView) findViewById(R.id.tv_system_os_version);
        tvSystOSVersion.setText("Android " + String.valueOf(VERSION.RELEASE));
        TextView tvRootState = (TextView) findViewById(R.id.tv_root_state);
        if (RootUtil.isDeviceRooted()) {
            tvRootState.setText("Rooted");
        } else {
            tvRootState.setText("Not Rooted");
        }
        if (VERSION.SDK_INT < 23) {
            TextView tvIMEI = (TextView) findViewById(R.id.tv_imei);
            tvIMEI.setText(((TelephonyManager) getSystemService("phone")).getDeviceId());
            TypeFaceUttils.setNomal((Context) this, tvIMEI);
        }
        this.tvInforStorage = (TextView) findViewById(R.id.tv_info_storage);
        this.tvInforRam = (TextView) findViewById(R.id.tv_info_ram);
        this.tvInfoCPU = (TextView) findViewById(R.id.tv_info_cpu);
        TypeFaceUttils.setNomal((Context) this, this.tv_title_cpu);
        TypeFaceUttils.setNomal((Context) this, this.tv_title_ram);
        TypeFaceUttils.setNomal((Context) this, this.tv_title_storage);
        TypeFaceUttils.setNomal((Context) this, this.tvInfoCPU);
        TypeFaceUttils.setNomal((Context) this, this.tvInforRam);
        TypeFaceUttils.setNomal((Context) this, this.tvInforStorage);
        TypeFaceUttils.setNomal((Context) this, this.tv_basic_information);
        TypeFaceUttils.setNomal((Context) this, this.tv_title_system_os_version);
        TypeFaceUttils.setNomal((Context) this, tvSystOSVersion);
        TypeFaceUttils.setNomal((Context) this, this.tv_title_imei);
        TypeFaceUttils.setNomal((Context) this, this.tv_title_root_state);
        TypeFaceUttils.setNomal((Context) this, tvRootState);
        long totalInternalMemorySize = Utils.getTotalInternalMemorySize();
        this.tvInforStorage.setText(Utils.formatSize(totalInternalMemorySize - Utils.getAvailableInternalMemorySize()));
        this.pb_storage.setSmoothPercent(((float) (totalInternalMemorySize - Utils.getAvailableInternalMemorySize())) / ((float) totalInternalMemorySize));
        long freeRam = Utils.getFreeRAM(this);
        long totalRam = Utils.getTotalRAM(this);
        this.tvInforRam.setText(Utils.formatSize(totalRam - freeRam));
        this.pb_ram.setSmoothPercent(((float) (totalRam - freeRam)) / ((float) totalRam));
    }

    public void startTimer() {
        this.timer = new Timer();
        initializeTimerTask();
        this.timer.schedule(this.timerTask, 500, 1000);
    }

    public void stoptimertask() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    public void initializeTimerTask() {
        this.timerTask = new TimerTask() {
            public void run() {
                PhoneInfoActivity.this.handler.post(new Runnable() {
                    public void run() {
                        int cpu = (int) (PhoneInfoActivity.this.readUsage() * 100.0f);
                        PhoneInfoActivity.this.tvInfoCPU.setText(cpu + "%");
                        PhoneInfoActivity.this.pb_cpu.setSmoothPercent((float) (((double) cpu) / 100.0d));
                    }
                });
            }
        };
    }

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String[] toks = reader.readLine().split(" +");
            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = ((((Long.parseLong(toks[2]) + Long.parseLong(toks[3])) + Long.parseLong(toks[5])) + Long.parseLong(toks[6])) + Long.parseLong(toks[7])) + Long.parseLong(toks[8]);
            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }
            reader.seek(0);
            String load = reader.readLine();
            reader.close();
            toks = load.split(" +");
            long cpu2 = ((((Long.parseLong(toks[2]) + Long.parseLong(toks[3])) + Long.parseLong(toks[5])) + Long.parseLong(toks[6])) + Long.parseLong(toks[7])) + Long.parseLong(toks[8]);
            return ((float) (cpu2 - cpu1)) / ((float) ((cpu2 + Long.parseLong(toks[4])) - (cpu1 + idle1)));
        } catch (IOException ex) {
            ex.printStackTrace();
            return 0.0f;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_INFO_REQUEST_CODE && grantResults.length == 1 && grantResults[0] == 0) {
            TextView tvIMEI = (TextView) findViewById(R.id.tv_imei);
            tvIMEI.setText(((TelephonyManager) getSystemService("phone")).getDeviceId());
            TypeFaceUttils.setNomal((Context) this, tvIMEI);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                stoptimertask();
                finish();
                break;
        }
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        stoptimertask();
    }

    protected void onStop() {
        super.onStop();
        stoptimertask();
    }
}
