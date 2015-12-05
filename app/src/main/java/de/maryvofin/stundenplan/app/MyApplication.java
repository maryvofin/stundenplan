package de.maryvofin.stundenplan.app;

import android.app.Application;
import android.content.Context;

import info.quantumflux.QuantumFlux;

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        QuantumFlux.initialize(this);
    }
}
