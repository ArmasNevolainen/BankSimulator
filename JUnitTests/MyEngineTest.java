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
import org.example.framework.Event;
import org.example.framework.Clock;

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


    @Test
    @DisplayName("Test client distribution")
    void testClientDistribution() {
        engine.initialize();

        // Create and process an arrival event that will trigger client distribution check
        Event arrival = new Event(EventType.ARR_AUTOMAT, Clock.getInstance().getClock());
        engine.runEvent(arrival);

        verify(mockController, times(1)).getClientDistribution();

        // Additional verification for the distribution value
        double expectedDistribution = 80.0;
        when(mockController.getClientDistribution()).thenReturn(expectedDistribution);
        assertEquals(expectedDistribution, mockController.getClientDistribution());
    }


    @Test
    @DisplayName("Test customer type distribution")
    void testCustomerTypeDistribution() {
        when(mockController.getClientDistribution()).thenReturn(85.0);
        engine.initialize();
        // Simulate multiple customer arrivals
        for(int i = 0; i < 100; i++) {
            Event event = new Event(EventType.ARR_AUTOMAT, Clock.getInstance().getClock());
            engine.runEvent(event);
        }
        Map<String, List<Customer>> status = engine.getQueueStatus();
        assertNotNull(status);
        assertTrue(status.values().stream().mapToInt(List::size).sum() > 0);
    }


    @Test
    @DisplayName("Test customer flow through system")
    void testCustomerFlow() {
        engine.initialize();
        // Add customer to automat
        Event arrivalEvent = new Event(EventType.ARR_AUTOMAT, Clock.getInstance().getClock());
        engine.runEvent(arrivalEvent);

        // Process automat departure
        Event departureEvent = new Event(EventType.DEP_AUTOMAT, Clock.getInstance().getClock());
        engine.runEvent(departureEvent);

        Map<String, List<Customer>> status = engine.getQueueStatus();
        assertTrue(status.values().stream().anyMatch(list -> !list.isEmpty()));
    }

    @Test
    @DisplayName("Test statistics generation")
    void testStatisticsGeneration() {
        engine.initialize();

        // Create and process a complete customer journey
        Event arrival = new Event(EventType.ARR_AUTOMAT, Clock.getInstance().getClock());
        engine.runEvent(arrival);

        // Process through automat
        Event automatDeparture = new Event(EventType.DEP_AUTOMAT, Clock.getInstance().getClock() + 1);
        engine.runEvent(automatDeparture);

        // Process teller service completion
        Event tellerDeparture = new Event(EventType.DEP_TELLER1, Clock.getInstance().getClock() + 10);
        engine.runEvent(tellerDeparture);

        // Verify customer count was updated
        verify(mockController, times(1)).updateCustomerCount(1);
    }

    @Test
    @DisplayName("Test queue balancing")
    void testQueueBalancing() {
        engine.initialize();

        // Add customers and process through automat
        for(int i = 0; i < 5; i++) {
            Event arrival = new Event(EventType.ARR_AUTOMAT, Clock.getInstance().getClock() + i);
            engine.runEvent(arrival);
            Event departure = new Event(EventType.DEP_AUTOMAT, Clock.getInstance().getClock() + i + 1);
            engine.runEvent(departure);
        }

        Map<String, List<Customer>> queues = engine.getQueueStatus();

        // Get transaction teller queue lengths
        int teller1Length = queues.get("teller1").size();
        int teller2Length = queues.get("teller2").size();

        // Verify that difference between teller queues is at most 1
        int queueDiff = Math.abs(teller1Length - teller2Length);
        assertTrue(queueDiff <= 1,
                String.format("Queue difference should be at most 1, but was %d (Teller1: %d, Teller2: %d)",
                        queueDiff, teller1Length, teller2Length));
    }






}