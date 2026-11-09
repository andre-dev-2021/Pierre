package br.com.pierre.pierre.identidade.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import br.com.pierre.pierre.identidade.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioAutenticacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparUsuarios() {
        usuarioRepository.deleteAll();
    }

    @Test
    void perfilSemLoginDeveResponderUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginValidoMantemSessaoEDevolveSomentePerfilDoUsuario() throws Exception {
        cadastrarUsuario();

        MvcResult resultadoLogin = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "email": "ana@example.com",
                          "senha": "senha-segura-123"
                        }
                        """))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpSession sessao = (MockHttpSession) resultadoLogin.getRequest().getSession(false);

        mockMvc.perform(get("/api/v1/usuarios/me").session(sessao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    void loginComSenhaIncorretaNaoCriaSessaoAutenticada() throws Exception {
        cadastrarUsuario();

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "email": "ana@example.com",
                          "senha": "senha-errada-123"
                        }
                        """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    private void cadastrarUsuario() throws Exception {
        mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "nomeCompleto": "Ana Silva",
                          "email": "ana@example.com",
                          "senha": "senha-segura-123",
                          "consentimentoLGPD": true
                        }
                        """))
                .andExpect(status().isCreated());
    }
}
