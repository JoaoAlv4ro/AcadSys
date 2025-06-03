package dao;

import factory.ConnectionFactory;
import model.Aluno;

import java.sql.*;

public class AlunoDAO {

    // Cadastrar
    public void createAluno(Aluno aluno) {
        String sql = "INSERT INTO Aluno(cpf, nome_Aluno, email, data_nascimento, ativo_Aluno, id_Curso) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getCpf());
            stmt.setString(2, aluno.getNome());
            stmt.setString(3, aluno.getEmail());
            stmt.setDate(4, Date.valueOf(aluno.getDataNascimento()));
            stmt.setBoolean(5, aluno.isAtivo());
            stmt.setInt(6, aluno.getIdCurso());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar aluno! ",e);
        }
    }

    // Editar
    public void updateAluno(Aluno aluno) {
        String sql = "UPDATE Aluno SET nome_Aluno = ?, email = ?, data_nascimento = ?, ativo_Aluno = ?, id_Curso = ? WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getEmail());
            stmt.setDate(3, Date.valueOf(aluno.getDataNascimento()));
            stmt.setBoolean(4, aluno.isAtivo());
            stmt.setInt(5, aluno.getIdCurso());
            stmt.setString(6, aluno.getCpf());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar aluno", e);
        }
    }

    // Excluir
    public void deleteAluno(String cpf) {
        String sql = "DELETE FROM Aluno WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir aluno!", e);
        }
    }

    // Reabilitar
    public void enableAluno(String cpf) {
        String sql = "UPDATE Aluno SET ativo = true WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao reativar aluno!", e);
        }
    }

    // Desabilitar
    public void disableAluno(String cpf) {
        String sql = "UPDATE Aluno SET ativo = false WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desativar aluno!", e);
        }
    }

    // Consultar aluno por cpf
    public Aluno getAlunoByCPF(int cpfRequisitado) {
        String sql = "SELECT * FROM Aluno WHERE cpf = ?";

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cpfRequisitado);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setId(rs.getInt("id_Aluno"));
                    aluno.setCpf(rs.getString("cpf"));
                    aluno.setNome(rs.getString("nome_Aluno"));
                    aluno.setEmail(rs.getString("email"));
                    aluno.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    aluno.setAtivo(rs.getBoolean("ativo"));
                    aluno.setIdCurso(rs.getInt("idCurso"));
                    return aluno;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar Aluno!", e);
        }
    }

    // Listar apenas ativos
    // SELECT * FROM Aluno WHERE ativo_Aluno = true

    // Listar apenas Inativos
    // SELECT * FROM Aluno WHERE ativoAluno = false

    // Listar todos
    // SELECT * FROM Aluno

    // Listar Aluno por Curso
    // SELECT * FROM Aluno WHERE id_Curso = ?
    // Retorna uma list<Aluno>, possui um paramentro pra busca


}
