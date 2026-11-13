package br.com.pierre.pierre.identidade.dto;

import br.com.pierre.pierre.identidade.entity.Usuario;

public record UsuarioResponse(
        Integer codUsuario,
        String nomeCompleto,
        String email,
        String escolaridade,
        String curso
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getCodUsuario(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getEscolaridade(),
                usuario.getCurso());
    }
}