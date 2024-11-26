package org.example.controller;

import org.example.View.SimulatorView;
import org.example.framework.Engine;
import org.example.framework.Trace;
import org.example.model.MyEngine;

public class SimulatorController {
    private SimulatorView view;
    private Engine engine;

    public SimulatorController(SimulatorView view) {
        this.view = view;
    }

    public void startSimulation() {
        Trace.setTraceLevel(Trace.Level.INFO);
        engine = new MyEngine();
        engine.setSimulationTime(1000);
        new Thread(() -> {
            engine.run();
        }).start();
    }
}
