package br.com.pierre.pierre.identidade;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.exceptions.TemplateInputException;

import br.com.pierre.pierre.compartilhado.EmailService;
import br.com.pierre.pierre.identidade.entity.Usuario;

class EmailCadastroServiceTest {

    @Test
    void enviaReciboEmPortuguesParaEmailDoUsuario() {
        EmailService emailService = mock(EmailService.class);
        when(emailService.enviar(anyString(), anyString(), anyString(), anyString())).thenReturn(true);
        EmailCadastroService service = new EmailCadastroService(emailService, new EmailTemplateConfig().templateEngine());
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("<img src=x onerror=alert(1)> Ana Silva");
        usuario.setEmail("ana@example.com");

        assertTrue(service.enviarEmailCOnfirmacao(usuario));
        ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> html = ArgumentCaptor.forClass(String.class);
        verify(emailService).enviar(
                eq("ana@example.com"),
                eq("Seja bem-vindo(a) ao Pierre!"),
            texto.capture(),
            html.capture());

        assertTrue(texto.getValue().contains("Bem-vindo(a) ao Pierre, <img src=x onerror=alert(1)> Ana Silva!"));
        assertTrue(texto.getValue().contains("O Pierre foi criado para ajudar estudantes de STEM"));
        assertTrue(texto.getValue().contains("Estamos torcendo por você. Bons estudos!"));
        assertTrue(html.getValue().contains("Seu próximo passo para estudar Matemática, Física e Química começa aqui."));
        assertTrue(html.getValue().contains("&lt;img src=x onerror=alert(1)&gt; Ana Silva"));
        assertFalse(html.getValue().contains("<img src=x onerror=alert(1)>"));
    }

    @Test
    void falhaDeRenderizacaoRetornaFalseSemEnviarEmail() {
        EmailService emailService = mock(EmailService.class);
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        when(templateEngine.process(eq("email/confirmacao-html"), any(IContext.class)))
                .thenThrow(new TemplateInputException("Template indisponível"));
        EmailCadastroService service = new EmailCadastroService(emailService, templateEngine);
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Ana Silva");
        usuario.setEmail("ana@example.com");

        assertFalse(service.enviarEmailCOnfirmacao(usuario));
        verify(emailService, org.mockito.Mockito.never()).enviar(anyString(), anyString(), anyString(), anyString());
    }
}