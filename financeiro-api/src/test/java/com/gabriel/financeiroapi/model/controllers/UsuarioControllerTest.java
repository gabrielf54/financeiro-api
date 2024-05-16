package com.gabriel.financeiroapi.model.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.financeiroapi.exception.ErroAutenticacaoException;
import com.gabriel.financeiroapi.exception.RegraNegocioException;
import com.gabriel.financeiroapi.model.dto.UsuarioDTO;
import com.gabriel.financeiroapi.model.entity.Usuario;
import com.gabriel.financeiroapi.model.services.LancamentoService;
import com.gabriel.financeiroapi.model.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    static final String API = "/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    public void deveAutenticarUmUsuario() throws Exception {
        //cenario
        String email = "usuario56@email.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
        Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
        String json = new ObjectMapper().writeValueAsString(dto);

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void retornarBadRequestAoAutenticar() throws Exception {
        //cenario
        String email = "usuario56@email.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);
        String json = new ObjectMapper().writeValueAsString(dto);

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void criarUsuario() throws Exception {
        //cenario
        String email = "usuario56@email.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
        String json = new ObjectMapper().writeValueAsString(dto);

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void deveRetornarBadRequestAoCriarUsuarioInvalido() throws Exception {
        //cenario
        String email = "usuario56@email.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
        String json = new ObjectMapper().writeValueAsString(dto);

        //execucao e verificacao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
