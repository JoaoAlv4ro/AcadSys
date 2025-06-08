package gui;

import dao.CursoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Curso;

public class EditarCursoController {
    @FXML private TextField txtNome;
    @FXML private TextField txtCargaHoraria;
    @FXML private TextField txtLimiteAlunos;

    private Curso curso; // Curso recebido da tela principal

    public void setCurso(Curso curso) {
        this.curso = curso;
        preencherCampos();
    }

    private void preencherCampos() {
        txtNome.setText(curso.getNomeCurso());
        txtCargaHoraria.setText(String.valueOf(curso.getCargaHoraria()));
        txtLimiteAlunos.setText(String.valueOf(curso.getLimiteAlunos()));
    }

    @FXML
    public void atualizarCurso() {
        try {
            String nome = txtNome.getText().trim();
            int carga = Integer.parseInt(txtCargaHoraria.getText().trim());
            int limite = Integer.parseInt(txtLimiteAlunos.getText().trim());

            curso.setNomeCurso(nome);
            curso.setCargaHoraria(carga);
            curso.setLimiteAlunos(limite);

            CursoDAO dao = new CursoDAO();
            dao.updateCurso(curso);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sucesso");
            alert.setContentText("Curso atualizado com sucesso!");
            alert.showAndWait();

            Stage stage = (Stage) txtNome.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText("Carga horária e limite devem ser números inteiros.");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao atualizar");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
