package br.com.pierre.pierre.compartilhado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

class EmailServiceTest {

    @Test
    void enviaMensagemMimeComAlternativasTextoHtmlERemetenteConfigurado() throws Exception {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mensagem = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mensagem);
        EmailService service = new EmailService(mockProvider(mailSender), true, "no-reply@pierre.local");

        boolean enviado = service.enviar("ana@example.com", "Assunto", "Conteúdo texto", "<p>Conteúdo HTML</p>");

        mensagem.saveChanges();
        assertTrue(enviado);
        verify(mailSender).send(mensagem);
        assertEquals("ana@example.com", mensagem.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals("Assunto", mensagem.getSubject());
        assertEquals("no-reply@pierre.local", mensagem.getFrom()[0].toString());
        assertTrue(mensagem.getContentType().toLowerCase().contains("multipart/"));

        Multipart partes = (Multipart) mensagem.getContent();
        BodyPart texto = localizarParte(partes, "text/plain");
        BodyPart html = localizarParte(partes, "text/html");
        assertEquals("Conteúdo texto", texto.getContent());
        assertEquals("<p>Conteúdo HTML</p>", html.getContent());
        assertTrue(texto.getContentType().toUpperCase().contains("CHARSET=UTF-8"));
        assertTrue(html.getContentType().toUpperCase().contains("CHARSET=UTF-8"));
    }

    @Test
    void falhaDeSmtpRetornaFalse() throws Exception {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mensagem = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mensagem);
        org.mockito.Mockito.doThrow(new MailSendException("SMTP indisponível"))
                .when(mailSender).send(org.mockito.ArgumentMatchers.any(MimeMessage.class));
        EmailService service = new EmailService(mockProvider(mailSender), true, "no-reply@pierre.local");

        assertFalse(service.enviar("ana@example.com", "Assunto", "Texto", "<p>HTML</p>"));
    }

    private BodyPart localizarParte(Multipart partes, String tipoMime) throws Exception {
        for (int indice = 0; indice < partes.getCount(); indice++) {
            BodyPart parte = partes.getBodyPart(indice);
            if (parte.isMimeType(tipoMime)) {
                return parte;
            }
            Object conteudo = parte.getContent();
            if (conteudo instanceof Multipart multipart) {
                BodyPart encontrado = localizarParte(multipart, tipoMime);
                if (encontrado != null) {
                    return encontrado;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ObjectProvider<JavaMailSender> mockProvider(JavaMailSender mailSender) {
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);
        return provider;
    }
}