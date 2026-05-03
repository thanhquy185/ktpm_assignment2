package com.shopcart.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopcart.FakeDataForTest;
import com.shopcart.configs.JwtAuthenticationFilter;
import com.shopcart.controllers.InventoryController;
import com.shopcart.dtos.request.InventoryCheckStockRequest;
import com.shopcart.dtos.request.InventoryItemRequest;
import com.shopcart.services.InventoryService;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Inventory Controller Integration Tests")
class InventoryControllerIntegrationTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private InventoryService inventoryService;

        private final FakeDataForTest fakeDataForTest = new FakeDataForTest();

        @Test
        @DisplayName("POST /api/inventories - Kiểm tra tồn kho của các sản phẩm")
        void test_CheckStock() throws Exception {
                UUID productId = this.fakeDataForTest.getProductIdFake1();
                List<InventoryItemRequest> inventoryItems = List.of(
                                InventoryItemRequest.builder()
                                                .productId(productId.toString())
                                                .quantity(2L)
                                                .build());
                InventoryCheckStockRequest request = InventoryCheckStockRequest.builder()
                                .inventoryItems(inventoryItems)
                                .build();

                when(inventoryService.checkStock(any(InventoryCheckStockRequest.class)))
                                .thenReturn(true);

                mockMvc.perform(post("/api/inventories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                                .andExpect(jsonPath("$.message").value("Check stock for inventory items is available!"))
                                .andExpect(jsonPath("$.error").isEmpty())
                                .andExpect(jsonPath("$.data").value(true));

                verify(this.inventoryService, times(1))
                                .checkStock(any(InventoryCheckStockRequest.class));
        }
}