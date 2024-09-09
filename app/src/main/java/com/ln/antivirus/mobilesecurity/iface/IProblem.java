package com.ln.antivirus.mobilesecurity.iface;

import android.content.Context;

public interface IProblem extends IJSONSerializer {

    public enum ProblemType {
        AppProblem,
        SystemProblem
    }

    ProblemType getType();

    boolean isDangerous();

    boolean problemExists(Context context);

    String getPackageName();
}
