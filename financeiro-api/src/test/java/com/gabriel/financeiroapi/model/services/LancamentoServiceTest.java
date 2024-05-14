package com.gabriel.financeiroapi.model.services;

import com.gabriel.financeiroapi.exception.RegraNegocioException;
import com.gabriel.financeiroapi.model.entity.Lancamento;
import com.gabriel.financeiroapi.model.entity.Usuario;
import com.gabriel.financeiroapi.model.enums.StatusLancamento;
import com.gabriel.financeiroapi.model.repository.LancamentoRepository;
import com.gabriel.financeiroapi.model.repository.LancamentoRepositoryTest;
import com.gabriel.financeiroapi.model.services.impl.LancamentoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LancamentoServiceTest {

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

    @Test
    void filtrarLancamentos() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);

        List<Lancamento> lista = List.of(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        // execução
        List<Lancamento> resultado = service.buscar(lancamento);

        // verificação
        assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
    }

    @Test
    void autalizarStatusLancamento() {
        // cenário
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1L);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        // execução
        service.atualizarStatus(lancamento, novoStatus);

        // verificação
        assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    void obterLancamentoPorId() {
        // cenário
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        // execução
        Optional<Lancamento> resultado = service.obterPorId(id);

        // verificação
        assertThat(resultado).isPresent();
    }

    @Test
    void retornarVazioQuandoLancamentoNaoExistir() {
        // cenário
        Long id = 1L;

        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        // execução
        Optional<Lancamento> resultado = service.obterPorId(id);

        // verificação
        assertThat(resultado).isNotPresent();
    }

    @Test
    void lancarErrosAoValidarLancamento() {
        Lancamento lancamento = new Lancamento();

        Throwable erroDescricao = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroDescricao).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

        lancamento.setDescricao("");

        Throwable erroDescricao2 = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroDescricao2).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");

        lancamento.setDescricao("Salário");

        Throwable erroMes = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroMes).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        lancamento.setMes(0);

        Throwable erroMes2 = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroMes2).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        lancamento.setMes(13);

        Throwable erroMes3 = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroMes3).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");

        lancamento.setMes(5);

        Throwable erroAno = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroAno).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

        lancamento.setAno(200);

        Throwable erroAno2 = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroAno2).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");

        lancamento.setAno(2019);

        Throwable erroUsuario = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroUsuario).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");

        lancamento.setUsuario(new Usuario());

        Throwable erroUsuario2 = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroUsuario2).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        lancamento.setUsuario(usuario);

        Throwable erroValor = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroValor).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

        lancamento.setValor(BigDecimal.ZERO);

        Throwable erroValor2 = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroValor2).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");

        lancamento.setValor(BigDecimal.valueOf(1));

        Throwable erroTipo = catchThrowable(() -> service.validar(lancamento));
        assertThat(erroTipo).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
    }
}
