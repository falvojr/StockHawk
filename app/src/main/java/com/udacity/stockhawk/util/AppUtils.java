package com.udacity.stockhawk.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class AppUtils {

    private static final AppUtils ourInstance = new AppUtils();

    public static AppUtils getInstance() {
        return ourInstance;
    }

    private final DecimalFormat mDollarFormatWithPlus;
    private final DecimalFormat mDollarFormat;
    private final DecimalFormat mPercentageFormat;

    private AppUtils() {
        mDollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus.setPositivePrefix("+$");
        mPercentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        mPercentageFormat.setMaximumFractionDigits(2);
        mPercentageFormat.setMinimumFractionDigits(2);
        mPercentageFormat.setPositivePrefix("+");
    }

    public DecimalFormat getDollarFormatWithPlus() {
        return mDollarFormatWithPlus;
    }

    public DecimalFormat getDollarFormat() {
        return mDollarFormat;
    }

    public DecimalFormat getPercentageFormat() {
        return mPercentageFormat;
    }
}
