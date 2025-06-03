package dao;

import factory.ConnectionFactory;
import model.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    // Cadastrar
    public static void createCurso(Curso curso) {
        String sql = "INSERT INTO Curso(nome_Curso, carga_horaria, limite_alunos, ativo) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, curso.getNomeCurso());
            stmt.setInt(2, curso.getCargaHoraria());
            stmt.setInt(3, curso.getLimiteAlunos());
            stmt.setBoolean(4, curso.isAtivo());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar curso!", e);
        }
    }

    // Editar
    public void updateCurso(Curso curso) {
        String sql = "UPDATE Curso SET nome_Curso = ?, carga_horaria = ?, limite_alunos = ?, ativo = ? WHERE idCurso = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, curso.getNomeCurso());
            stmt.setInt(2, curso.getCargaHoraria());
            stmt.setInt(3, curso.getLimiteAlunos());
            stmt.setBoolean(4, curso.isAtivo());
            stmt.setInt(5, curso.getIdCurso());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar curso!", e);
        }
    }

    // Deletar
    public void deleteCurso(int idCurso) {
        String sql = "DELETE FROM Curso WHERE id_Curso = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir curso!", e);
        }
    }

    // Desabilitar
    public void disableCurso(int idCurso) {
        String sql = "UPDATE Curso SET ativo = false WHERE idCurso = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desativar curso!", e);
        }
    }

    // Reabilitar
    public void enableCurso(int idCurso) {
        String sql = "UPDATE Curso SET ativo = true WHERE idCurso = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCurso);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao reativar curso!", e);
        }
    }

    // Consultar curso por id
    public Curso getCursoByID(int idRequisitado) {
        String sql = "SELECT * FROM Curso WHERE id_Curso = ?";

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRequisitado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Curso cursoEncontrado = new Curso();
                    cursoEncontrado.setIdCurso(rs.getInt("id_Curso"));
                    cursoEncontrado.setNomeCurso(rs.getString("nome_Curso"));
                    cursoEncontrado.setCargaHoraria(rs.getInt("carga_horaria"));
                    cursoEncontrado.setLimiteAlunos(rs.getInt("limite_alunos"));
                    cursoEncontrado.setAtivo(rs.getBoolean("ativo_Curso"));
                    return cursoEncontrado;
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar Cursos!", e);
        }
    }

    // Listar todos os cursos
    public List<Curso> getAllCursos() {
        String sql = "SELECT * FROM Curso";
        List<Curso> cursos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("id_Curso"));
                curso.setNomeCurso(rs.getString("nome_Curso"));
                curso.setCargaHoraria(rs.getInt("carga_horaria"));
                curso.setLimiteAlunos(rs.getInt("limite_alunos"));
                curso.setAtivo(rs.getBoolean("ativo_Curso"));
                cursos.add(curso);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Cursos",e);
        }
        return cursos;
    }

    // Listar apenas ativos
    public List<Curso> getCursosAtivos() {
        String sql = "SELECT * FROM Curso WHERE ativo_Curso = true";
        List<Curso> cursos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){

            while (rs.next()) {
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("id_Curso"));
                curso.setNomeCurso(rs.getString("nome_Curso"));
                curso.setCargaHoraria(rs.getInt("carga_horaria"));
                curso.setLimiteAlunos(rs.getInt("limite_alunos"));
                curso.setAtivo(rs.getBoolean("ativo_Curso"));
                cursos.add(curso);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao encontrar Cursos ativos!", e);
        }
        return cursos;
    }
}
