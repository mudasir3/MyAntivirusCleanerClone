package com.ln.antivirus.mobilesecurity.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ln.antivirus.mobilesecurity.model.AppData;
import com.ln.antivirus.mobilesecurity.service.MonitorShieldService;
import com.ln.antivirus.mobilesecurity.util.TypeFaceUttils;
import com.ln.antivirus.mobilesecurity.util.Utils;
import com.mikepenz.aboutlibraries.util.UIUtils;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.studioninja.antivirus.mobilesecurity.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.studioninja.antivirus.mobilesecurity.R.id.adView;


public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    @BindView(R.id.bg_animation_scan)
    ImageView bg_animation_scan;
    boolean bound = false;
    @BindView(R.id.img_resolvep_roblems)
    ImageView img_resolvep_roblems;
    @BindView(R.id.iv_start_scan_anim)
    ImageView iv_start_scan;
    private MonitorShieldService monitorShieldService;
    @BindView(R.id.noti_danger)
    View noti_danger;
    @BindView(R.id.notifi_safe)
    View notifi_safe;
    @BindView(R.id.img_threat)
    ImageView img_threat;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private MiniDrawer miniResult = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.this.bound = true;
            MainActivity.this.monitorShieldService = ((MonitorShieldService.MonitorShieldLocalBinder) service).getServiceInstance();
            MainActivity.this.initView();
        }

        public void onServiceDisconnected(ComponentName name) {
            MainActivity.this.monitorShieldService = null;
            MainActivity.this.bound = false;
        }
    };
    @BindView(R.id.tv_app_system)
    TextView tv_app_system;
    @BindView(R.id.tv_danger)
    TextView tv_danger;
    @BindView(R.id.tv_first_run)
    TextView tv_first_run;
    @BindView(R.id.tv_found_problem)
    TextView tv_found_problem;

    @BindView(R.id.tv_safe)
    TextView tv_safe;
    @BindView(R.id.tv_scan)
    TextView tv_scan;
    private View view;
    SharedPreferences appLockSettings;

    AdRequest adRequest;
    AdView adView;

    @OnClick({R.id.bg_animation_scan})
    void onStartScan() {
        startActivity(new Intent(this, ScanningActivity.class));
    }

    @OnClick({R.id.img_threat})
    void onPhoneInfo() {
        startActivity(new Intent(this, PhoneInfoActivity.class));
    }

    @OnClick({R.id.img_privacy})
    void onStartAppLock() {
        if (VERSION.SDK_INT >= 22 && !Utils.isUsageAccessEnabled(MainActivity.this)) {
            Utils.openUsageAccessSetings(MainActivity.this);
        } else if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(MainActivity.this)) {
            MainActivity.this.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + MainActivity.this.getPackageName())));
        } else if (Utils.isAccessibilitySettingsOn(MainActivity.this)) {
            if (appLockSettings.getString(AppLockCreatePasswordActivity.KEY_PASSWORD, null) != null) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, AppLockScreenActivity.class));
            } else {
                MainActivity.this.startActivity(new Intent(MainActivity.this, AppLockCreatePasswordActivity.class));
            }
        } else {
            MainActivity.this.startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.getWindow().setType(2003);
            dialog.requestWindowFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(R.layout.dialog_guide_accessibility);
            dialog.show();
        }
    }

    @OnClick({R.id.img_booster})
    void onRate() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(intent);
    }

    private void customFont() {
        TypeFaceUttils.setNomal((Context) this, this.tv_scan);
        TypeFaceUttils.setNomal((Context) this, this.tv_app_system);
        //  TypeFaceUttils.setNomal((Context) this, this.tv_storage);
        // TypeFaceUttils.setNomal((Context) this, this.tv_memory);
        // TypeFaceUttils.setNomal((Context) this, this.tv_percent_storage);
        // TypeFaceUttils.setNomal((Context) this, this.tv_percent_memory);
        //  TypeFaceUttils.setNomal((Context) this, this.tv_info_storage);
        //  TypeFaceUttils.setNomal((Context) this, this.tv_info_memory);
        TypeFaceUttils.setNomal((Context) this, this.tv_first_run);
        TypeFaceUttils.setNomal((Context) this, this.tv_safe);
        TypeFaceUttils.setNomal((Context) this, this.tv_danger);
        TypeFaceUttils.setNomal((Context) this, this.tv_found_problem);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_bar_main);

        appLockSettings = getSharedPreferences(AppLockCreatePasswordActivity.SHARED_PREFERENCES_NAME, 0);

        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= 23){
            if(!Settings.canDrawOverlays(this)){

            }
        }
        adRequest = new AdRequest.Builder().build();
        adView = (AdView) findViewById(R.id.adView);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }
        });

        // call first method...
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#27ae60")));

        final IProfile profile2 = new ProfileDrawerItem().withIcon(R.drawable.user_icon);

