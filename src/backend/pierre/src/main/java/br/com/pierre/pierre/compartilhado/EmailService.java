package br.com.pierre.pierre.compartilhado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    public boolean enviar(String destinatario, String assunto, String conteudo) {
        if (!habilitado) {
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            logger.warn("Envio de e-mail habilitado, mas nenhum servidor SMTP está configurado.");
            return false;
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(destinatario);
        mensagem.setSubject(assunto);
        mensagem.setText(conteudo);
        if (remetente != null && !remetente.isBlank()) {
            mensagem.setFrom(remetente);
        }

        try {
            mailSender.send(mensagem);
            return true;
        } catch (MailException exception) {
            // Não registrar endereço de e-mail: é um dado pessoal e não é necessário para diagnosticar SMTP indisponível.
            logger.warn("Falha ao enviar e-mail pelo servidor SMTP configurado.");
            return false;
        }
    }
}