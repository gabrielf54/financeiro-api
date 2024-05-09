package com.gabriel.financeiroapi.model.repository;

import com.gabriel.financeiroapi.model.entity.Lancamento;
import com.gabriel.financeiroapi.model.enums.StatusLancamento;
import com.gabriel.financeiroapi.model.enums.TipoLancamento;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void salvarLancamento() {
        Lancamento lancamento = criarLancamento();

        lancamento = repository.save(lancamento);

        assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    void deletarLancamento() {
        Lancamento lancamento = criarEPersistirLancamento();
        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoInexistente).isNull();
    }

    @Test
    void atualizarLancamento() {
        Lancamento lancamento = criarEPersistirLancamento();

        lancamento.setAno(2018);
        lancamento.setDescricao("teste atualizado");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
        assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("teste atualizado");
        assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
    }

    @Test
    void buscarUmLancamentoPorId() {
        Lancamento lancamento = criarEPersistirLancamento();

        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        assertThat(lancamentoEncontrado).isPresent();
    }

    public static Lancamento criarLancamento() {
        return Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

    private Lancamento criarEPersistirLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }

}
