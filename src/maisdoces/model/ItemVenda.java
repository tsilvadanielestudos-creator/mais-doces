package maisdoces.model;

public class ItemVenda {

    private Produto produto;
    private int quantidade;
    private double preco;

    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.preco = produto.getPreco();
    }

    public double getSubtotal() {
        return preco * quantidade;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getPreco() { return preco; }
}
