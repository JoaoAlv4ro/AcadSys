package gui;

import dao.AlunoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Aluno;
import model.Curso;

import java.time.LocalDate;
import java.util.List;

public class AlunosController {
    @FXML private Label lblTituloCurso;
    @FXML private Label lblNomeCurso;
    @FXML private Label lblCargaHoraria;
    @FXML private Label lblLimiteAlunos;
    @FXML private Label lblStatusCurso;

    @FXML private TextField txtPesquisaAluno;
    @FXML private ChoiceBox<String> choiceFiltroStatus;

    @FXML private TableView<Aluno> tabelaAlunos;
    @FXML private TableColumn<Aluno, Boolean> colStatus;
    @FXML private TableColumn<Aluno, String> colNome;
    @FXML private TableColumn<Aluno, String> colCpf;
    @FXML private TableColumn<Aluno, String> colEmail;
    @FXML private TableColumn<Aluno, LocalDate> colNascimento;
    @FXML private TableColumn<Aluno, Void> colAcoes;

    private Curso curso;
    private ObservableList<Aluno> listaAlunos;

    public void setCurso(Curso curso) {
        this.curso = curso;
        preencherDadosDoCurso();
        carregarAlunosDoCurso();
    }

    private void preencherDadosDoCurso() {
        lblTituloCurso.setText("Alunos do Curso: " + curso.getNomeCurso());
        lblNomeCurso.setText(curso.getNomeCurso());
        lblCargaHoraria.setText(curso.getCargaHoraria() + "h");
        lblLimiteAlunos.setText(String.valueOf(curso.getLimiteAlunos()));
        lblStatusCurso.setText(curso.isAtivo() ? "Ativo" : "Inativo");
    }

    private void carregarAlunosDoCurso() {
        AlunoDAO dao = new AlunoDAO();
        List<Aluno> alunos = dao.getAlunosByCurso(curso.getIdCurso());
        listaAlunos = FXCollections.observableArrayList(alunos);
        tabelaAlunos.setItems(listaAlunos);

        choiceFiltroStatus.getItems().addAll("Todos", "Ativos", "Inativos");
        choiceFiltroStatus.setValue("Todos");


    }

}
