

import org.example.model.Customer;
import org.example.model.CustomerType;
import org.example.framework.Clock;
import org.example.framework.Trace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    private Customer customer;

    @BeforeEach
    void setup() {
        Trace.setTraceLevel(Trace.Level.INFO);
        Clock.getInstance().reset();
        Customer.resetCustomerCount();
        customer = new Customer(CustomerType.TRANSACTION_CLIENT);
    }

    @Test
    @DisplayName("Customer initializes with correct values")
    void testCustomerInitialization() {
        assertEquals(1, customer.getId());
        assertEquals(CustomerType.TRANSACTION_CLIENT, customer.getType());
        assertEquals(0.0, customer.getArrivalTime());
    }

    @Test
    @DisplayName("Customer ID increments correctly")
    void testCustomerIdIncrement() {
        Customer customer2 = new Customer(CustomerType.ACCOUNT_CLIENT);
        Customer customer3 = new Customer(CustomerType.TRANSACTION_CLIENT);

        assertEquals(1, customer.getId());
        assertEquals(2, customer2.getId());
        assertEquals(3, customer3.getId());
    }

    @Test
    @DisplayName("Time setters and getters work correctly")
    void testTimeOperations() {
        double removalTime = 10.0;
        double queueStartTime = 5.0;

        customer.setRemovalTime(removalTime);
        customer.setQueueStartTime(queueStartTime);

        assertEquals(removalTime, customer.getRemovalTime());
        assertEquals(queueStartTime, customer.getQueueStartTime());
    }

    @Test
    @DisplayName("Customer count resets correctly")
    void testCustomerReset() {
        new Customer(CustomerType.TRANSACTION_CLIENT);
        new Customer(CustomerType.ACCOUNT_CLIENT);
        Customer.resetCustomerCount();
        Customer newCustomer = new Customer(CustomerType.TRANSACTION_CLIENT);
        assertEquals(1, newCustomer.getId());
    }
}
