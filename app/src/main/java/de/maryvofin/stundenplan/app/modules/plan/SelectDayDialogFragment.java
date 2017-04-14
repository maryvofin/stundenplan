package de.maryvofin.stundenplan.app.modules.plan;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.utils.ViewPager;

public class SelectDayDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    public static String BUNDLEKEY_DAY = "day";
    public static String BUNDLEKEY_MONTH = "month";
    public static String BUNDLEKEY_YEAR = "year";
    public static String INTENTKEY_TIME = "time";
    public static final int ACTIVITY_RESULT_KEY = 346578;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = this.getArguments().getInt(BUNDLEKEY_YEAR);
        int month = this.getArguments().getInt(BUNDLEKEY_MONTH);
        int day = this.getArguments().getInt(BUNDLEKEY_YEAR);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar nCal = Calendar.getInstance();
        nCal.set(Calendar.YEAR,year);
        nCal.set(Calendar.MONTH,monthOfYear);
        nCal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        long time = nCal.getTimeInMillis();

        Intent intent = new Intent();
        intent.putExtra(INTENTKEY_TIME, time);

        getTargetFragment().onActivityResult(ACTIVITY_RESULT_KEY, ACTIVITY_RESULT_KEY, intent);
    }
}
