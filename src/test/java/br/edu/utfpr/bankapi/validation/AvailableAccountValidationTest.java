package br.edu.utfpr.bankapi.validation;

import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.repository.AccountRepository;
import br.edu.utfpr.bankapi.validations.AvailableAccountValidation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AvailableAccountValidationTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AvailableAccountValidation availableAccountValidation;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateAccountExists() throws Exception {
        // Simula a conta existente
        var account = new Account("John Doe", 123L, 1000.0, 500.0);

        when(accountRepository.getByNumber(123L)).thenReturn(Optional.of(account));

        // Executa o método de validação
        var result = availableAccountValidation.validate(123L);

        assertNotNull(result);
        assertEquals(123L, result.getNumber());
        verify(accountRepository, times(1)).getByNumber(123L);
    }

    @Test
    void testValidateAccountDoesNotExist() {
        // Simula a ausência da conta
        when(accountRepository.getByNumber(123L)).thenReturn(Optional.empty());

        // Executa o método de validação e verifica a exceção
        var exception = assertThrows(NotFoundException.class, () -> availableAccountValidation.validate(123L));

        assertEquals("Conta 123 inexistente", exception.getMessage());
        verify(accountRepository, times(1)).getByNumber(123L);
    }
}
