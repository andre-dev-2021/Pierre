package br.com.pierre.pierre.identidade.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.pierre.pierre.identidade.UsuarioService;
import br.com.pierre.pierre.identidade.dto.CadastroRequest;
import br.com.pierre.pierre.identidade.dto.LoginRequest;
import br.com.pierre.pierre.identidade.dto.UsuarioResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("${api-prefix}")
public class UsuarioController {

    private final UsuarioService service;
    private final SecurityContextRepository securityContextRepository;

    public UsuarioController(UsuarioService service, SecurityContextRepository securityContextRepository) {
        this.service = service;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/auth/cadastro")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody CadastroRequest request){
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse) {
        var usuario = service.login(request);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                usuario.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Troca o identificador de uma sessão anônima antes de autenticá-la.
        if (servletRequest.getSession(false) != null) {
            servletRequest.changeSessionId();
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuarios/me")
    public UsuarioResponse get(Authentication authentication){
        return UsuarioResponse.de(service.buscarPorEmail(authentication.getName()));
    }
}
