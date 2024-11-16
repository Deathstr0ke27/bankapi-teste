package br.edu.utfpr.bankapi.service;

import br.edu.utfpr.bankapi.dto.AccountDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetByNumber() {
        var account = new Account();
        account.setNumber(123L);

        when(accountRepository.getByNumber(123L)).thenReturn(Optional.of(account));

        var result = accountService.getByNumber(123L);

        assertTrue(result.isPresent());
        assertEquals(123L, result.get().getNumber());
        verify(accountRepository, times(1)).getByNumber(123L);
    }

    @Test
    void testGetAll() {
        var account1 = new Account();
        account1.setNumber(123L);

        var account2 = new Account();
        account2.setNumber(456L);

        when(accountRepository.findAll()).thenReturn(List.of(account1, account2));

        var result = accountService.getAll();

        assertEquals(2, result.size());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testSave() {
        var accountDTO = new AccountDTO("John Doe", 123L, 0.0, 500.0);
        var account = new Account();
        account.setName(accountDTO.name());
        account.setNumber(accountDTO.number());
        account.setBalance(accountDTO.balance());
        account.setSpecialLimit(accountDTO.specialLimit());

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        var result = accountService.save(accountDTO);

        assertNotNull(result);
        assertEquals(123L, result.getNumber());
        assertEquals("John Doe", result.getName());
        assertEquals(0.0, result.getBalance());
        assertEquals(500.0, result.getSpecialLimit());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdate() throws NotFoundException {
        var accountDTO = new AccountDTO("Jane Doe", 123L, 0.0, 1000.0);
        var existingAccount = new Account();
        existingAccount.setId(1L);
        existingAccount.setNumber(111L);
        existingAccount.setName("Old Name");
        existingAccount.setSpecialLimit(200.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        var updatedAccount = accountService.update(1L, accountDTO);

        assertNotNull(updatedAccount);
        assertEquals("Jane Doe", updatedAccount.getName());
        assertEquals(123L, updatedAccount.getNumber());
        assertEquals(1000.0, updatedAccount.getSpecialLimit());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(existingAccount);
    }
}
