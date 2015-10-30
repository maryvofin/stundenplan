package de.maryvofin.stundenplan.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_task,menu);

        menu.findItem(R.id.action_confirm).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_done).color(Color.WHITE).actionBar());
        menu.findItem(R.id.action_abort).setIcon(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_close).color(Color.WHITE).actionBar());

        return super.onCreateOptionsMenu(menu);
    }
}
