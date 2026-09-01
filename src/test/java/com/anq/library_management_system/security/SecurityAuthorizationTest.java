package com.anq.library_management_system.security;

import com.anq.library_management_system.config.SecurityConfig;
import com.anq.library_management_system.controller.BookController;
import com.anq.library_management_system.controller.BorrowRecordController;
import com.anq.library_management_system.dto.BookDto;
import com.anq.library_management_system.service.BookService;
import com.anq.library_management_system.service.BorrowRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        BookController.class,
        BorrowRecordController.class
})
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BorrowRecordService borrowRecordService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void getBooks_withoutAuthentication_shouldReturn401() throws Exception {

        mockMvc.perform(
                        get("/api/books")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void getBooks_asUser_shouldReturn200() throws Exception {

        when(bookService.getAllBooks())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/books")
                                .with(user("testuser")
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk());
    }


    @Test
    void getBooks_asLibrarian_shouldReturn200() throws Exception {

        when(bookService.getAllBooks())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/books")
                                .with(user("anq")
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_LIBRARIAN"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk());
    }


    @Test
    void createBook_asUser_shouldReturn403() throws Exception {

        String json = """
                {
                  "title": "Effective Java",
                  "description": "Java programming book",
                  "authors": [1],
                  "genres": [1],
                  "publicationYear": 2018,
                  "availableCopies": 5
                }
                """;

        mockMvc.perform(
                        post("/api/books")
                                .with(user("testuser")
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USER"
                                                )
                                        )
                                )
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void createBook_asLibrarian_shouldReturn200() throws Exception {

        BookDto response = new BookDto(
                1L,
                "Effective Java",
                "Java programming book",
                List.of(1L),
                List.of(1L),
                2018,
                5
        );

        when(bookService.createBook(any(BookDto.class)))
                .thenReturn(response);

        String json = """
                {
                  "title": "Effective Java",
                  "description": "Java programming book",
                  "authors": [1],
                  "genres": [1],
                  "publicationYear": 2018,
                  "availableCopies": 5
                }
                """;

        mockMvc.perform(
                        post("/api/books")
                                .with(user("anq")
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_LIBRARIAN"
                                                )
                                        )
                                )
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk());
    }


    @Test
    void getBorrowRecords_asUser_shouldReturn403()
            throws Exception {

        mockMvc.perform(
                        get("/api/borrow-records")
                                .with(user("testuser")
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void getBorrowRecords_asLibrarian_shouldReturn200()
            throws Exception {

        when(borrowRecordService.getAllBorrowRecords())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/borrow-records")
                                .with(user("anq")
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_LIBRARIAN"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk());
    }


    @Test
    void getMyBorrowRecords_asUser_shouldReturn200()
            throws Exception {

        when(
                borrowRecordService
                        .getBorrowRecordsForCurrentUser("testuser")
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/borrow-records/me")
                                .with(user("testuser")
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_USER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk());
    }
}