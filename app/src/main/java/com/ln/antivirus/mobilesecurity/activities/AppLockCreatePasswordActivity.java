package com.ln.antivirus.mobilesecurity.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ln.antivirus.mobilesecurity.adapter.QuestionSpinnerAdapter;
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

public class AppLockCreatePasswordActivity extends BaseToolbarActivity {
    public static final String KEY_ANSWER = "answer";
    public static final String KEY_APPLOCKER_SERVICE = "applocker_service";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_RELOCK_POLICY = "relock_policy";
    public static final String KEY_RELOCK_TIMEOUT = "timeout";
    public static final String SHARED_PREFERENCES_NAME = "App_Lock_Settings";
    @BindView(R.id.done)
    TextView done;
    @BindView(R.id.edt_answer)
    EditText edt_answer;
    @BindView(R.id.la_password)
    View la_password;
    @BindView(R.id.la_password_again)
    View la_password_again;
    @BindView(R.id.la_question)
    View la_question;
    @BindView(R.id.lock_view)
    Lock9View lock_view;
    @BindView(R.id.lock_view_again)
    Lock9View lock_view_again;
    private String password;
    private String passwordAgain;
    private SharedPreferences sharedPreferences;
    @BindView(R.id.spinner_question)
    Spinner spinner_question;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.title_1)
    TextView title_1;
    @BindView(R.id.title_2)
    TextView title_2;
    @BindView(R.id.title_again)
    TextView title_again;

    private StartAppNativeAd startAppNativeAd;
    private NativeAdDetails nativeAd;
    private StartAppAd startAppAd;

    private AdRequest adRequest ;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int random_no=0;



    private void customFont() {
        TypeFaceUttils.setNomal((Context) this, this.title);
        TypeFaceUttils.setNomal((Context) this, this.title_again);
        TypeFaceUttils.setNomal((Context) this, this.title_1);
        TypeFaceUttils.setNomal((Context) this, this.title_2);
        TypeFaceUttils.setNomal((Context) this, this.done);
    }

    public int getLayoutId() {
        return R.layout.activity_app_lock_create_password;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customFont();
        this.sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
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
                nativeAd.sendImpression(AppLockCreatePasswordActivity.this);

            }
        }

        @Override
        public void onFailedToReceiveAd(Ad ad) {

            // Error occurred while loading the native ad
        }
    };

    private void initView() {
        this.lock_view.setCallBack(new CallBack() {
            public void onFinish(String password) {
                AppLockCreatePasswordActivity.this.password = password;
                if (AppLockCreatePasswordActivity.this.password != null) {
                    AppLockCreatePasswordActivity.this.la_password.setVisibility(8);
                    AppLockCreatePasswordActivity.this.la_password_again.setVisibility(0);
                }
            }
        });
        this.lock_view_again.setCallBack(new CallBack() {
            public void onFinish(String password) {
                AppLockCreatePasswordActivity.this.passwordAgain = password;
                if (AppLockCreatePasswordActivity.this.password.equals(AppLockCreatePasswordActivity.this.passwordAgain)) {
                    AppLockCreatePasswordActivity.this.la_password_again.setVisibility(8);
                    AppLockCreatePasswordActivity.this.la_question.setVisibility(0);
                    return;
                }
                Snackbar.make(AppLockCreatePasswordActivity.this.la_password_again, R.string.patterns_do_not_match, -1).show();
            }
        });
        this.spinner_question.setAdapter(new QuestionSpinnerAdapter(this, getResources().getStringArray(R.array.question_arrays)));
        this.done.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AppLockCreatePasswordActivity.this.actionDone();
            }
        });
        this.edt_answer.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 6) {
                    AppLockCreatePasswordActivity.this.actionDone();
                }
                return false;
            }
        });
    }

    private void actionDone() {
        if (this.edt_answer.getText().toString().length() == 0) {
            Snackbar.make(this.la_password_again, R.string.answer_is_not_empty, -1).show();
            return;
        }
        Editor editor = this.sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, this.password);
        editor.putString(KEY_QUESTION, this.spinner_question.getSelectedItem().toString());
        editor.putString(KEY_ANSWER, this.edt_answer.getText().toString());
        editor.apply();
        startActivity(new Intent(this, AppLockHomeActivity.class));
        finish();
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
