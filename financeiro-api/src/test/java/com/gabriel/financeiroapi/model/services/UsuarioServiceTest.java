package com.gabriel.financeiroapi.model.services;

import com.gabriel.financeiroapi.exception.ErroAutenticacaoException;
import com.gabriel.financeiroapi.exception.RegraNegocioException;
import com.gabriel.financeiroapi.model.entity.Usuario;
import com.gabriel.financeiroapi.model.repository.UsuarioRepository;
import com.gabriel.financeiroapi.model.services.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test
    public void salvarUmUsuario() {
        // cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().id(1L).nome("nome").email("email@email.com").senha("senha").build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //acao
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        // verificacao
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test
    public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
        // cenario
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        // acao
        org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.salvarUsuario(usuario);
        });

        // verificacao
        Mockito.verify(repository, Mockito.never()).save(usuario); // Está verificando se o método save nunca foi chamado

    }

    @Test
    public void autenticarUsuarioComSucesso() {
        // cenario
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).id(1L).senha(senha).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //acao
        Usuario result = service.autenticar(email, senha);

        //verificacao
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void lancarErroQuandoNaoEncontrarUsuarioPeloEmail() {
        //cenario
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));

        //verificacao
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Usuário não encontrado pelo e-mail informado");
    }

    @Test
    public void lancarErroQuandoSenhaIncorreta() {
        //cenario
        String senha = "senha";

        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Senha inválida");
    }

    @Test
    public void validarEmail() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //acao
        service.validarEmail("email@email.com");
    }

    @Test
    public void dispararErroQuandoExistirEmailCadastrado() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //acao
        org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
            service.validarEmail("email@email.com");
        });
    }
}
