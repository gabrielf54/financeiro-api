package com.gabriel.financeiroapi.model.services;

import com.gabriel.financeiroapi.exception.RegraNegocioException;
import com.gabriel.financeiroapi.model.entity.Lancamento;
import com.gabriel.financeiroapi.model.enums.StatusLancamento;
import com.gabriel.financeiroapi.model.repository.LancamentoRepository;
import com.gabriel.financeiroapi.model.repository.LancamentoRepositoryTest;
import com.gabriel.financeiroapi.model.services.impl.LancamentoServiceImpl;
import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl service;

    @MockBean
    LancamentoRepository repository;


    @Test
    void salvarLancamento() {
        //cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        // execução
        Lancamento lancamento = service.salvar(lancamentoASalvar);

        // verificação
        assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    void naoSalvarLancamentoQuandoHouverErroDeValidacao() {
        //cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        //execução e verificação
        catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    void atualizarLancamento() {
        //cenário
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1L);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(service).validar(lancamentoSalvo);

        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        // execução
        service.atualizar(lancamentoSalvo);

        // verificação
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    void deveLancarErroAoTentarAtualizarLancamentoNaoSalvo() {
        //cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        //execução e verificação
        catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamento);
    }

    @Test
    void deletarLancamento() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        // execução
        catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

        // verificação
        Mockito.verify(repository).delete(lancamento);
    }

    @Test
    void lancarErroAoTentarDeletarLancamentoNaoSalvo() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        // execução
        catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

        // verificação
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }
}
