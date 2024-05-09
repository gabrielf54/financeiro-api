package com.gabriel.financeiroapi.model.repository;

import com.gabriel.financeiroapi.model.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager entityManager;

    private static Usuario criarUsuario() {
        return Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").build();
    }

    @Test
    void verficarSeExisteEmail() {
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        boolean result = usuarioRepository.existsByEmail("usuario@email.com");

        assertThat(result).isTrue();
    }

    @Test
    void retornarFalsoQuandoNaoExistirEmail() {
        boolean result = usuarioRepository.existsByEmail("usuario@email.com");

        assertThat(result).isFalse();
    }

    @Test
    void persistirUsuarioNaBaseDeDados() {
        Usuario usuario = criarUsuario();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    void buscarUsuarioPorEmail() {
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    void retornarVazioAoBuscarUsuarioPorEmailQuandoNaoExistirUsuario() {
        Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

        assertThat(result.isPresent()).isFalse();
    }
}
