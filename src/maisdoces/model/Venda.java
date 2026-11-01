package maisdoces.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private int id;
    private Cliente cliente;          // null = venda de balcao
    private String clienteNome;
    private int funcionarioId;
    private LocalDate data = LocalDate.now();
    private LocalDate dataEntrega;    // so para encomendas
    private String formaPagamento;
    private String status;            // Pago ou Pendente
    private String ocasiao = "Nenhuma";
    private double total;
    private List<ItemVenda> itens = new ArrayList<>();

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        calcularTotal();
    }

    public void calcularTotal() {
        total = 0;
        for (ItemVenda item : itens) {
            total += item.getSubtotal();
        }
    }

    // encomenda = tem cliente e data de entrega
    public boolean isEncomenda() {
        return cliente != null && dataEntrega != null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalDate getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDate dataEntrega) { this.dataEntrega = dataEntrega; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOcasiao() { return ocasiao; }
    public void setOcasiao(String ocasiao) { this.ocasiao = ocasiao; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<ItemVenda> getItens() { return itens; }
}
