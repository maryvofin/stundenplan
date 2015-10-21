package de.maryvofin.stundenplan.app;

import android.content.Context;
import android.os.AsyncTask;

import de.maryvofin.stundenplan.app.parser.Parser;

import static android.content.SharedPreferences.*;

/**
 * Created by mark on 01.10.2015.
 */
public class Updater extends AsyncTask<UpdateSet,Boolean,Boolean> {

    UpdateSet set;

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(set.getDialog()!=null) set.getDialog().dismiss();
        set.getActivity().setLastUpdateText(false);
    }

    @Override
    protected Boolean doInBackground(UpdateSet... params) {
        set = params[0];
        Parser parser = new Parser(set.getActivity());

        while(parser.getSemesters().isEmpty() || (set.isAbortOnError() && parser.hasError())) {
            parser.parse();
        }

        if(!parser.hasError()) {
            Editor editor = set.getActivity().getSharedPreferences("update", Context.MODE_PRIVATE).edit();
            editor.putLong("lastupdate",System.currentTimeMillis());
            editor.apply();
        }

        return !parser.hasError();
    }
}
