package de.maryvofin.stundenplan.app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import com.rey.material.widget.Spinner;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Task;

public class AddTaskActivity extends AppCompatActivity {

    DateFormat dateFormat = DateFormat.getDateInstance();
    DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);




    Task task = null;
    boolean newTask = false;

    ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Bundle args = getIntent().getExtras();
        Object ser = null;
        if(args != null) ser = args.getSerializable("task");
        if(ser != null) {
            task = (Task)ser;
        }
        else {
            task = new Task();
            task.completed = false;
            task.deadline = System.currentTimeMillis();
            task.description = "";
            task.entryReference = 0;
            task.estimatedDuration = Task.durations[0];
            task.text = "";
            newTask = true;
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.text_task));
        if (task.entryReference != 0) {
            PlanEntry entry = Database.getInstance().getEntryFromId(this,task.entryReference);
            if(entry != null) {
                setTitle(getTitle()+": "+entry.getEventName());
            }
        }

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.durations, android.R.layout.simple_spinner_item);
        Spinner durationSpinner = (Spinner)findViewById(R.id.spinner_duration);
        durationSpinner.setAdapter(spinnerAdapter);


        setValues();
        setListeners();


    }

    void setListeners() {
        EditText descEdit = (EditText)findViewById(R.id.text_description);
        descEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                task.description = s.toString();
            }
        });
        EditText contentEdit = (EditText)findViewById(R.id.text_content);
        contentEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                task.text = s.toString();
            }
        });

        View dateEdit = findViewById(R.id.text_deadline_date);
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
        EditText timeEdit = (EditText)findViewById(R.id.text_deadline_time);
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        Spinner durationSpinner = (Spinner)findViewById(R.id.spinner_duration);
        durationSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner spinner, View view, int i, long l) {
                task.estimatedDuration = Task.durations[i];
            }
        });
    }

    void selectDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(task.deadline);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                task.deadline = cal.getTimeInMillis();
                setValues();
            }
        },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        dpd.show();

    }

    void selectTime() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(task.deadline);

        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
                cal.set(Calendar.MINUTE,minute);
                task.deadline = cal.getTimeInMillis();
                setValues();
            }
        },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true);
        tpd.show();
    }

    void setValues() {

        EditText descEdit = (EditText)findViewById(R.id.text_description);
        descEdit.setText(task.description);

        EditText contentEdit = (EditText)findViewById(R.id.text_content);
        contentEdit.setText(task.text);

        EditText dateEdit = (EditText)findViewById(R.id.text_deadline_date);
        dateEdit.setText(dateFormat.format(new Date(task.deadline)));

        EditText timeEdit = (EditText)findViewById(R.id.text_deadline_time);
        timeEdit.setText(timeFormat.format(new Date(task.deadline)));

        Spinner durationSpinner = (Spinner)findViewById(R.id.spinner_duration);
        for(int i=0;i<Task.durations.length;i++) {
            if (task.estimatedDuration == Task.durations[i]) {
                durationSpinner.setSelection(i);
                break;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_task,menu);

        menu.findItem(R.id.action_confirm).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_done).color(Color.WHITE).actionBar());
        menu.findItem(R.id.action_abort).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_close).color(Color.WHITE).actionBar());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                if(newTask) {
                    task.save();
                }
                else {
                    task.update();
                }
                finish();
                return true;
            case R.id.action_abort:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
