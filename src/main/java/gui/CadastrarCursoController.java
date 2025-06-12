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
        if (!validarCurso()) {
            return;
        }
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

    private boolean validarCurso() {
        if (txtNome.getText().isEmpty() || txtCargaHoraria.getText().isEmpty() || txtLimiteAlunos.getText().isEmpty()) {
            mostrarAlerta("Campos obrigatórios", "Preencha todos os campos.");
            return false;
        }

        // Valida Nome (mínimo 3 caracteres)
        if (txtNome.getText().trim().length() < 3) {
            mostrarAlerta("Nome inválido", "O nome do curso deve ter no mínimo 3 caracteres");
            return false;
        }

        // Valida Carga Horária (mínimo 20 horas)
        try {
            int carga = Integer.parseInt(txtCargaHoraria.getText().trim());
            if (carga < 20) {
                mostrarAlerta("Carga horária inválida", "A carga horária mínima é 20 horas");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "A carga horária deve ser um número inteiro");
            return false;
        }

        // Valida Limite de Alunos (mínimo 1)
        try {
            int limite = Integer.parseInt(txtLimiteAlunos.getText().trim());
            if (limite < 1) {
                mostrarAlerta("Limite inválido", "O limite de alunos deve ser no mínimo 1");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "O limite de alunos deve ser um número inteiro");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
