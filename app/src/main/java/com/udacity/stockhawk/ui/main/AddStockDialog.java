package com.udacity.stockhawk.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.databinding.AddStockDialogBinding;

public class AddStockDialog extends DialogFragment {

    AddStockDialogBinding mBinding;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.add_stock_dialog, null, false);

        mBinding.stock.setOnEditorActionListener((v, actionId, event) -> {
            addStock();
            return true;
        });
        builder.setView(mBinding.getRoot());

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add), (dialog, id) -> addStock());
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {
        Activity parent = getActivity();
        if (parent instanceof MainActivity) {
            ((MainActivity) parent).addStock(mBinding.stock.getText().toString());
        }
        dismissAllowingStateLoss();
    }


}
