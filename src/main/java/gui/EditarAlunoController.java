package gui;

import dao.AlunoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Aluno;

import java.time.LocalDate;

public class EditarAlunoController {

    @FXML private TextField txtCpf;
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpNascimento;

    private Aluno aluno;

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
        preencherCampos();
    }

    private void preencherCampos() {
        txtCpf.setText(aluno.getCpf());
        txtCpf.setEditable(false); // CPF não pode ser alterado
        txtNome.setText(aluno.getNome());
        txtEmail.setText(aluno.getEmail());
        dpNascimento.setValue(aluno.getDataNascimento());

    }

    @FXML
    private void atualizarAluno() {
        try {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            LocalDate nascimento = dpNascimento.getValue();

            if (nome.isEmpty() || email.isEmpty() || nascimento == null) {
                mostrarAlerta("Campos obrigatórios", "Preencha todos os campos.");
                return;
            }

            aluno.setNome(nome);
            aluno.setEmail(email);
            aluno.setDataNascimento(nascimento);

            new AlunoDAO().updateAluno(aluno);

            mostrarAlerta("Sucesso", "Aluno atualizado com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao atualizar aluno: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void fecharJanela() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
}
