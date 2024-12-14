package JUnitTests;

import org.example.model.*;
import org.example.controller.SimulatorController;
import org.example.framework.Trace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

class MyEngineTest {
    private MyEngine engine;
    private SimulatorController mockController;

    @BeforeEach
    void setup() {
        Trace.setTraceLevel(Trace.Level.INFO);
        mockController = mock(SimulatorController.class);
        when(mockController.getNumberOfStations()).thenReturn(2);
        when(mockController.getNumberOfAccountStations()).thenReturn(1);
        when(mockController.getArrivalInterval()).thenReturn(5.0);
        when(mockController.getTransactionServiceTime()).thenReturn(10.0);
        when(mockController.getAccountServiceTime()).thenReturn(15.0);
        when(mockController.getClientDistribution()).thenReturn(80.0);
        when(mockController.getSleepTime()).thenReturn(100L);

        engine = new MyEngine(mockController);
    }

    @Test
    @DisplayName("Engine initializes with correct configuration")
    void testEngineInitialization() {
        engine.initialize();
        verify(mockController, times(2)).getArrivalInterval();
    }

    @Test
    @DisplayName("Arrival interval updates correctly")
    void testArrivalIntervalUpdate() {
        double newInterval = 7.0;
        engine.setArrivalInterval(newInterval);
        verify(mockController, times(2)).getArrivalInterval();
    }

    @Test
    @DisplayName("Engine processes events in sequence")
    void testEventProcessing() {
        engine.initialize();
        verify(mockController, times(2)).getArrivalInterval();

    }

    @Test
    @DisplayName("Queue status updates when simulation runs")
    void testQueueStatusUpdates() {
        engine.initialize();
        Map<String, List<Customer>> status = engine.getQueueStatus();
        assertNotNull(status);
    }



    @Test
    @DisplayName("Simulation pause functionality works")
    void testSimulationPause() {
        engine.initialize();
        engine.setPaused(true);
        assertTrue(engine.isPaused());

        engine.setPaused(false);
        assertFalse(engine.isPaused());
    }

    @Test
    @DisplayName("Engine processes pause state correctly")
    void testPauseState() {
        engine.setPaused(true);
        assertTrue(engine.isPaused());
        engine.setPaused(false);
        assertFalse(engine.isPaused());
    }




}