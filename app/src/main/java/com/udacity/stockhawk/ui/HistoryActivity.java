package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.components.ComponentBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.databinding.ActivityHistoryBinding;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_SYMBOL = "HistoryActivity.KEY_SYMBOL";

    private static final int STOCK_BY_SYMBOL_LOADER = 1;

    private String mSymbol;
    private ActivityHistoryBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_history);

        if (getIntent().hasExtra(KEY_SYMBOL)) {
            mSymbol = getIntent().getStringExtra(KEY_SYMBOL);
            super.getSupportLoaderManager().initLoader(STOCK_BY_SYMBOL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri uri = Contract.Quote.makeUriForStock(mSymbol);
        final String[] projection = Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{});
        final String selection = Contract.Quote.COLUMN_SYMBOL;
        final String[] selectionArgs = {mSymbol};
        final String sortOrder = Contract.Quote.COLUMN_SYMBOL;
        return new CursorLoader(this, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            this.bindChart(data);
        } else {
            Timber.d("No data for %s symbol", mSymbol);
        }
    }

    private void bindChart(Cursor data) {
        data.moveToFirst();
        final String quoteHistory = data.getString(Contract.Quote.POSITION_HISTORY);
        final String[] historyData = quoteHistory.split(QuoteSyncJob.HISTORY_BREAK);

        final List<Entry> entries = new LinkedList<>();
        for (final String history : historyData) {
            final String[] historyDataValues = history.split(QuoteSyncJob.HISTORY_SEPARATOR);
            final Float valueX = Float.valueOf(historyDataValues[0]);
            final Float valueY = Float.valueOf(historyDataValues[1]);
            entries.add(new Entry(valueX, valueY));
        }

        final LineDataSet dataSet = new LineDataSet(entries, mSymbol);
        mBinding.chart.setData(new LineData(dataSet));
        mBinding.chart.setDescription(null);

        final YAxis yAxisRight = mBinding.chart.getAxisRight();
        final YAxis yAxisLeft = mBinding.chart.getAxisLeft();
        final XAxis xAxis = mBinding.chart.getXAxis();
        final Legend legend = mBinding.chart.getLegend();

        this.changeColor(android.R.color.primary_text_dark, yAxisLeft, yAxisRight, xAxis, legend);

        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter((value, axis) -> "");

        mBinding.chart.invalidate();
    }

    private void changeColor(int colorRes, ComponentBase... components) {
        final int color = ContextCompat.getColor(this, colorRes);
        for (final ComponentBase component : components) {
            component.setTextColor(color);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("Loader %d reset.", loader.getId());
    }
}
