package com.ln.antivirus.mobilesecurity.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ln.antivirus.mobilesecurity.base.BaseToolbarActivity;
import com.ln.antivirus.mobilesecurity.util.TypeFaceUttils;
import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.takwolf.android.lock9.Lock9View;
import com.takwolf.android.lock9.Lock9View.CallBack;
import com.studioninja.antivirus.mobilesecurity.R;
import com.unity3d.ads.UnityAds;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;

public class AppLockEditPasswordActivity extends BaseToolbarActivity {
    @BindView(R.id.la_old_password)
    View la_old_password;
    @BindView(R.id.la_password)
    View la_password;
    @BindView(R.id.la_password_again)
    View la_password_again;
    @BindView(R.id.lock_view)
    Lock9View lock_view;
    @BindView(R.id.lock_view_again)
    Lock9View lock_view_again;
    @BindView(R.id.lock_view_old)
    Lock9View lock_view_old;
    private String password;
    private SharedPreferences sharedPreferences;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.title_again)
    TextView title_again;
    @BindView(R.id.title_old)
    TextView title_old;

    private StartAppNativeAd startAppNativeAd;
    private NativeAdDetails nativeAd;
    private StartAppAd startAppAd;

    private AdRequest adRequest ;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int random_no=0;


    private void customFont() {
        TypeFaceUttils.setNomal((Context) this, this.title_old);
        TypeFaceUttils.setNomal((Context) this, this.title);
        TypeFaceUttils.setNomal((Context) this, this.title_again);
    }

    public int getLayoutId() {
        return R.layout.activity_app_lock_edit_password;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customFont();
        this.sharedPreferences = getSharedPreferences(AppLockCreatePasswordActivity.SHARED_PREFERENCES_NAME, 0);
        initView();


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
                nativeAd.sendImpression(AppLockEditPasswordActivity.this);

            }
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {

            // Error occurred while loading the native ad
        }
    };



    private void initView() {
        this.lock_view_old.setCallBack(new CallBack() {
            public void onFinish(String password) {
                if (password.equals(AppLockEditPasswordActivity.this.sharedPreferences.getString(AppLockCreatePasswordActivity.KEY_PASSWORD, null))) {
                    AppLockEditPasswordActivity.this.la_old_password.setVisibility(8);
                    AppLockEditPasswordActivity.this.la_password.setVisibility(0);
                    return;
                }
                Snackbar.make(AppLockEditPasswordActivity.this.la_old_password, R.string.patterns_do_not_match, -1).show();
            }
        });
        this.lock_view.setCallBack(new CallBack() {
            public void onFinish(String password) {
                AppLockEditPasswordActivity.this.password = password;
                AppLockEditPasswordActivity.this.la_password.setVisibility(8);
                AppLockEditPasswordActivity.this.la_password_again.setVisibility(0);
            }
        });
        this.lock_view_again.setCallBack(new CallBack() {
            public void onFinish(String password) {
                if (AppLockEditPasswordActivity.this.password.equals(password)) {
                    AppLockEditPasswordActivity.this.sharedPreferences.edit().putString(AppLockCreatePasswordActivity.KEY_PASSWORD, password).apply();
                    AppLockEditPasswordActivity.this.finish();
                    return;
                }
                Snackbar.make(AppLockEditPasswordActivity.this.la_password_again, R.string.patterns_do_not_match, -1).show();
            }
        });
    }

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
}
