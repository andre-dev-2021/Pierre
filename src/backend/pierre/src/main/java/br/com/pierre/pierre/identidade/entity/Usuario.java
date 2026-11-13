package br.com.pierre.pierre.identidade.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Column
    private String escolaridade;

    @Column
    private String curso;

    @Column(nullable = false)
    private String status;

    @Column
    private LocalDateTime consentimentoLGPDEm;

    @Column
    private String versaoTermos;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column
    private LocalDateTime dataAtualizacao;

    @Column
    private LocalDateTime dataExclusao;

    @PrePersist
    public void prePersist() {
        if (this.status == null || this.status.isBlank()) {
            this.status = "ativo";
        }
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
    }
}
