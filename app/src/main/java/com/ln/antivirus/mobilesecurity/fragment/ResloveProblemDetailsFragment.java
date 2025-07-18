package com.ln.antivirus.mobilesecurity.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ln.antivirus.mobilesecurity.activities.ResultActivity;
import com.ln.antivirus.mobilesecurity.adapter.WarningAdapter;
import com.ln.antivirus.mobilesecurity.iface.IProblem;
import com.ln.antivirus.mobilesecurity.iface.IProblem.ProblemType;
import com.ln.antivirus.mobilesecurity.model.AppProblem;
import com.ln.antivirus.mobilesecurity.model.MenacesCacheSet;
import com.ln.antivirus.mobilesecurity.model.SystemProblem;
import com.ln.antivirus.mobilesecurity.model.UserWhiteList;
import com.ln.antivirus.mobilesecurity.util.ProblemsDataSetTools;
import com.ln.antivirus.mobilesecurity.util.TypeFaceUttils;
import com.ln.antivirus.mobilesecurity.util.Utils;
import com.studioninja.antivirus.mobilesecurity.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResloveProblemDetailsFragment extends Fragment {
    IProblem _problem = null;
    private boolean _uninstallingPackage = false;
    private ResultActivity activity;
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
    @BindView(R.id.rv_warning_problem)
    RecyclerView rv_warning_problem;
    @BindView(R.id.tv_app_name)
    TextView tv_app_name;

    private void customFont() {
        TypeFaceUttils.setNomal(getActivity(), this.tv_app_name);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reslove_problem_details, container, false);
        ButterKnife.bind(this, view);
        customFont();
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.activity = (ResultActivity) getActivity();
        if (this.activity.getMonitorShieldService() == null) {
            getActivity().getSupportFragmentManager().popBackStack();
            return;
        }
        this._problem = this.activity.getComu();
        this.rv_warning_problem.setAdapter(new WarningAdapter(getActivity(), this._problem));
        this.rv_warning_problem.setLayoutManager(new LinearLayoutManager(getActivity()));
        init();
    }

    public void onResume() {
        super.onResume();
        initForResume();
    }

    private void init() {
        if (this._problem.getType() == ProblemType.AppProblem) {
            this.ll_layout_for_app.setVisibility(0);
            this.ll_layout_for_system.setVisibility(8);
            final AppProblem appProblem = (AppProblem) this._problem;
            this.bt_uninstall_app.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ResloveProblemDetailsFragment.this._uninstallingPackage = true;
                    ResloveProblemDetailsFragment.this.startActivity(new Intent("android.intent.action.DELETE", Uri.fromParts("package", appProblem.getPackageName(), null)));
                }
            });
            this.bt_trust_app.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new Builder(ResloveProblemDetailsFragment.this.getActivity(), R.style.MyAlertDialogStyle).setTitle(ResloveProblemDetailsFragment.this.getString(R.string.warning)).setMessage(ResloveProblemDetailsFragment.this.getString(R.string.dialog_trust_app)).setPositiveButton(ResloveProblemDetailsFragment.this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            UserWhiteList userWhiteList = ResloveProblemDetailsFragment.this.activity.getMonitorShieldService().getUserWhiteList();
                            userWhiteList.addItem(appProblem);
                            userWhiteList.writeToJSON();
                            MenacesCacheSet menacesCacheSet = ResloveProblemDetailsFragment.this.activity.getMonitorShieldService().getMenacesCacheSet();
                            menacesCacheSet.removeItem(appProblem);
                            menacesCacheSet.writeToJSON();
                            ResloveProblemDetailsFragment.this.bt_trust_app.setEnabled(false);
                            ResloveProblemDetailsFragment.this.sendResult(ResloveProblemDetailsFragment.this._problem);
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
                }
            });
            Drawable s = Utils.getIconFromPackage(appProblem.getPackageName(), getActivity());
            getActivity().setTitle(Utils.getAppNameFromPackage(getActivity(), appProblem.getPackageName()));
            this.iv_icon_app.setImageDrawable(s);
            this.tv_app_name.setText(Utils.getAppNameFromPackage(getActivity(), appProblem.getPackageName()));
            return;
        }
        this.ll_layout_for_app.setVisibility(8);
        this.ll_layout_for_system.setVisibility(0);
        final SystemProblem systemProblem = (SystemProblem) this._problem;
        this.iv_icon_app.setImageDrawable(systemProblem.getIcon(getActivity()));
        this.tv_app_name.setText(systemProblem.getTitle(getActivity()));
        this.bt_open_setting.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                systemProblem.doAction(ResloveProblemDetailsFragment.this.getActivity());
            }
        });
        this.bt_ignore_setting.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                UserWhiteList _userWhiteList = ResloveProblemDetailsFragment.this.activity.getMonitorShieldService().getUserWhiteList();
                _userWhiteList.addItem(ResloveProblemDetailsFragment.this._problem);
                _userWhiteList.writeToJSON();
                MenacesCacheSet menaceCacheSet = ResloveProblemDetailsFragment.this.activity.getMonitorShieldService().getMenacesCacheSet();
                menaceCacheSet.removeItem(ResloveProblemDetailsFragment.this._problem);
                menaceCacheSet.writeToJSON();
                ResloveProblemDetailsFragment.this.sendResult(ResloveProblemDetailsFragment.this._problem);
            }
        });
    }

    private void initForResume() {
        AppProblem appProblem;
        MenacesCacheSet menacesCacheSet;
        if (this._uninstallingPackage) {
            if (this._problem != null) {
                appProblem = (AppProblem) this._problem;
                if (!Utils.isPackageInstalled(getActivity(), appProblem.getPackageName())) {
                    menacesCacheSet = this.activity.getMonitorShieldService().getMenacesCacheSet();
                    menacesCacheSet.removeItem( appProblem);
                    menacesCacheSet.writeToJSON();
                    sendResult(this._problem);
                    this._uninstallingPackage = false;
                }
            }
        } else if (this._problem.getType() == ProblemType.AppProblem) {
            appProblem = (AppProblem) this._problem;
            if (!ProblemsDataSetTools.checkIfPackageInCollection(appProblem.getPackageName(), this.activity.getMonitorShieldService().getMenacesCacheSet().getSet()) && !Utils.isPackageInstalled(getActivity(), appProblem.getPackageName())) {
                menacesCacheSet = this.activity.getMonitorShieldService().getMenacesCacheSet();
                menacesCacheSet.removeItem(appProblem);
                menacesCacheSet.writeToJSON();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } else if (this._problem.getType() == ProblemType.SystemProblem) {
            SystemProblem systemProblem = (SystemProblem) this._problem;
            if (!systemProblem.problemExists(getActivity())) {
                menacesCacheSet = this.activity.getMonitorShieldService().getMenacesCacheSet();
                menacesCacheSet.removeItem(systemProblem);
                menacesCacheSet.writeToJSON();
                sendResult(this._problem);
            }
        }
    }

    private void sendResult(IProblem iProblem) {
        this.activity.refresh(iProblem);
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
