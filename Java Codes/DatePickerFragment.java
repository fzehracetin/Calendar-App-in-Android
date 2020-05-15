package com.example.calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        int year, month, day;
        if (args != null) {
            year = args.getInt("YEAR", 1970);
            month = args.getInt("MONTH", 0);
            day = args.getInt("DAY", 1);
        }
        else {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog dialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), (DatePickerDialog.OnDateSetListener)getActivity(),
                year, month, day);
        dialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        return dialog;
    }
}
