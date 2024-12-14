
import org.example.framework.Trace;
import org.example.model.*;
import org.example.eduni.distributions.ContinuousGenerator;
import org.example.framework.EventList;
import org.example.framework.Clock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ServicePointTest {
    private ServicePoint servicePoint;
    private ContinuousGenerator mockGenerator;
    private EventList mockEventList;
    private Customer testCustomer;

    @BeforeEach
    void setup() {
        Trace.setTraceLevel(Trace.Level.INFO);

        mockGenerator = mock(ContinuousGenerator.class);
        mockEventList = mock(EventList.class);
        Clock.getInstance().reset();

        servicePoint = new ServicePoint(
                mockGenerator,
                mockEventList,
                EventType.DEP_TELLER1
        );

        testCustomer = new Customer(CustomerType.TRANSACTION_CLIENT);
    }

    @Test
    @DisplayName("Queue operations work correctly")
    void testQueueOperations() {
        servicePoint.addQueue(testCustomer);
        assertEquals(1, servicePoint.getQueueLength());
        assertTrue(servicePoint.isOnQueue());
        assertFalse(servicePoint.isReserved());
    }

    @Test
    @DisplayName("Service process executes properly")
    void testServiceProcess() {
        when(mockGenerator.sample()).thenReturn(10.0);

        servicePoint.addQueue(testCustomer);
        servicePoint.beginService();

        assertTrue(servicePoint.isReserved());
        verify(mockEventList).add(any());
    }

    @Test
    @DisplayName("Customer removal updates statistics")
    void testCustomerRemoval() {
        servicePoint.addQueue(testCustomer);
        servicePoint.beginService();
        Customer removedCustomer = servicePoint.removeQueue();

        assertEquals(testCustomer, removedCustomer);
        assertEquals(1, servicePoint.getServedCustomers());
        assertFalse(servicePoint.isReserved());
    }

    @Test
    @DisplayName("Service statistics are calculated correctly")
    void testServiceStatistics() {
        servicePoint.addQueue(testCustomer);
        servicePoint.beginService();
        Clock.getInstance().setClock(10.0);
        servicePoint.removeQueue();

        assertTrue(servicePoint.getAverageServiceTime() > 0);
        assertTrue(servicePoint.getAverageQueueTime() >= 0);
    }
}
