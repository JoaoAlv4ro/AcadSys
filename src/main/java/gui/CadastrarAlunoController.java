package gui;

import dao.AlunoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Aluno;
import model.Curso;

import java.time.LocalDate;

public class CadastrarAlunoController {

    @FXML private TextField txtCpf;
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpNascimento;

    private Curso curso;

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @FXML
    private void salvarAluno() {
        try {
            String cpf = txtCpf.getText().trim();
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            LocalDate nascimento = dpNascimento.getValue();

            if (cpf.isEmpty() || nome.isEmpty() || email.isEmpty() || nascimento == null) {
                mostrarAlerta("Campos obrigatórios", "Preencha todos os campos.");
                return;
            }

            Aluno aluno = new Aluno(cpf, nome, email, nascimento, true, curso.getIdCurso());
            new AlunoDAO().createAluno(aluno);

            mostrarAlerta("Sucesso", "Aluno cadastrado com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao cadastrar aluno: " + e.getMessage());
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
        Stage stage = (Stage) txtCpf.getScene().getWindow();
        stage.close();
    }
}
