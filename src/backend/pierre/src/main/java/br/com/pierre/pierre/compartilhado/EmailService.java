package br.com.pierre.pierre.compartilhado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean habilitado;
    private final String remetente;

    public EmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${pierre.email.enabled:false}") boolean habilitado,
            @Value("${pierre.email.remetente:}") String remetente) {
        this.mailSenderProvider = mailSenderProvider;
        this.habilitado = habilitado;
        this.remetente = remetente;
    }

    public boolean enviar(String destinatario, String assunto, String texto, String html) {
        if (!habilitado) {
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            logger.warn("Envio de e-mail habilitado, mas nenhum servidor SMTP está configurado.");
            return false;
        }

        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mensagem,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(texto, html);
            if (remetente != null && !remetente.isBlank()) {
                helper.setFrom(remetente);
            }

            mailSender.send(mensagem);
            return true;
        } catch (MailException | MessagingException exception) {
            // Não registrar endereço de e-mail: é um dado pessoal e não é necessário para diagnosticar SMTP indisponível.
            logger.warn("Falha ao enviar e-mail pelo servidor SMTP configurado.");
            return false;
        }
    }
}