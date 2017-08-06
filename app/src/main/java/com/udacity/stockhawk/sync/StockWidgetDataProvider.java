package com.udacity.stockhawk.sync;

import android.content.Context;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.util.AppUtils;

import java.lang.ref.WeakReference;

public class StockWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private WeakReference<Context> mContext;
    private Cursor mCursor;

    public StockWidgetDataProvider(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    private void initCursor() {
        if (mCursor != null) {
            mCursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.get().getContentResolver().query(Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onCreate() {
        initCursor();
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
    }

    @Override
    public void onDataSetChanged() {
        initCursor();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int idx) {
        mCursor.moveToPosition(idx);
        final RemoteViews remoteViews = new RemoteViews(mContext.get().getPackageName(), R.layout.list_item_quote);
        remoteViews.setTextViewText(R.id.symbol, mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
        remoteViews.setTextViewText(R.id.price, mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)));

        float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
        if (rawAbsoluteChange > 0) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        final String change = AppUtils.getInstance().getDollarFormatWithPlus().format(rawAbsoluteChange);
        final String percentage = AppUtils.getInstance().getPercentageFormat().format(percentageChange / 100);
        if (PrefUtils.getDisplayMode(mContext.get()).equals(mContext.get().getString(R.string.pref_display_mode_absolute_key))) {
            remoteViews.setTextViewText(R.id.change, change);
        } else {
            remoteViews.setTextViewText(R.id.change, percentage);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
