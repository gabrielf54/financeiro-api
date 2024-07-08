package com.gabriel.financeiroapi.service.impl;

import com.gabriel.financeiroapi.exception.ErroAutenticacaoException;
import com.gabriel.financeiroapi.exception.RegraNegocioException;
import com.gabriel.financeiroapi.model.entity.Usuario;
import com.gabriel.financeiroapi.model.repository.UsuarioRepository;
import com.gabriel.financeiroapi.service.UsuarioService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);

        if(usuario.isEmpty()) {
            throw new ErroAutenticacaoException("Usuário não encontrado pelo e-mail informado");
        }

        if(!usuario.get().getSenha().equals(senha)) {
            throw new ErroAutenticacaoException("Senha inválida");
        }

        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean exists = this.repository.existsByEmail(email);
        if (exists) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }
}