//        .withDividerBelowHeader(false)
//                .withOnlySmallProfileImagesVisible(true)
//                .withResetDrawerOnProfileListClick(false)
//                .withSelectionFistLineShown(false)
//                .withCurrentProfileHiddenInList(true)
//                .withOnlyMainProfileImageVisible(true)
//                .withResetDrawerOnProfileListClick(false)
//                .withAlternativeProfileHeaderSwitching(false)
//                .withCompactStyle(false)
//                .withSelectionListEnabledForSingleProfile(false)


        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)

                .withTranslucentStatusBar(false)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile2)
                .build();




        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        //  final IProfile profile = new ProfileDrawerItem().withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");
//        final IProfile profile2 = new ProfileDrawerItem().withName("Bernat Borras").withEmail("alorma@github.com").withIcon(Uri.parse("https://avatars3.githubusercontent.com/u/887462?v=3&s=460"));


//Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerLayout(R.layout.crossfade_material_drawer)
                .withHasStableIds(true)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.Home).withIcon(R.drawable.homee).withIdentifier(1),


                        new PrimaryDrawerItem().withName(R.string.IgnoredList).withIcon(R.drawable.ignoredlist).withIdentifier(3),

                        new PrimaryDrawerItem().withName(R.string.InfosDevice).withIcon(R.drawable.infosdevice).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.Privacy).withIcon(R.drawable.privacyicone).withIdentifier(5),

                        new PrimaryDrawerItem().withName(R.string.Share).withIcon(R.drawable.sharee).withIdentifier(6),
                        new PrimaryDrawerItem().withName(R.string.Rate).withIcon(R.drawable.ratee).withIdentifier(7)
                        // new DividerDrawerItem(),
                        //  new SecondaryDrawerItem().withName(R.string.drawer_item_seventh).withIcon(FontAwesome.Icon.faw_github).withIdentifier(7).withSelectable(false)
                ) // add the items we want to use with our Drawer
                .withSelectedItemByPosition(1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();


                        if (drawerItem.getIdentifier() == 1) {
                            // Toast.makeText(MainActivity.this,"2",Toast.LENGTH_LONG).show();


                            result.closeDrawer();




                        }else if (drawerItem.getIdentifier() == 3) {

                            // Toast.makeText(MainActivity.this,"2",Toast.LENGTH_LONG).show();
                            Intent browserIntent2 = new Intent(MainActivity.this, IgnoredListActivity.class);
                            startActivity(browserIntent2);
                            result.closeDrawer();

                        } else if (drawerItem.getIdentifier() == 4) {
                            // Toast.makeText(MainActivity.this,"2",Toast.LENGTH_LONG).show();
                            Intent browserIntent2 = new Intent(MainActivity.this, PhoneInfoActivity.class);
                            startActivity(browserIntent2);
                            result.closeDrawer();

                        } else if (drawerItem.getIdentifier() == 5) {
                            // Toast.makeText(MainActivity.this,"2",Toast.LENGTH_LONG).show();
                            Intent browserIntent2 = new Intent(MainActivity.this, privacy.class);
                            startActivity(browserIntent2);
                            result.closeDrawer();


                        } else if (drawerItem.getIdentifier() == 6) {

                            Intent myapp = new Intent(Intent.ACTION_SEND);
                            myapp.setType("text/plain");
                            myapp.putExtra(Intent.EXTRA_TEXT, "Hey my friend check out this app\n https://play.google.com/store/apps/details?id="+ getPackageName() +" \n");
                            startActivity(myapp);
                            result.closeDrawer();


                        }else if (drawerItem.getIdentifier() == 7) {
                            // Toast.makeText(MainActivity.this,"3",Toast.LENGTH_LONG).show();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                            }

                            result.closeDrawer();
                        }
//
//

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(false)
                .build();



        //get out our drawerLyout
        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
        //add second view (which is the miniDrawer)
        MiniDrawer miniResult = result.getMiniDrawer();
        //build the view for the MiniDrawer
        View view = miniResult.build(this);
        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });
