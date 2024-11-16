package br.edu.utfpr.bankapi.service;

import br.edu.utfpr.bankapi.dto.TransferDTO;
import br.edu.utfpr.bankapi.dto.WithdrawDTO;
import br.edu.utfpr.bankapi.exception.NotFoundException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.model.TransactionType;
import br.edu.utfpr.bankapi.repository.TransactionRepository;
import br.edu.utfpr.bankapi.validations.AvailableAccountValidation;
import br.edu.utfpr.bankapi.validations.AvailableBalanceValidation;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AvailableBalanceValidation availableBalanceValidation;

    @Mock
    private AvailableAccountValidation availableAccountValidation;

    @InjectMocks
    private TransactionService transactionService;

    public TransactionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testWithdraw() throws NotFoundException {
        // Dados simulados
        var account = new Account();
        account.setNumber(123);
        account.setBalance(1000.0);

        var dto = new WithdrawDTO(123, 200.0);

        when(availableAccountValidation.validate(123)).thenReturn(account);

        // Simulação do repositório
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execução do teste
        var transaction = transactionService.withdraw(dto);

        // Validações
        assertNotNull(transaction);
        assertEquals(TransactionType.WITHDRAW, transaction.getType());
        assertEquals(800.0, account.getBalance());
        verify(availableBalanceValidation, times(1)).validate(transaction);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testTransfer() throws NotFoundException {
        // Dados simulados
        var sourceAccount = new Account();
        sourceAccount.setNumber(123);
        sourceAccount.setBalance(1000.0);

        var receiverAccount = new Account();
        receiverAccount.setNumber(456);
        receiverAccount.setBalance(500.0);

        var dto = new TransferDTO(123, 456, 300.0);

        when(availableAccountValidation.validate(123)).thenReturn(sourceAccount);
        when(availableAccountValidation.validate(456)).thenReturn(receiverAccount);

        // Simulação do repositório
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execução do teste
        var transaction = transactionService.transfer(dto);

        // Validações
        assertNotNull(transaction);
        assertEquals(TransactionType.TRANSFER, transaction.getType());
        assertEquals(700.0, sourceAccount.getBalance());
        assertEquals(800.0, receiverAccount.getBalance());
        verify(availableBalanceValidation, times(1)).validate(transaction);
        verify(transactionRepository, times(1)).save(transaction);
    }
}

