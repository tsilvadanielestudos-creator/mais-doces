package maisdoces.model;

public class Funcionario {

    private int id;
    private String nome;
    private String cargo;   // Gerente, Atendente, Confeiteiro ou Financeiro
    private String login;
    private String senha;
    private boolean ativo = true;

    public Funcionario() {
    }

    public Funcionario(int id, String nome, String cargo, String login, String senha, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.ativo = ativo;
    }

    // define quais telas cada cargo pode abrir
    public boolean podeAcessar(String tela) {
        switch (cargo) {
            case "Gerente":
                return true;
            case "Atendente":
                return tela.equals("Vendas") || tela.equals("Produtos") || tela.equals("Clientes");
            case "Confeiteiro":
                return tela.equals("Encomendas") || tela.equals("Produtos");
            case "Financeiro":
                return tela.equals("Vendas") || tela.equals("Caixa") || tela.equals("Fornecedores");
            default:
                return false;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
