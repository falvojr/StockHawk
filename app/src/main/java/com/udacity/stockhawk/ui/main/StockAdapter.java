package com.udacity.stockhawk.ui.main;


import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.databinding.ListItemQuoteBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private final DecimalFormat mDollarFormatWithPlus;
    private final DecimalFormat mDollarFormat;
    private final DecimalFormat mPercentageFormat;
    private Cursor mCursor;
    private final StockAdapterOnClickHandler mClickHandler;

    StockAdapter(StockAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;

        mDollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        mDollarFormatWithPlus.setPositivePrefix("+$");
        mPercentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        mPercentageFormat.setMaximumFractionDigits(2);
        mPercentageFormat.setMinimumFractionDigits(2);
        mPercentageFormat.setPositivePrefix("+");
    }

    void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(Contract.Quote.POSITION_SYMBOL);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ListItemQuoteBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_quote, parent, false);
        return new StockViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.mBinding.symbol.setText(mCursor.getString(Contract.Quote.POSITION_SYMBOL));
        holder.mBinding.price.setText(mDollarFormat.format(mCursor.getFloat(Contract.Quote.POSITION_PRICE)));

        float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            holder.mBinding.change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.mBinding.change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = mDollarFormatWithPlus.format(rawAbsoluteChange);
        String percentage = mPercentageFormat.format(percentageChange / 100);

        final View rootView = holder.itemView;
        final Context context = rootView.getContext();
        if (PrefUtils.getDisplayMode(context).equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            holder.mBinding.change.setText(change);
        } else {
            holder.mBinding.change.setText(percentage);
        }

        rootView.setOnClickListener(holder);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }
        return count;
    }

    interface StockAdapterOnClickHandler {
        void onClick(String symbol);
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ListItemQuoteBinding mBinding;

        StockViewHolder(ListItemQuoteBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }
    }
}
