package br.com.pierre.pierre.identidade.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_usuario")
    private Integer codUsuario;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Column(name = "google_sub", unique = true)
    private String googleSub;

    @Column
    private String escolaridade;

    @Column
    private String curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "consentimento_lgpd_em", nullable = false)
    private LocalDateTime consentimentoLGPDEm;

    @Column(name = "versao_termos", nullable = false)
    private String versaoTermos;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_exclusao")
    private LocalDateTime dataExclusao;

    public enum Status {
        ATIVO,
        EXCLUSAO_SOLICITADA,
        EXCLUIDO
    }
}
