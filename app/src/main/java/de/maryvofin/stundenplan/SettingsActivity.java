package de.maryvofin.stundenplan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.maryvofin.stundenplan.database.Database;

/**
 * Created by mark on 06.10.2015.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.activity_title_settings));

        Spinner updateIntervalSpinner = (Spinner)findViewById(R.id.activity_settings_updateinterval_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.text_settings_updateinterval_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateIntervalSpinner.setAdapter(adapter);
        long updateIntervalMinutes = getSharedPreferences("update",Context.MODE_PRIVATE).getLong("interval", 60);
        if(updateIntervalMinutes == 60) {
            updateIntervalSpinner.setSelection(0);
        }
        else {
            updateIntervalSpinner.setSelection(1);
        }

        updateIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences("update", Context.MODE_PRIVATE).edit();
                editor.putLong("interval",(position == 0)? 60 : 60*24);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
