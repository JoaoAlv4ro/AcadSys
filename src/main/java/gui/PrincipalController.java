package gui;

import dao.CursoDAO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Curso;

import java.io.IOException;

public class PrincipalController {
    @FXML private TableView<Curso> tabelaCursos;
    @FXML private TableColumn<Curso, Boolean> colStatus;
    @FXML private TableColumn<Curso, String> colNome;
    @FXML private TableColumn<Curso, Integer> colCargaHoraria;
    @FXML private TableColumn<Curso, Integer> colLimiteAlunos;

    @FXML private TextField txtPesquisaCurso;

    private ObservableList<Curso> listaCursos;
    private FilteredList<Curso> listaFiltrada;

    @FXML
    public void initialize() {
        // Configura as colunas
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomeCurso()));
        colCargaHoraria.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCargaHoraria()).asObject());
        colLimiteAlunos.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLimiteAlunos()).asObject());
        colStatus.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isAtivo()).asObject());

        // Inicializa e configura o filtro da barra de pesquisa
        // Isso vai aplicar filtro sempre que o user digitar na pesquisa com addlistener, o que irá alterar a listaFiltrada
        txtPesquisaCurso.textProperty().addListener((obs, antigo, novo) -> {
            if (listaFiltrada != null) {
                listaFiltrada.setPredicate(curso -> {
                    if (novo == null || novo.isEmpty()) return true;
                    return curso.getNomeCurso().toLowerCase().contains(novo.toLowerCase());
                });
            }
        });

        atualizarTabela();
    }

    public void atualizarTabela() {
        // Carrega todos os cursos do banco
        CursoDAO dao = new CursoDAO();
        listaCursos = FXCollections.observableArrayList(dao.getAllCursos());

        // Lista Filtrada usando a original
        listaFiltrada = new FilteredList<>(listaCursos, p -> true);

        // Cria uma lista ordenada para manter a ordenação da tabela
        SortedList<Curso> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tabelaCursos.comparatorProperty());

        // Define a tabela após a ordenação e o filtro
        tabelaCursos.setItems(listaOrdenada);
    }

    @FXML
    public void abrirCadastrarCurso() {
        abrirModal("CadastrarCurso.fxml", "Cadastro de Curso");
        atualizarTabela();
    }

    private void abrirModal(String fxml, String titulo) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(titulo);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
