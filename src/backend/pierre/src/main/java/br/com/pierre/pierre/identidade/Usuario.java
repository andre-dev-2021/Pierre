package br.com.pierre.pierre.identidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
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

    private String escolaridade;

    private String curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "consentimento_lgpd_em", nullable = false)
    private LocalDateTime consentimentoLgpdEm;

    @Column(name = "versao_termos", nullable = false)
    private String versaoTermos;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_exclusao")
    private LocalDateTime dataExclusao;

    public enum Status {
        ATIVO,
        EXCLUSAO_SOLICITADA,
        EXCLUIDO
    }

    protected Usuario() {
        // exigido pelo JPA
    }

    public Usuario(String nomeCompleto, String email, String senhaHash, String escolaridade, String curso, LocalDateTime consentimentoLgpdEm, String versaoTermos, LocalDateTime agora) {
    this.nomeCompleto = nomeCompleto;
    this.email = email;
    this.senhaHash = senhaHash;
    this.escolaridade = escolaridade;
    this.curso = curso;
    this.consentimentoLgpdEm = consentimentoLgpdEm;
    this.versaoTermos = versaoTermos;
    this.status = Status.ATIVO;
    this.dataCriacao = agora;
    this.dataAtualizacao = agora;
    }

    public void registrarAtualizacao(LocalDateTime agora) {
        this.dataAtualizacao = agora;
    }

    public Integer getCodUsuario() { return codUsuario; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public String getGoogleSub() { return googleSub; }
    public String getEscolaridade() { return escolaridade; }
    public String getCurso() { return curso; }
    public Status getStatus() { return status; }
    public LocalDateTime getConsentimentoLgpdEm() { return consentimentoLgpdEm; }
    public String getVersaoTermos() { return versaoTermos; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public LocalDateTime getDataExclusao() { return dataExclusao; }

    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public void setEmail(String email) { this.email = email; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public void setGoogleSub(String googleSub) { this.googleSub = googleSub; }
    public void setEscolaridade(String escolaridade) { this.escolaridade = escolaridade; }
    public void setCurso(String curso) { this.curso = curso; }
    public void setStatus(Status status) { this.status = status; }
    public void setConsentimentoLgpdEm(LocalDateTime v) { this.consentimentoLgpdEm = v; }
    public void setVersaoTermos(String versaoTermos) { this.versaoTermos = versaoTermos; }
    public void setDataExclusao(LocalDateTime dataExclusao) { this.dataExclusao = dataExclusao; }

}
