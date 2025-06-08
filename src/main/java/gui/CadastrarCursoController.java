package gui;

import dao.CursoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Curso;

public class CadastrarCursoController {
    @FXML private TextField txtNome;
    @FXML private TextField txtCargaHoraria;
    @FXML private TextField txtLimiteAlunos;

    @FXML
    public void salvarCurso() {
        try {
            String nome = txtNome.getText().trim();
            int carga = Integer.parseInt(txtCargaHoraria.getText().trim());
            int limite = Integer.parseInt(txtLimiteAlunos.getText().trim());

            Curso curso = new Curso(nome, carga, limite, true);
            CursoDAO.createCurso(curso);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Sucesso");
            alert.setContentText("Curso cadastrado com sucesso!");
            alert.showAndWait();

            // Fecha a janela após o cadastro
            Stage stage = (Stage) txtNome.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro");
            alert.setContentText("Carga horária e limite devem ser números inteiros.");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao salvar");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
