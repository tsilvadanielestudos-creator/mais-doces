package maisdoces.model;

public class Fornecedor {

    private int id;
    private String nome;
    private String telefone;
    private String cidade;
    private String categoria;

    public Fornecedor() {
    }

    public Fornecedor(int id, String nome, String telefone, String cidade, String categoria) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.cidade = cidade;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return nome;
    }
}
