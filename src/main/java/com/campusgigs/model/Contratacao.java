package com.campusgigs.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contratacoes")
public class Contratacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contratante_id", nullable = false)
    private Usuario contratante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoContratacao situacao;

    @Column(name = "data_contratacao", nullable = false)
    private LocalDateTime dataContratacao;

    public Contratacao() {
    }

    public Contratacao(Servico servico, Usuario contratante) {
        this.servico = servico;
        this.contratante = contratante;
        this.situacao = SituacaoContratacao.SOLICITADA;
        this.dataContratacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Usuario getContratante() {
        return contratante;
    }

    public void setContratante(Usuario contratante) {
        this.contratante = contratante;
    }

    public SituacaoContratacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoContratacao situacao) {
        this.situacao = situacao;
    }

    public LocalDateTime getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(LocalDateTime dataContratacao) {
        this.dataContratacao = dataContratacao;
    }
}
