package com.udacity.stockhawk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class AppUtils {

    private final DecimalFormat mDollarFormatWithPlus;
    private final DecimalFormat mDollarFormat;
    private final DecimalFormat mPercentageFormat;

    public DecimalFormat getDollarFormatWithPlus() {
        return mDollarFormatWithPlus;
    }

    public DecimalFormat getDollarFormat() {
        return mDollarFormat;
    }

    public DecimalFormat getPercentageFormat() {
        return mPercentageFormat;
    }

    public boolean isConnected(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Private constructor for Singleton pattern (Bill Pugh's).
     *
     * @see <a href="https://goo.gl/2RfcWb">Singleton Design Pattern Best Practices</a>
     */
    private AppUtils() {
        super();
        mDollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus.setPositivePrefix("+$");
        mPercentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        mPercentageFormat.setMaximumFractionDigits(2);
        mPercentageFormat.setMinimumFractionDigits(2);
        mPercentageFormat.setPositivePrefix("+");
    }

    private static class AppUtilsHolder {
        static final AppUtils INSTANCE = new AppUtils();
    }

    public static AppUtils getInstance() {
        return AppUtilsHolder.INSTANCE;
    }
}
