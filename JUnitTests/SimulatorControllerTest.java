import org.example.View.SimulatorView;
import org.example.controller.SimulatorController;
import org.example.model.Customer;
import org.example.model.CustomerType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulatorControllerTest {
    private SimulatorController controller;
    private SimulatorView mockView;

    @BeforeAll
    static void initJavaFX() {
        System.setProperty("javafx.platform", "mock");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
    }

    @BeforeEach
    void setup() {
        mockView = mock(SimulatorView.class);
        controller = new SimulatorController(mockView);
    }

    @Test
    @DisplayName("Test initialization with default values")
    void testInitialization() {
        assertEquals(2, controller.getNumberOfStations());
        assertEquals(1, controller.getNumberOfAccountStations());
        assertEquals(5.0, controller.getArrivalInterval());
        assertEquals(80.0, controller.getClientDistribution());
    }


    @Test
    @DisplayName("Test service time configuration")
    void testServiceTimeConfiguration() {
        controller.setTransactionServiceTime(12.0);
        controller.setAccountServiceTime(18.0);
        assertEquals(12.0, controller.getTransactionServiceTime());
        assertEquals(18.0, controller.getAccountServiceTime());
    }

    @Test
    @DisplayName("Test queue status conversion")
    void testQueueStatusConversion() {
        Map<String, List<Customer>> queueStatus = new HashMap<>();
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(CustomerType.TRANSACTION_CLIENT));
        customers.add(new Customer(CustomerType.ACCOUNT_CLIENT));
        queueStatus.put("teller1", customers);

        Map<String, List<Integer>> signs = controller.convertQueueStatusToSigns(queueStatus);
        assertEquals(2, signs.get("teller1").size());
        assertEquals(1, signs.get("teller1").get(0)); // Transaction client
        assertEquals(2, signs.get("teller1").get(1)); // Account client
    }

    @Test
    @DisplayName("Test simulation speed control")
    void testSimulationSpeedControl() {
        controller.setSimulationSpeed(50.0);
        assertEquals(50, controller.getSleepTime());

        controller.setSimulationSpeed(100.0);
        assertEquals(1, controller.getSleepTime()); // Minimum sleep time
    }

    @Test
    @DisplayName("Test simulation time configuration")
    void testSimulationTimeConfiguration() {
        double newTime = 2000.0;
        controller.setSimulationTime(newTime);
        assertEquals(newTime, controller.getSimulationTime());
    }

    @Test
    @DisplayName("Test client distribution configuration")
    void testClientDistributionConfiguration() {
        double newDistribution = 70.0;
        controller.setClientDistribution(newDistribution);
        assertEquals(newDistribution, controller.getClientDistribution());
    }

    @Test
    @DisplayName("Test customer count update")
    void testCustomerCountUpdate() {
        int count = 10;
        controller.updateCustomerCount(count);
        verify(mockView).updateStatusArea(count);
    }

}
