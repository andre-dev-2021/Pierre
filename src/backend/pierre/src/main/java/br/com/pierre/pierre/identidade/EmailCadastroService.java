package br.com.pierre.pierre.identidade;

import org.springframework.stereotype.Service;

import br.com.pierre.pierre.compartilhado.EmailService;
import br.com.pierre.pierre.identidade.entity.Usuario;

@Service
public class EmailCadastroService {

    private final EmailService emailService;

    public EmailCadastroService(EmailService emailService) {
        this.emailService = emailService;
    }

    public boolean enviarRecibo(Usuario usuario) {
        String assunto = "Cadastro realizado no Pierre";
        String conteudo = "Olá, " + usuario.getNomeCompleto() + "!\n\n"
                + "Seu cadastro na plataforma Pierre foi realizado com sucesso.\n"
                + "Este e-mail é apenas um recibo; você já pode acessar sua conta.\n\n"
                + "Equipe Pierre";

        return emailService.enviar(usuario.getEmail(), assunto, conteudo);
    }
}