package de.maryvofin.stundenplan.app;

import android.app.ProgressDialog;

/**
 * Created by mark on 01.10.2015.
 */
public class UpdateSet {

    private MainActivity activity;
    private ProgressDialog dialog;
    private boolean abortOnError = true;

    public MainActivity getActivity() {
        return activity;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public UpdateSet(MainActivity activity, ProgressDialog dialog) {
        this.activity = activity;
        this.dialog = dialog;
    }

    public void setAbortOnError(boolean abortOnError) {
        this.abortOnError = abortOnError;
    }

    public boolean isAbortOnError() {
        return abortOnError;
    }
}
