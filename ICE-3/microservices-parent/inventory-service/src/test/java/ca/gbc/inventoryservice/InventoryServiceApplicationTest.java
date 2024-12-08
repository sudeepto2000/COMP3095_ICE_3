package ca.gbc.inventoryservice;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceApplicationTests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InventoryRepository inventoryRepository;


    private List<Inventory> getInventoryList() {
        List<Inventory> inventoryList = new ArrayList<>();
        Inventory inventory = Inventory.builder()
                .skuCode("IPAD2023")
                .quantity(10)
                .build();

        inventoryList.add(inventory);
        return inventoryList;
    }

    @Test
    void isInStock() throws Exception {

        List<Inventory> inventoryList = getInventoryList();
        inventoryRepository.saveAll(inventoryList);

        List<InventoryRequest> inventoryRequestList = new ArrayList<>();
        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .skuCode("IPAD2023")
                .quantity(10)
                .build();

        inventoryRequestList.add(inventoryRequest);

        String inventoryRequestJson = objectMapper.writeValueAsString(inventoryRequestList);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inventoryRequestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].skuCode").value("IPAD2023"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sufficientStock").value(true));

        assertEquals(10, (int) inventoryRepository.findBySkuCode("IPAD2023").get().getQuantity());

    }
}