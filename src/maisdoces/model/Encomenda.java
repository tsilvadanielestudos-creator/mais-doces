package maisdoces.model;

import java.time.LocalDate;

public class Encomenda {

    private int id;
    private LocalDate dataEntrega;
    private String cliente;
    private String produto;
    private int quantidade;
    private String ocasiao;
    private double valor;     // valor cobrado do cliente
    private double custo;     // custo de producao
    private String status;    // Em andamento, Finalizado ou Entregue

    // proximo status na ordem da producao
    public String proximoStatus() {
        if (status.equals("Em andamento")) {
            return "Finalizado";
        }
        return "Entregue";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDate dataEntrega) { this.dataEntrega = dataEntrega; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getOcasiao() { return ocasiao; }
    public void setOcasiao(String ocasiao) { this.ocasiao = ocasiao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public double getCusto() { return custo; }
    public void setCusto(double custo) { this.custo = custo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
