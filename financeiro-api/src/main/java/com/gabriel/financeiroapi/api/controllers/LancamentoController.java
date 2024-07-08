package com.gabriel.financeiroapi.api.controllers;

import com.gabriel.financeiroapi.exception.RegraNegocioException;
import com.gabriel.financeiroapi.api.dto.AtualizaStatusDTO;
import com.gabriel.financeiroapi.api.dto.LancamentoDTO;
import com.gabriel.financeiroapi.model.entity.Lancamento;
import com.gabriel.financeiroapi.model.entity.Usuario;
import com.gabriel.financeiroapi.model.enums.StatusLancamento;
import com.gabriel.financeiroapi.model.enums.TipoLancamento;
import com.gabriel.financeiroapi.service.LancamentoService;
import com.gabriel.financeiroapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private LancamentoService service;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "usuario") Long idUsuario
    ) {
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado");
        }
        lancamentoFiltro.setUsuario(usuario.get());

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return service.obterPorId(id).map(entidade -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entidade.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusDTO dto) {
        return service.obterPorId(id).map(entidade -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

            if (statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status, informe um status válido");
            }

            try {
                entidade.setStatus(statusSelecionado);
                service.atualizar(entidade);
                return ResponseEntity.ok(entidade);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() -> new ResponseEntity("Lancçamento não encontrado", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long id) {
        return service.obterPorId(id).map(entidade -> {
            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
    }

    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();

        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        if (dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        if (dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado"));
        lancamento.setUsuario(usuario);

        return lancamento;
    }
}
