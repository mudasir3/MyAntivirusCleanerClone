package com.ln.antivirus.mobilesecurity.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ln.antivirus.mobilesecurity.adapter.ResultAdapter;
import com.ln.antivirus.mobilesecurity.base.BaseToolbarActivity;
import com.ln.antivirus.mobilesecurity.fragment.ResloveProblemDetailsFragment;
import com.ln.antivirus.mobilesecurity.iface.IProblem;
import com.ln.antivirus.mobilesecurity.iface.IResultItemSelectedListener;
import com.ln.antivirus.mobilesecurity.service.MonitorShieldService;
import com.ln.antivirus.mobilesecurity.util.TypeFaceUttils;
import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.studioninja.antivirus.mobilesecurity.R;
import com.unity3d.ads.UnityAds;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;

public class ResultActivity extends BaseToolbarActivity {
    public static final String PROBLEM_DETAIL_FRAGMENT_TAG = "PROBLEM_DETAIL";
    public static final int REQUEST_DETAIL_PROBLEM = 3;
    ResultAdapter adapter;
    private boolean bound;
    private IProblem comu;
    @BindView(R.id.framelayout_skip_all)
    View framelayout_skip_all;
    private MonitorShieldService monitorShieldService;
    @BindView(R.id.result_layout)
    View result_layout;
    @BindView(R.id.rv_scan_result)
    RecyclerView rv_scan_result;


    private StartAppNativeAd startAppNativeAd;
    private NativeAdDetails nativeAd;
    private StartAppAd startAppAd;

    private AdRequest adRequest ;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int random_no=0;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            ResultActivity.this.bound = true;
            ResultActivity.this.monitorShieldService = ((MonitorShieldService.MonitorShieldLocalBinder) service).getServiceInstance();
            ResultActivity.this.init();
        }

        public void onServiceDisconnected(ComponentName name) {
            ResultActivity.this.bound = false;
            ResultActivity.this.monitorShieldService = null;
        }
    };
    @BindView(R.id.tv_num_of_issues)
    TextView tv_num_of_issues;
    @BindView(R.id.tv_skip_all)
    TextView tv_skip_all;

    private void customFont() {
        TypeFaceUttils.setNomal((Context) this, this.tv_num_of_issues);
        TypeFaceUttils.setNomal((Context) this, this.tv_skip_all);
    }

    public int getLayoutId() {
        return R.layout.activity_scan_result;
    }

    public MonitorShieldService getMonitorShieldService() {
        return this.monitorShieldService;
    }

    public IProblem getComu() {
        return this.comu;
    }

    public void setComu(IProblem comu) {
        this.comu = comu;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= 19) {
            getWindow().setFlags(67108864, 67108864);
        }
        bindService(new Intent(this, MonitorShieldService.class), this.serviceConnection, 1);
        customFont();
        getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {
            public void onBackStackChanged() {
                if (ResultActivity.this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    ResultActivity.this.result_layout.setVisibility(0);
                } else {
                    ResultActivity.this.result_layout.setVisibility(8);
                }
            }
        });

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
                nativeAd.sendImpression(ResultActivity.this);

            }
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {

            // Error occurred while loading the native ad
        }
    };

    private void init() {
        this.tv_num_of_issues.setText(getResources().getString(R.string.found) + " " + this.monitorShieldService.getMenacesCacheSet().getItemCount() + " " + getResources().getString(R.string.issues));
        this.adapter = new ResultAdapter(this, new ArrayList(this.monitorShieldService.getMenacesCacheSet().getSet()));
        this.rv_scan_result.setAdapter(this.adapter);
        this.rv_scan_result.setLayoutManager(new StickyHeaderLayoutManager());
        this.adapter.setResultItemSelectedStateChangedListener(new IResultItemSelectedListener() {
            public void onItemSelected(IProblem bpdw, ImageView iv_icon_app, Context c) {
                ResultActivity.this.setComu(bpdw);
                ResloveProblemDetailsFragment f = (ResloveProblemDetailsFragment) ResultActivity.this.getSupportFragmentManager().findFragmentByTag(ResultActivity.PROBLEM_DETAIL_FRAGMENT_TAG);
                if (f == null) {
                    f = new ResloveProblemDetailsFragment();
                }
                FragmentTransaction transaction = ResultActivity.this.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.container, f, ResultActivity.PROBLEM_DETAIL_FRAGMENT_TAG);
                transaction.addToBackStack(ResultActivity.PROBLEM_DETAIL_FRAGMENT_TAG);
                transaction.commitAllowingStateLoss();
            }
        });
        this.framelayout_skip_all.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ResultActivity.this.startActivity(new Intent(ResultActivity.this, SafeActivity.class));
                ResultActivity.this.finish();
            }
        });
    }

    protected void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        if (this.bound && this.monitorShieldService != null) {
            unbindService(this.serviceConnection);
            this.bound = false;
        }
        super.onDestroy();
    }

    public void refresh(IProblem iProblem) {
        this.adapter.remove(iProblem);
        this.tv_num_of_issues.setText(getResources().getString(R.string.found) + " " + this.monitorShieldService.getMenacesCacheSet().getItemCount() + " " + getResources().getString(R.string.issues));
        if (this.monitorShieldService.getMenacesCacheSet().getItemCount() == 0) {
            startActivity(new Intent(this, SafeActivity.class));
            finish();
        }
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
