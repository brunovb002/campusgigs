package com.campusgigs.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    // Hash da senha (BCrypt) — nunca armazenar texto puro
    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String cep;

    // Preenchidos automaticamente via HttpExchange (consulta externa de CEP)
    private String cidade;

    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    @OneToMany(mappedBy = "prestador")
    private List<Servico> servicosPublicados;

    @OneToMany(mappedBy = "contratante")
    private List<Contratacao> contratacoes;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String cep, Papel papel) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cep = cep;
        this.papel = papel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public List<Servico> getServicosPublicados() {
        return servicosPublicados;
    }

    public List<Contratacao> getContratacoes() {
        return contratacoes;
    }
}
