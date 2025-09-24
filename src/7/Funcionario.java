public class Funcionario implements Identificavel<String> {
    private String id;
    private String nome;
    private String departamento;

    public Funcionario(String id, String nome, String departamento) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public String toString() {
        return "Funcionario{id='" + id + "', nome='" + nome + "', departamento='" + departamento + "'}";
    }
}
