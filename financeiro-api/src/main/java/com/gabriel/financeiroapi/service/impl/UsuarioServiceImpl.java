package com.gabriel.financeiroapi.service.impl;

import com.gabriel.financeiroapi.exception.ErroAutenticacaoException;
import com.gabriel.financeiroapi.exception.RegraNegocioException;
import com.gabriel.financeiroapi.model.entity.Usuario;
import com.gabriel.financeiroapi.model.repository.UsuarioRepository;
import com.gabriel.financeiroapi.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;
    private PasswordEncoder encoder;

    public UsuarioServiceImpl(
            UsuarioRepository repository,
            PasswordEncoder encoder) {
        super();
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);

        if (usuario.isEmpty()) {
            throw new ErroAutenticacaoException("Usuário não encontrado para o email informado.");
        }

        boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());

        if (!senhasBatem) {
            throw new ErroAutenticacaoException("Senha inválida.");
        }

        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        criptografarSenha(usuario);
        return repository.save(usuario);
    }

    private void criptografarSenha(Usuario usuario) {
        String senha = usuario.getSenha();
        String senhaCripto = encoder.encode(senha);
        usuario.setSenha(senhaCripto);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email.");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }

}