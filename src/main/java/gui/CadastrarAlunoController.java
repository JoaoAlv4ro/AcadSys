package gui;

import dao.AlunoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Aluno;
import model.Curso;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        if (!validarAluno()) {
            return;
        }
        try {
            String cpf = txtCpf.getText().trim();
            String nome = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            LocalDate nascimento = dpNascimento.getValue();

            Aluno aluno = new Aluno(cpf, nome, email, nascimento, true, curso.getIdCurso());
            new AlunoDAO().createAluno(aluno);

            mostrarAlerta("Sucesso", "Aluno cadastrado com sucesso!");
            fecharJanela();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao cadastrar aluno: " + e.getMessage());
        }
    }

    private boolean validarAluno() {
        if (txtCpf.getText().isEmpty() || txtNome.getText().isEmpty() || txtEmail.getText().isEmpty() || dpNascimento.getValue() == null) {
            mostrarAlerta("Campos obrigatórios", "Preencha todos os campos.");
            return false;
        }

        // Valida Nome (mínimo 3 caracteres)
        if (txtNome.getText().trim().length() < 3) {
            mostrarAlerta("Nome inválido", "O nome deve ter no mínimo 3 caracteres");
            return false;
        }

        // Valida CPF (11 dígitos numéricos)
        String cpf = txtCpf.getText().replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            mostrarAlerta("CPF inválido", "O CPF deve conter 11 dígitos");
            return false;
        }

        // Verifica se CPF já existe
        if (new AlunoDAO().cpfExistente(txtCpf.getText())) {
            mostrarAlerta("CPF existente", "Este CPF já está cadastrado no sistema");
            return false;
        }

        // Validação dos dígitos verificadores
        try {
            // Calcula o primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (10 - i) * Character.getNumericValue(cpf.charAt(i));
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;

            // Calcula o segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (11 - i) * Character.getNumericValue(cpf.charAt(i));
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            // Verifica se os dígitos conferem
            if (Character.getNumericValue(cpf.charAt(9)) != digito1 ||
                    Character.getNumericValue(cpf.charAt(10)) != digito2) {
                mostrarAlerta("CPF inválido", "CPF não é válido (CPF incorreto)");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("CPF inválido", "CPF deve conter apenas números");
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
