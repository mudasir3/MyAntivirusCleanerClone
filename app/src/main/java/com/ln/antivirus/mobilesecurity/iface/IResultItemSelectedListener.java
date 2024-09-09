package com.ln.antivirus.mobilesecurity.iface;

import android.content.Context;
import android.widget.ImageView;

public interface IResultItemSelectedListener {
    void onItemSelected(IProblem iProblem, ImageView imageView, Context context);
}
