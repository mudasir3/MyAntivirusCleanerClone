package com.ln.antivirus.mobilesecurity.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ln.antivirus.mobilesecurity.adapter.WarningAdapter;
import com.ln.antivirus.mobilesecurity.base.BaseToolbarActivity;
import com.ln.antivirus.mobilesecurity.iface.IProblem;
import com.ln.antivirus.mobilesecurity.iface.IProblem.ProblemType;
import com.ln.antivirus.mobilesecurity.model.AppProblem;
import com.ln.antivirus.mobilesecurity.model.MenacesCacheSet;
import com.ln.antivirus.mobilesecurity.model.SystemProblem;
import com.ln.antivirus.mobilesecurity.model.UserWhiteList;
import com.ln.antivirus.mobilesecurity.service.MonitorShieldService;
import com.ln.antivirus.mobilesecurity.util.ProblemsDataSetTools;
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

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;

public class ProblemDetailActivity extends BaseToolbarActivity {
    IProblem _problem = null;
    boolean _uninstallingPackage = false;
    @BindView(R.id.adView)
    AdView adView;
    private boolean bound;
    @BindView(R.id.bt_ignore_setting)
    ImageView bt_ignore_setting;
    @BindView(R.id.bt_open_setting)
    ImageView bt_open_setting;
    @BindView(R.id.bt_trust_app)
    ImageView bt_trust_app;
    @BindView(R.id.bt_uninstall_app)
    ImageView bt_uninstall_app;
    @BindView(R.id.iv_icon_app)
    ImageView iv_icon_app;
    @BindView(R.id.ll_layout_for_app)
    LinearLayout ll_layout_for_app;
    @BindView(R.id.ll_layout_for_system)
    LinearLayout ll_layout_for_system;
    private MonitorShieldService monitorShieldService;
    @BindView(R.id.rv_warning_problem)
    RecyclerView rv_warning_problem;


    private StartAppNativeAd startAppNativeAd;
    private NativeAdDetails nativeAd;
    private StartAppAd startAppAd;

