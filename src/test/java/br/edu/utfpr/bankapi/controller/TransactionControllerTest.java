package br.edu.utfpr.bankapi.controller;

import br.edu.utfpr.bankapi.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
class TransactionControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    TestEntityManager entityManager;

    Account sourceAccount;
    Account receiverAccount;

    @BeforeEach
    void setup() {
        sourceAccount = new Account("Lauro Lima", 12346L, 1000.0, 500.0);
        receiverAccount = new Account("Maria Silva", 12347L, 2000.0, 800.0);

        entityManager.persist(sourceAccount);
        entityManager.persist(receiverAccount);
    }

    @Test
    void deveriaRetornarStatus400ParaRequisicaoInvalida() throws Exception {
        // ARRANGE
        var json = "{}"; // Body inválido

        // ACT
        var response = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void deveriaRetornarStatus201ParaRequisicaoOK() throws Exception {
        // ARRANGE

        var json = """
                {
                    "receiverAccountNumber": 12346,
                    "amount": 200
                }
                    """;

        // ACT
        var response = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/deposit")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void deveriaRetornarDadosCorretosNoJson() throws Exception {
        // ARRANGE
        var json = """
                {
                    "receiverAccountNumber": 12346,
                    "amount": 200
                }
                    """;

        // ACT + ASSERT
        mvc.perform(
            MockMvcRequestBuilders.post("/transaction/deposit")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.receiverAccount.number", Matchers.equalTo(12346)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.equalTo(200.0)));
}

    @Test
    void deveriaRetornarStatus400ParaRequisicaoInvalidaTransfer() throws Exception {
        var json = "{}";

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void deveriaRetornarStatus400ParaQuantidadeInvalidaTransfer() throws Exception {
    var json = """
            {
                "sourceAccountNumber": 12346,
                "receiverAccountNumber": 12347,
                "amount": 100000000000
            }
            """;

    var response = mvc.perform(
            MockMvcRequestBuilders.post("/transaction/transfer")
                    .content(json).contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void deveriaRetornarStatus201ParaRequisicaoOKTransfer() throws Exception {
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "receiverAccountNumber": 12347,
                    "amount": 200
                }
                """;

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void deveriaRetornarDadosCorretosNoJsonTransfer() throws Exception {
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "receiverAccountNumber": 12347,
                    "amount": 200
                }
                """;

        mvc.perform(
                MockMvcRequestBuilders.post("/transaction/transfer")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sourceAccount.number", Matchers.equalTo(12346)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.receiverAccount.number", Matchers.equalTo(12347)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.equalTo(200.0)));
    }

    @Test
    void deveriaRetornarStatus400ParaRequisicaoInvalidaWithdraw() throws Exception {
        var json = "{}";

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void deveriaRetornarStatus400ParaQuantidadeInvalidaWithdraw() throws Exception {
    var json = """
            {
                "sourceAccountNumber": 12346,
                "amount": 100000000000
            }
            """;

    var response = mvc.perform(
            MockMvcRequestBuilders.post("/transaction/withdraw")
                    .content(json).contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(400);
    }

    @Test
    void deveriaRetornarStatus201ParaRequisicaoOKWithdraw() throws Exception {
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "amount": 200
                }
                """;

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void deveriaRetornarDadosCorretosNoJsonWithdraw() throws Exception {
        var json = """
                {
                    "sourceAccountNumber": 12346,
                    "amount": 200
                }
                """;

        mvc.perform(
                MockMvcRequestBuilders.post("/transaction/withdraw")
                        .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sourceAccount.number", Matchers.equalTo(12346)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", Matchers.equalTo(200.0)));
    }
}