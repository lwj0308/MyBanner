package com.linlinlin.mybanner;

import android.content.Context;

public class DensityUtil {
    public static int dip2px(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * dip + 0.5f);
    }
}
