package br.com.pierre.pierre.identidade;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateEngineException;

import br.com.pierre.pierre.compartilhado.EmailService;
import br.com.pierre.pierre.identidade.entity.Usuario;

@Service
public class EmailCadastroService {

    private static final Logger logger = LoggerFactory.getLogger(EmailCadastroService.class);

    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public EmailCadastroService(EmailService emailService, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    public boolean enviarEmailCOnfirmacao(Usuario usuario) {
        Context contexto = new Context(Locale.forLanguageTag("pt-BR"));
        contexto.setVariable("nome", usuario.getNomeCompleto());

        try {
            String html = templateEngine.process("email/confirmacao-html", contexto);
            String texto = templateEngine.process("email/confirmacao-texto", contexto);

            return emailService.enviar(
                    usuario.getEmail(),
                    "Seja bem-vindo ao Pierre!",
                    texto,
                    html);
        } catch (TemplateEngineException exception) {
            logger.warn("Falha ao renderizar o template de confirmação do cadastro.");
            return false;
        }
    }
}