package maisdoces.model;

public class Cliente {

    private int id;
    private String nome;
    private String telefone;
    private String email;
    private String endereco;
    private String observacao;

    public Cliente() {
    }

    public Cliente(int id, String nome, String telefone, String email, String endereco, String observacao) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.observacao = observacao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    // usado para mostrar o nome nas caixas de selecao
    @Override
    public String toString() {
        return nome;
    }
}
