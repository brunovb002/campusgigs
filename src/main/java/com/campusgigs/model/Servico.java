package com.campusgigs.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Usuario prestador;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private String categoria;

    // BigDecimal para valores monetários — nunca double/float
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoServico situacao;

    @OneToMany(mappedBy = "servico")
    private List<Contratacao> contratacoes;

    public Servico() {
    }

    public Servico(Usuario prestador, String titulo, String descricao, String categoria, BigDecimal preco) {
        this.prestador = prestador;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.situacao = SituacaoServico.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getPrestador() {
        return prestador;
    }

    public void setPrestador(Usuario prestador) {
        this.prestador = prestador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public SituacaoServico getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoServico situacao) {
        this.situacao = situacao;
    }

    public List<Contratacao> getContratacoes() {
        return contratacoes;
    }
}
