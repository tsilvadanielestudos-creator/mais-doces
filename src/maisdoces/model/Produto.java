package maisdoces.model;

import java.time.LocalDate;
import maisdoces.util.Util;

public class Produto {

    private int id;
    private String nome;
    private String categoria;
    private double preco;
    private int quantidade;
    private LocalDate validade;
    private int fornecedorId;        // 0 = sem fornecedor
    private String fornecedorNome;

    public Produto() {
    }

    public boolean isVencido() {
        return validade != null && validade.isBefore(LocalDate.now());
    }

    public double getValorEstoque() {
        return preco * quantidade;
    }

    public String getSituacao() {
        if (isVencido()) {
            return "Vencido";
        } else if (quantidade <= 10) {
            return "Estoque baixo";
        }
        return "OK";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }
    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }
    public String getFornecedorNome() { return fornecedorNome; }
    public void setFornecedorNome(String fornecedorNome) { this.fornecedorNome = fornecedorNome; }

    @Override
    public String toString() {
        return nome + " - " + Util.moeda(preco) + " (estoque: " + quantidade + ")";
    }
}