    private AdRequest adRequest ;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int random_no=0;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            ProblemDetailActivity.this.bound = true;
            ProblemDetailActivity.this.monitorShieldService = ((MonitorShieldService.MonitorShieldLocalBinder) service).getServiceInstance();
            ProblemDetailActivity.this.init();
        }

        public void onServiceDisconnected(ComponentName name) {
            ProblemDetailActivity.this.bound = false;
            ProblemDetailActivity.this.monitorShieldService = null;
        }
    };
    @BindView(R.id.tv_app_name)
    TextView tv_app_name;

    private void customFont() {
        TypeFaceUttils.setNomal((Context) this, this.tv_app_name);
    }

    public int getLayoutId() {
        return R.layout.activity_problem_detail;
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customFont();
        this._problem = Utils.selectedProblem;
        this.rv_warning_problem.setAdapter(new WarningAdapter(this, this._problem));
        this.rv_warning_problem.setLayoutManager(new LinearLayoutManager(this));
        bindService(new Intent(this, MonitorShieldService.class), this.serviceConnection, 1);

        AdRequest adRequest = new AdRequest.Builder().build();

        this.adView.loadAd(adRequest);


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
                nativeAd.sendImpression(ProblemDetailActivity.this);

            }
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {

            // Error occurred while loading the native ad
        }
    };

    private void init() {
        if (this._problem.getType() == ProblemType.AppProblem) {
            this.ll_layout_for_app.setVisibility(0);
            this.ll_layout_for_system.setVisibility(8);
            final AppProblem appProblem = (AppProblem) this._problem;
            this.bt_uninstall_app.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ProblemDetailActivity.this._uninstallingPackage = true;
                    ProblemDetailActivity.this.startActivity(new Intent("android.intent.action.DELETE", Uri.fromParts("package", appProblem.getPackageName(), null)));
                }
            });
            this.bt_trust_app.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new AlertDialog.Builder(ProblemDetailActivity.this).setTitle(ProblemDetailActivity.this.getString(R.string.warning)).setMessage(ProblemDetailActivity.this.getString(R.string.dialog_trust_app)).setPositiveButton(ProblemDetailActivity.this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            UserWhiteList userWhiteList = ProblemDetailActivity.this.monitorShieldService.getUserWhiteList();
                            userWhiteList.addItem(appProblem);
                            userWhiteList.writeToJSON();
                            MenacesCacheSet menacesCacheSet = ProblemDetailActivity.this.monitorShieldService.getMenacesCacheSet();
                            menacesCacheSet.removeItem(appProblem);
                            menacesCacheSet.writeToJSON();
                            ProblemDetailActivity.this.bt_trust_app.setEnabled(false);
                            ProblemDetailActivity.this.sendResult();
                            ProblemDetailActivity.this.finish();
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
                }
            });
            Drawable s = Utils.getIconFromPackage(appProblem.getPackageName(), this);
            setTitle(Utils.getAppNameFromPackage(this, appProblem.getPackageName()));
            this.iv_icon_app.setImageDrawable(s);
            this.tv_app_name.setText(Utils.getAppNameFromPackage(this, appProblem.getPackageName()));
            return;
        }
        this.ll_layout_for_app.setVisibility(8);
        this.ll_layout_for_system.setVisibility(0);
        final SystemProblem systemProblem = (SystemProblem) this._problem;
        this.iv_icon_app.setImageDrawable(systemProblem.getIcon(this));
        setTitle(systemProblem.getTitle(this));
        this.bt_open_setting.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                systemProblem.doAction(ProblemDetailActivity.this);
            }
        });
        this.bt_ignore_setting.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                UserWhiteList _userWhiteList = ProblemDetailActivity.this.monitorShieldService.getUserWhiteList();
                _userWhiteList.addItem(ProblemDetailActivity.this._problem);
                _userWhiteList.writeToJSON();
                MenacesCacheSet menaceCacheSet = ProblemDetailActivity.this.monitorShieldService.getMenacesCacheSet();
                menaceCacheSet.removeItem(ProblemDetailActivity.this._problem);
                menaceCacheSet.writeToJSON();
                ProblemDetailActivity.this.sendResult();
                ProblemDetailActivity.this.finish();
            }
        });
    }

    private void initForResume() {
        if (this.monitorShieldService == null) {
            return;
        }
        AppProblem appProblem;
        MenacesCacheSet menacesCacheSet;
        if (this._uninstallingPackage) {
            if (this._problem != null) {
                appProblem = (AppProblem) this._problem;
                if (!Utils.isPackageInstalled(this, appProblem.getPackageName())) {
                    menacesCacheSet = this.monitorShieldService.getMenacesCacheSet();
                    menacesCacheSet.removeItem( appProblem);
                    menacesCacheSet.writeToJSON();
                    sendResult();
                    this._uninstallingPackage = false;
                    finish();
                }
            }
        } else if (this._problem.getType() == ProblemType.AppProblem) {
            appProblem = (AppProblem) this._problem;
            if (!ProblemsDataSetTools.checkIfPackageInCollection(appProblem.getPackageName(), this.monitorShieldService.getMenacesCacheSet().getSet()) && !Utils.isPackageInstalled(this, appProblem.getPackageName())) {
                menacesCacheSet = this.monitorShieldService.getMenacesCacheSet();
                menacesCacheSet.removeItem( appProblem);
                menacesCacheSet.writeToJSON();
                finish();
            }
        } else if (this._problem.getType() == ProblemType.SystemProblem) {
            SystemProblem systemProblem = (SystemProblem) this._problem;
            if (!systemProblem.problemExists(this)) {
                menacesCacheSet = this.monitorShieldService.getMenacesCacheSet();
                menacesCacheSet.removeItem( systemProblem);
                menacesCacheSet.writeToJSON();
                sendResult();
                finish();
            }
        }
    }

    public void onResume() {
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
        initForResume();

    }

    private void sendResult() {
        Intent intent = new Intent();
        intent.putExtra("is_trust", true);
        setResult(3, intent);
    }

    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }


        super.onPause();
    }

    protected void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        if (this.bound && this.monitorShieldService != null) {
            unbindService(this.serviceConnection);
            this.bound = false;
        }
        if (this.mAdView != null) {
            this.mAdView.destroy();
        }
        super.onDestroy();
    }
}
