package br.com.pierre.pierre.identidade;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import br.com.pierre.pierre.compartilhado.EmailService;
import br.com.pierre.pierre.identidade.entity.Usuario;

class EmailCadastroServiceTest {

    @Test
    void enviaReciboEmPortuguesParaEmailDoUsuario() {
        EmailService emailService = mock(EmailService.class);
        when(emailService.enviar(
                eq("ana@example.com"),
                eq("Cadastro realizado no Pierre"),
                org.mockito.ArgumentMatchers.contains("Olá, Ana Silva!")))
                .thenReturn(true);
        EmailCadastroService service = new EmailCadastroService(emailService);
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Ana Silva");
        usuario.setEmail("ana@example.com");

        assertTrue(service.enviarRecibo(usuario));
        verify(emailService).enviar(
                eq("ana@example.com"),
                eq("Cadastro realizado no Pierre"),
                org.mockito.ArgumentMatchers.contains("Este e-mail é apenas um recibo"));
    }
}