package gui;

import dao.AlunoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Aluno;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        if (!validarAluno()) {
            return;
        }
        try {
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            LocalDate nascimento = dpNascimento.getValue();

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

    private boolean validarAluno() {
        if (txtNome.getText().isEmpty() || txtEmail.getText().isEmpty() || dpNascimento.getValue() == null) {
            mostrarAlerta("Campos obrigatórios", "Preencha todos os campos.");
            return false;
        }

        // Valida Nome (mínimo 3 caracteres)
        if (txtNome.getText().trim().length() < 3) {
            mostrarAlerta("Nome inválido", "O nome deve ter no mínimo 3 caracteres");
            return false;
        }

        // Valida Email (formato básico)
        if (!txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            mostrarAlerta("Email inválido", "Informe um email válido (exemplo: usuario@dominio.com)");
            return false;
        }

        // Valida Data de Nascimento (mínimo 16 anos)
        if (dpNascimento.getValue() == null) {
            mostrarAlerta("Data inválida", "Informe a data de nascimento");
            return false;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate nascimento = dpNascimento.getValue();
        long idade = ChronoUnit.YEARS.between(nascimento, hoje);

        if (idade < 16) {
            mostrarAlerta("Idade inválida", "O aluno deve ter no mínimo 16 anos");
            return false;
        }

        return true;
    }
}
