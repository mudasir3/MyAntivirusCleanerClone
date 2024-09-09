package com.ln.antivirus.mobilesecurity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.antivirus.mobilesecurity.iface.IProblem;
import com.ln.antivirus.mobilesecurity.model.AppProblem;
import com.ln.antivirus.mobilesecurity.model.MenacesCacheSet;
import com.ln.antivirus.mobilesecurity.model.SystemProblem;
import com.ln.antivirus.mobilesecurity.model.UserWhiteList;
import com.ln.antivirus.mobilesecurity.service.MonitorShieldService;
import com.ln.antivirus.mobilesecurity.util.TypeFaceUttils;
import com.ln.antivirus.mobilesecurity.util.Utils;
import com.studioninja.antivirus.mobilesecurity.R;

import java.util.List;


public class IgnoredAppsAdapter extends Adapter<IgnoredAppsAdapter.ViewHolder> {
    private static OnItemClickListener listener;
    private Context context;
    private List<IProblem> iProblems;
    private MonitorShieldService monitorShieldService;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public ImageView imgIconApp;
        public TextView tvAppName;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.tvAppName = (TextView) itemView.findViewById(R.id.tv_app_name);
            this.imgIconApp = (ImageView) itemView.findViewById(R.id.img_icon_app);
            itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (IgnoredAppsAdapter.listener != null) {
                        IgnoredAppsAdapter.listener.onItemClick(itemView, ViewHolder.this.getLayoutPosition());
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public IgnoredAppsAdapter(Context context, List<IProblem> iProblems, MonitorShieldService monitorShieldService) {
        this.iProblems = iProblems;
        this.context = context;
        this.monitorShieldService = monitorShieldService;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ignored_app, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        IProblem iProblem = (IProblem) this.iProblems.get(position);
        TextView tvName = holder.tvAppName;
        ImageView imgIcon = holder.imgIconApp;
        TypeFaceUttils.setNomal(this.context, tvName);
        if (iProblem.getType() == IProblem.ProblemType.AppProblem) {
            AppProblem appProblem = (AppProblem) iProblem;
            tvName.setText(Utils.getAppNameFromPackage(this.context, appProblem.getPackageName()));
            imgIcon.setImageDrawable(Utils.getIconFromPackage(appProblem.getPackageName(), this.context));
            return;
        }
        SystemProblem sp = (SystemProblem) iProblem;
        tvName.setText(sp.getTitle(this.context));
        imgIcon.setImageDrawable(sp.getIcon(this.context));
    }

    public int getItemCount() {
        return this.iProblems.size();
    }

    public void removeItem(int position) {
        IProblem iProblem = (IProblem) this.iProblems.get(position);
        MenacesCacheSet menaceCacheSet = this.monitorShieldService.getMenacesCacheSet();
        UserWhiteList userWhiteList = this.monitorShieldService.getUserWhiteList();
        userWhiteList.removeItem(iProblem);
        userWhiteList.writeToJSON();
        menaceCacheSet.addItem( iProblem);
        menaceCacheSet.writeToJSON();
        this.iProblems.remove(iProblem);
        notifyItemRemoved(position);
    }
}
