package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.user.projection.UserShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    final LocalDateTime start = LocalDateTime.now();
    final LocalDateTime end = LocalDateTime.now();
    final BookingRequestDto bookingRequestDto = new BookingRequestDto();
    BookingResponseDto bookingResponseDto;
    static final String userIdHeader = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        setBookingRequest();
        setBookingResponse();
    }

    void setBookingRequest() {
        bookingRequestDto.setItemId(1);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
    }

    void setBookingResponse() {
        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .booker(new UserShort(1))
                .item(new ItemShort(1, "itemName"))
                .build();
    }

    @Test
    void testAddBooking() throws Exception {
        Mockito.when(bookingService.addBooking(Mockito.anyInt(),Mockito.any())).thenReturn(bookingResponseDto);
        mvc.perform(post("/bookings")
                .header(userIdHeader,"1")
                .content(mapper.writeValueAsString(bookingRequestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()),Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    void testSetBookingStatus() throws Exception {
        Mockito.when(bookingService.setBookingStatus(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingResponseDto);
        mvc.perform(patch("/bookings/1")
                        .header(userIdHeader, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    void testGetBookingById() throws Exception {
        Mockito.when(bookingService.getBookingById(Mockito.anyInt(), Mockito.anyLong())).thenReturn(bookingResponseDto);
        mvc.perform(get("/bookings/1")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    void testGetAllBookingsOfBookerByState() throws Exception {
        Mockito.when(bookingService.getAllBookingsOfBookerByState(Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(bookingResponseDto));
        mvc.perform(get("/bookings")
                        .header(userIdHeader, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())));
    }

    @Test
    void testGetAllBookingsOfOwnerByState() throws Exception {
        Mockito.when(bookingService.getAllBookingsOfOwnerByState(Mockito.anyInt(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(bookingResponseDto));
        mvc.perform(get("/bookings/owner")
                        .header(userIdHeader, "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponseDto.getItem().getName())));
    }

}
