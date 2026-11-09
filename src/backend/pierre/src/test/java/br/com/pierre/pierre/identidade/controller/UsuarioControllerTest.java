package br.com.pierre.pierre.identidade.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.pierre.pierre.identidade.UsuarioService;
import br.com.pierre.pierre.identidade.dto.CadastroRequest;
import br.com.pierre.pierre.identidade.entity.Usuario;
import br.com.pierre.pierre.identidade.repository.UsuarioRepository;

class UsuarioControllerTest {

    private final UsuarioRepository repository = mock(UsuarioRepository.class);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new UsuarioController(
                    new UsuarioService(repository, new BCryptPasswordEncoder()),
                    new HttpSessionSecurityContextRepository()))
            .addPlaceholderValue("api-prefix", "/api/v1")
            .build();

    @Test
    void cadastroDeveResponderCreated() throws Exception {
        when(repository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "nomeCompleto": "Ana Silva",
                          "email": "ana@example.com",
                          "senha": "senha1234",
                          "consentimentoLGPD": true
                        }
                        """))
               .andExpect(status().isCreated());
    }

    @Test
    void cadastroEmailEmUsoDeveFalhar() {
        when(repository.existsByEmailIgnoreCase("ana@example.com"))
                .thenReturn(false, true);

        UsuarioService service = new UsuarioService(repository, new BCryptPasswordEncoder());
        CadastroRequest request = new CadastroRequest(
                "Ana Silva", "ana@example.com", "exemplo-senha", true);

        service.save(request);

        CadastroRequest request2 = new CadastroRequest("Ana Souza", "ana@example.com", "senha123", true);
        assertThrows(IllegalArgumentException.class, () -> service.save(request2));
    }

    @Test
    void cadastroSemConsentimentoDeveFalhar() {
        UsuarioService service = new UsuarioService(repository, new BCryptPasswordEncoder());
        CadastroRequest request = new CadastroRequest(
                "Bruno Silva", "bruno@example.com", "exemplo-senha", false);

        assertThrows(IllegalArgumentException.class, () -> service.save(request));
    }
}
