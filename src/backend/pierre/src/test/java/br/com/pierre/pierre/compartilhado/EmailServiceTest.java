package br.com.pierre.pierre.compartilhado;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {

    @Test
    void enviaMensagemComRemetenteConfigurado() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService service = new EmailService(mockProvider(mailSender), true, "no-reply@pierre.local");

        boolean enviado = service.enviar("ana@example.com", "Assunto", "Conteúdo");

        assertTrue(enviado);
        org.mockito.ArgumentCaptor<SimpleMailMessage> mensagem =
                org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mensagem.capture());
        org.junit.jupiter.api.Assertions.assertEquals("ana@example.com", mensagem.getValue().getTo()[0]);
        org.junit.jupiter.api.Assertions.assertEquals("Assunto", mensagem.getValue().getSubject());
        org.junit.jupiter.api.Assertions.assertEquals("Conteúdo", mensagem.getValue().getText());
        org.junit.jupiter.api.Assertions.assertEquals("no-reply@pierre.local", mensagem.getValue().getFrom());
    }

    @Test
    void falhaDeSmtpRetornaFalse() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        org.mockito.Mockito.doThrow(new MailSendException("SMTP indisponível"))
                .when(mailSender).send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));
        EmailService service = new EmailService(mockProvider(mailSender), true, "no-reply@pierre.local");

        assertFalse(service.enviar("ana@example.com", "Assunto", "Conteúdo"));
    }

    @SuppressWarnings("unchecked")
    private ObjectProvider<JavaMailSender> mockProvider(JavaMailSender mailSender) {
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);
        return provider;
    }
}