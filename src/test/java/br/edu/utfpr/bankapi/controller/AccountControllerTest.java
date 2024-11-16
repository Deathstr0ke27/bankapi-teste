package br.edu.utfpr.bankapi.controller;

import br.edu.utfpr.bankapi.dto.AccountDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void deveriaRetornarStatus200EContaAoBuscarPorNumero() throws Exception {
        var account = new Account("John Doe", 123L, 1000.0, 500.0);

        when(accountService.getByNumber(123L)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/account/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.number").value(123L))
                .andExpect(jsonPath("$.balance").value(1000.0))
                .andExpect(jsonPath("$.specialLimit").value(500.0));

        verify(accountService, times(1)).getByNumber(123L);
    }

    @Test
    void deveriaRetornarStatus404AoBuscarNumeroInexistente() throws Exception {
        when(accountService.getByNumber(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/account/999"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getByNumber(999L);
    }

    @Test
    void deveriaRetornarStatus200EListaDeContas() throws Exception {
        var account1 = new Account("John Doe", 123L, 1000.0, 500.0);
        var account2 = new Account("Jane Doe", 456L, 2000.0, 800.0);

        when(accountService.getAll()).thenReturn(List.of(account1, account2));

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Doe"));

        verify(accountService, times(1)).getAll();
    }

    @Test
    void deveriaRetornarStatus201AoCriarConta() throws Exception {
        var accountDTO = new AccountDTO("John Doe", 123L, 1000.0, 500.0);
        var savedAccount = new Account("John Doe", 123L, 1000.0, 500.0);

        when(accountService.save(accountDTO)).thenReturn(savedAccount);

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.number").value(123L));

        verify(accountService, times(1)).save(accountDTO);
    }

    @Test
    void deveriaRetornarStatus400AoCriarContaComDadosInvalidos() throws Exception {
        var invalidJson = "{}"; // Campos obrigatórios ausentes

        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveriaRetornarStatus200AoAtualizarConta() throws Exception {
        var accountDTO = new AccountDTO("Jane Doe", 456L, 2000.0, 800.0);
        var updatedAccount = new Account("Jane Doe", 456L, 2000.0, 800.0);

        when(accountService.update(1L, accountDTO)).thenReturn(updatedAccount);

        mockMvc.perform(put("/account/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.number").value(456L));

        verify(accountService, times(1)).update(1L, accountDTO);
    }

    @Test
    void deveriaRetornarStatus404AoAtualizarContaInexistente() throws Exception {
    var accountDTO = new AccountDTO("Jane Doe", 456L, 2000.0, 800.0);

    when(accountService.update(999L, accountDTO)).thenThrow(new NotFoundException("Conta não encontrada"));

    mockMvc.perform(put("/account/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(accountDTO)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Conta não encontrada")); // Verifica a string diretamente
    }
}
