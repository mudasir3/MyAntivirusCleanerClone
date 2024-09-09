package com.ln.antivirus.mobilesecurity.model;

import android.content.Context;

import com.ln.antivirus.mobilesecurity.iface.IProblem;

public class MenacesCacheSet extends JSONDataSet<IProblem> {
    public MenacesCacheSet(Context context) {
        super(context, "menacescache.json", new ProblemFactory());
    }
}
