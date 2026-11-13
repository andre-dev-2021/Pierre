package br.com.pierre.pierre.identidade;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.pierre.pierre.identidade.dto.CadastroRequest;
import br.com.pierre.pierre.identidade.dto.LoginRequest;
import br.com.pierre.pierre.identidade.entity.Usuario;
import br.com.pierre.pierre.identidade.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailCadastroService emailCadastroService;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            EmailCadastroService emailCadastroService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailCadastroService = emailCadastroService;
    }

    public Usuario login(LoginRequest request) {
        return repository.findByEmailIgnoreCase(request.email())
                .filter(candidato -> "ativo".equalsIgnoreCase(candidato.getStatus()))
                .filter(candidato -> passwordEncoder.matches(request.senha(), candidato.getSenhaHash()))
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos."));
    }

    public boolean save(CadastroRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        if (Boolean.TRUE.equals(request.consentimentoLGPD())) {
            Usuario usuario = new Usuario();
            usuario.setNomeCompleto(request.nomeCompleto());
            usuario.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
            usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
            usuario.setStatus("ativo");
            usuario.setDataCriacao(LocalDateTime.now());
            usuario.setConsentimentoLGPDEm(LocalDateTime.now());
    
            Usuario usuarioSalvo = repository.save(usuario);
            // O envio ocorre após repository.save retornar, para que falha SMTP não reverta o cadastro.
            return emailCadastroService.enviarRecibo(usuarioSalvo);
        }else{
            throw new IllegalArgumentException("É preciso aceitar os termos de consentimento!");
        }
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Usuário autenticado não encontrado."));
    }
}
