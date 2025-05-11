package yuriy.dev.carfixbackend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.dto.ModelDto;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.dto.enums.Status;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.mapper.UserMapper;
import yuriy.dev.carfixbackend.model.Car;
import yuriy.dev.carfixbackend.model.Order;
import yuriy.dev.carfixbackend.repository.CarRepository;
import yuriy.dev.carfixbackend.repository.OrderRepository;
import yuriy.dev.carfixbackend.service.CarService;
import yuriy.dev.carfixbackend.service.ModelService;
import yuriy.dev.carfixbackend.service.UserService;
import yuriy.dev.carfixbackend.service.WorkService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "mail2@mail.ru", authorities = {"ROLE_USER"})
public class OrdersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CarService carService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private WorkService workService;

    @Test
    public void testAddOrder_WithNewCar() throws Exception {

        UserDto userDto =  userMapper.toDto(userService.getByUsername("mail2@mail.ru"));
        ModelDto modelDto = modelService.findModelById(UUID.fromString("173cb097-680a-44c3-933e-1c8813fc16a3"));
        List<WorkDto> workDtos = List.of(
                workService.findById(UUID.fromString("09d4c1d4-d39e-40d3-b70f-81d2178a7f0e")),
                workService.findById(UUID.fromString("bbeec102-9d76-42cd-a248-ea82ccaed16a"))
        );


        OrderDto requestDto = new OrderDto(
                null,
                new CarDto(null, "ABC123", "1HGCM82633A123410",modelDto),
                userDto,
                LocalDateTime.now(),
                new BigDecimal("3200"),
                Status.PENDING,
                "Test order description",
                workDtos
        );

        String requestJson = objectMapper.writeValueAsString(requestDto);


        MvcResult result = mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
                            .with(user("mail2@mail.ru").roles("USER"))
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ApiResponseDto<OrderDto> response = objectMapper.readValue(
                responseJson,
                new TypeReference<>() {});

        assertThat(response.getMessage()).isEqualTo("OK");

        Order savedOrder = orderRepository.findById(response.getData().id())
                .orElseThrow();

        assertThat(savedOrder.getCar().getVinNumber()).isEqualTo("1HGCM82633A123410");
        assertThat(savedOrder.getPrice()).isEqualTo(BigDecimal.valueOf(3200));
        assertThat(savedOrder.getDescription()).isEqualTo("Test order description");
        assertThat(savedOrder.getWorks()).hasSize(2);

        Car savedCar = carRepository.findByVinNumber("1HGCM82633A123410").orElseThrow();
        assertThat(savedCar.getVinNumber()).isEqualTo("1HGCM82633A123410");
        assertThat(savedCar.getId()).isNotNull();
        assertThat(savedCar.getModel().getModelName()).isEqualTo("CDX");
        assertThat(savedCar.getModel().getMark().getMarkName()).isEqualTo("Acura");
    }

    @Test
    public void testAddOrder_WithExistingCar() throws Exception {

        CarDto carDto = carService.findCarById(UUID.fromString("cc0a84db-04f4-43cf-9e7b-17db5ddfc1b8"));
        UserDto userDto =  userMapper.toDto(userService.getByUsername("mail2@mail.ru"));
        List<WorkDto> workDtos = List.of(
                workService.findById(UUID.fromString("09d4c1d4-d39e-40d3-b70f-81d2178a7f0e")),
                workService.findById(UUID.fromString("bbeec102-9d76-42cd-a248-ea82ccaed16a"))
        );
        OrderDto requestDto = new OrderDto(
                null,
                carDto,
                userDto,
                LocalDateTime.now(),
                new BigDecimal("3200"),
                Status.ACCEPTED,
                "Order with existing car",
                workDtos);

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ApiResponseDto<OrderDto> response = objectMapper.readValue(
                responseJson,
                new TypeReference<>() {});

        assertThat(response.getMessage()).isEqualTo("OK");

        Order savedOrder = orderRepository.findById(response.getData().id())
                .orElseThrow();

        assertThat(savedOrder.getCar().getVinNumber()).isEqualTo("1HGCM82633A123456");
        assertThat(savedOrder.getDescription()).isEqualTo("Order with existing car");
        assertThat(savedOrder.getPrice()).isEqualTo(BigDecimal.valueOf(3200));
        assertThat(savedOrder.getWorks()).hasSize(2);
    }

}