//hook to the crossfade event
        crossfadeDrawerLayout.withCrossfadeListener(new CrossfadeDrawerLayout.CrossfadeListener() {
            @Override
            public void onCrossfade(View containerView, float currentSlidePercentage, int slideOffset) {
                Log.e("CrossfadeDrawerLayout", "crossfade: " + currentSlidePercentage + " - " + slideOffset);
            }
        });

        //  TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        //  mTitle.setText(R.string.app_name);

        customFont();
        this.view = findViewById(R.id.background);
        this.iv_start_scan = (ImageView) this.view.findViewById(R.id.iv_start_scan_anim);
        this.iv_start_scan.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation));
        this.bg_animation_scan.setAnimation(AnimationUtils.loadAnimation(this, R.anim.bg_animation_scan));
        this.img_resolvep_roblems.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, ResultActivity.class));
            }
        });
    }

    protected void onResume() {
        super.onResume();
        Utils.getCacheSize(this);
        getMemoryInfo();
        Intent i = new Intent(this, MonitorShieldService.class);
        if (!Utils.isServiceRunning(this, MonitorShieldService.class)) {
            startService(i);
        }
        bindService(i, this.serviceConnection, 1);
        if (getSharedPreferences("Settings", 0).getInt("rate", 0) == 1) {
            showRateDialog();
        }
    }



    private void getMemoryInfo() {
//        long totalInternalMemorySize = Utils.getTotalInternalMemorySize();
//        long availableInternalMemorySize = Utils.getAvailableInternalMemorySize();
//        this.tv_info_storage.setText(Utils.formatSize(totalInternalMemorySize - availableInternalMemorySize) + "/" + Utils.formatSize(totalInternalMemorySize));
//        int percent_storage = (int) (((totalInternalMemorySize - availableInternalMemorySize) * 100) / totalInternalMemorySize);
//        this.tv_percent_storage.setText(String.valueOf(percent_storage + "%"));
//        long freeRam = Utils.getFreeRAM(this);
//        long totalRam = Utils.getTotalRAM(this);
//        this.tv_info_memory.setText(Utils.formatSize(totalRam - freeRam) + "/" + Utils.formatSize(totalRam));
//        int percent_memory = (int) (((totalRam - freeRam) * 100) / totalRam);
//        this.tv_percent_memory.setText(String.valueOf(percent_memory) + "%");
//        this.pb_storage.setSmoothPercent((float) (((double) percent_storage) / 100.0d));
//        this.pb_memory.setSmoothPercent((float) (((double) percent_memory) / 100.0d));
    }

    private void showRateDialog() {
        getSharedPreferences("Settings", 0).edit().putInt("rate", 2).apply();
        new ExitDialog(this, 0x1030071);
    }

    public void setBackgroungDanger() {
        this.view.setBackgroundResource(R.drawable.background_danger);
    }

    public void setBackgroungSafe() {
        this.view.setBackgroundResource(R.drawable.settings_background);
    }

    public MonitorShieldService getMonitorShieldService() {
        return this.monitorShieldService;
    }

    private void initView() {
        if (!AppData.getInstance(this).getFirstScanDone()) {
            showFirstScan();
        } else if (this.monitorShieldService.getMenacesCacheSet().getItemCount() == 0) {
            showSafe();
        } else {
            showDanger();
        }
    }

    private void showFirstScan() {
        this.tv_first_run.setVisibility(0);
        this.noti_danger.setVisibility(4);
        this.notifi_safe.setVisibility(4);
    }

    private void showSafe() {
        this.tv_first_run.setVisibility(4);
        this.noti_danger.setVisibility(4);
        this.notifi_safe.setVisibility(0);
        setBackgroungSafe();
    }

    private void showDanger() {
        this.tv_first_run.setVisibility(4);
        this.notifi_safe.setVisibility(4);
        this.noti_danger.setVisibility(0);
        setBackgroungDanger();
        this.tv_found_problem.setText(this.monitorShieldService.getMenacesCacheSet().getItemCount() + " " + getResources().getString(R.string.problem_found));
    }

    public void onStop() {
        super.onStop();
        if (this.bound && this.monitorShieldService != null) {
            unbindService(this.serviceConnection);
            this.bound = false;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        //   AppEventsLogger.deactivateApp(this);
    }
}
