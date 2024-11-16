package br.edu.utfpr.bankapi.validation;

import br.edu.utfpr.bankapi.exception.WithoutBalanceException;
import br.edu.utfpr.bankapi.model.Account;
import br.edu.utfpr.bankapi.model.Transaction;
import br.edu.utfpr.bankapi.model.TransactionType;
import br.edu.utfpr.bankapi.validations.AvailableBalanceValidation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AvailableBalanceValidationTest {

    private AvailableBalanceValidation availableBalanceValidation;

    @BeforeEach
    void setup() {
        availableBalanceValidation = new AvailableBalanceValidation();
    }

    @Test
    void testValidateWithSufficientBalance() {
        // Simula uma conta com saldo suficiente
        var sourceAccount = new Account("John Doe", 123L, 1000.0, 500.0);
        var transaction = new Transaction(sourceAccount, null, 1200.0, TransactionType.WITHDRAW);

        // Executa a validação
        assertDoesNotThrow(() -> availableBalanceValidation.validate(transaction));
    }

    @Test
    void testValidateWithoutSufficientBalance() {
        // Simula uma conta com saldo insuficiente
        var sourceAccount = new Account("John Doe", 123L, 1000.0, 500.0);
        var transaction = new Transaction(sourceAccount, null, 1600.0, TransactionType.WITHDRAW);

        // Executa a validação e verifica se a exceção é lançada
        var exception = assertThrows(WithoutBalanceException.class, () -> availableBalanceValidation.validate(transaction));

        assertNotNull(exception);
    }
}
