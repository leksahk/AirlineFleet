package com.airline.airlineweb;

import com.airline.airlineweb.controller.WebController;
import com.airline.airlineweb.model.Airplane;
import com.airline.airlineweb.service.AirlineService;
import com.airline.airlineweb.service.EmailService; // Додай цей імпорт
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AirlineWebAutomationTest {

    @Test
    void testMainPagesAutomation() {
        AirlineService mockService = mock(AirlineService.class);
        EmailService mockEmail = mock(EmailService.class);

        when(mockService.getAllAirplanes()).thenReturn(new ArrayList<Airplane>());

        WebController controller = new WebController(mockService, mockEmail);

        ConcurrentModel model = new ConcurrentModel();

        assertEquals("index", controller.index(model));
        assertEquals("add-plane", controller.showAddForm());

        System.out.println(" ест пройдено");
    }
}