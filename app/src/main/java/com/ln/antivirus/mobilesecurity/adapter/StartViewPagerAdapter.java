package com.ln.antivirus.mobilesecurity.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ln.antivirus.mobilesecurity.fragment.ModifySettingFragment;

import com.ln.antivirus.mobilesecurity.fragment.StartWelcomeFragment;

import java.util.ArrayList;
import java.util.List;


public class StartViewPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    List<Fragment> fragmentList = new ArrayList();
    int pages = 3;

    public StartViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        fragmentList.add(new StartWelcomeFragment());

        fragmentList.add(new ModifySettingFragment());


    }

    public Fragment getItem(int position) {
        return (Fragment) this.fragmentList.get(position);
    }

    public void setPagesCount(int count) {
        this.pages = count;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.pages;
    }
}
