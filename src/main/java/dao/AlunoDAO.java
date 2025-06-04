package dao;

import factory.ConnectionFactory;
import model.Aluno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "UPDATE Aluno SET ativo_Aluno = true WHERE cpf = ?";

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
        String sql = "UPDATE Aluno SET ativo_Aluno = false WHERE cpf = ?";

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
                    aluno.setAtivo(rs.getBoolean("ativo_Aluno"));
                    aluno.setIdCurso(rs.getInt("id_Curso"));
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
    public List<Aluno> getAlunosAtivos() {
        String sql = "SELECT * FROM Aluno WHERE ativo_Aluno = true";
        List<Aluno> alunos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while(rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id_Aluno"));
                aluno.setCpf(rs.getString("cpf"));
                aluno.setNome(rs.getString("nome_Aluno"));
                aluno.setEmail(rs.getString("email"));
                aluno.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                aluno.setAtivo(rs.getBoolean("ativo_Aluno"));
                aluno.setIdCurso(rs.getInt("id_Curso"));
                alunos.add(aluno);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Alunos ativos!", e);
        }

        return alunos;
    }


    // Listar apenas Inativos
    public List<Aluno> getAlunosInativos() {
        String sql = "SELECT * FROM Aluno WHERE ativo_Aluno = false";
        List<Aluno> alunos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while(rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id_Aluno"));
                aluno.setCpf(rs.getString("cpf"));
                aluno.setNome(rs.getString("nome_Aluno"));
                aluno.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                aluno.setAtivo(rs.getBoolean("ativo_Aluno"));
                aluno.setIdCurso(rs.getInt("id_Curso"));
                alunos.add(aluno);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Alunos inativos!", e);
        }

        return alunos;
    }


    // Listar todos
    public List<Aluno> getAllAlunos() {
        String sql = "SELECT * FROM Aluno";
        List<Aluno> alunos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while(rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id_Aluno"));
                aluno.setCpf(rs.getString("cpf"));
                aluno.setNome(rs.getString("nome_Aluno"));
                aluno.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                aluno.setAtivo(rs.getBoolean("ativo_Aluno"));
                aluno.setIdCurso(rs.getInt("id_Curso"));
                alunos.add(aluno);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Alunos!", e);
        }
        return alunos;
    }

    // Listar Aluno por Curso
    public List<Aluno> getAlunosByCurso(int idCursoRequisitado) {
        String sql = "SELECT * FROM Aluno WHERE id_Curso = ?";
        List<Aluno> alunos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            stmt.setInt(1, idCursoRequisitado);

            try (ResultSet rs = stmt.executeQuery()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id_Aluno"));
                aluno.setId(rs.getInt("id_Aluno"));
                aluno.setCpf(rs.getString("cpf"));
                aluno.setNome(rs.getString("nome_Aluno"));
                aluno.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                aluno.setAtivo(rs.getBoolean("ativo_Aluno"));
                aluno.setIdCurso(rs.getInt("id_Curso"));
                alunos.add(aluno);

            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar Alunos por Curso!", e);
        }

        return alunos;
    }
}
