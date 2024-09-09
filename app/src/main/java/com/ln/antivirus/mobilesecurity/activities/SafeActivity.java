package com.ln.antivirus.mobilesecurity.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoOptions;
import com.ln.antivirus.mobilesecurity.base.BaseToolbarActivity;
import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.studioninja.antivirus.mobilesecurity.R;
import com.unity3d.ads.UnityAds;

import java.util.ArrayList;
import java.util.Random;

public class SafeActivity extends BaseToolbarActivity {

    private ImageView img_rocket;
    private View ll_done;
    CardView cardView;

     InterstitialAd mInterstitialAd;
    NativeExpressAdView mAdView;

    private StartAppNativeAd startAppNativeAd;
    private NativeAdDetails nativeAd;
    private StartAppAd startAppAd;


    private int random_no=0;



    public int getLayoutId() {
        return R.layout.activity_safe;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Locate the NativeExpressAdView.
        mAdView = (NativeExpressAdView) findViewById(R.id.adView);



        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(new AdRequest.Builder().build());
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit));

        random_no=getRandomNumberInRange(1,3);

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



        this.ll_done = findViewById(R.id.ll_done);
        this.img_rocket = (ImageView) findViewById(R.id.img_rocket);
        cardView = (CardView) findViewById(R.id.card_view_recommend_app);
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_left_in);
        set.setTarget(this.img_rocket);


        final Animation animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                animShow.setDuration(1000);

            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        this.ll_done.startAnimation(animation);
        set.start();

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
                nativeAd.sendImpression(SafeActivity.this);

            }
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {

            // Error occurred while loading the native ad
        }
    };
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mInterstitialAd.show();

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
