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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @FXML private ChoiceBox<String> choiceFiltroStatus;


    private ObservableList<Curso> listaCursos;
    private FilteredList<Curso> listaFiltrada;

    @FXML
    public void initialize() {
        // Configura as colunas
        colStatus.setCellFactory(column -> new TableCell<>() {
            private final Label statusLabel = new Label();

            {
                statusLabel.setMinSize(16, 16);
                statusLabel.setMaxSize(16, 16);
                statusLabel.setStyle("-fx-background-radius: 8; -fx-border-color: #000000; -fx-border-radius: 8;");
                setAlignment(Pos.CENTER);

                // Ouvinte de clique configurado apenas uma vez
                statusLabel.setOnMouseClicked(e -> {
                    Curso curso = getTableView().getItems().get(getIndex());

                    if (curso != null) {
                        CursoDAO dao = new CursoDAO();
                        if (curso.isAtivo()) {
                            dao.disableCurso(curso.getIdCurso());
                            curso.setAtivo(false);
                        } else {
                            dao.enableCurso(curso.getIdCurso());
                            curso.setAtivo(true);
                        }

                        aplicarFiltros();
                    }
                });
            }

            @Override
            protected void updateItem(Boolean ativo, boolean empty) {
                super.updateItem(ativo, empty);

                if (empty || ativo == null) {
                    setGraphic(null);
                } else {
                    String cor = ativo ? "green" : "red";
                    statusLabel.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 8; -fx-border-color: #000000; -fx-border-radius: 8; -fx-cursor: hand;" );
                    setGraphic(statusLabel);
                }
            }
        });


        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomeCurso()));
        colCargaHoraria.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCargaHoraria()).asObject());
        colLimiteAlunos.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLimiteAlunos()).asObject());
        colStatus.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isAtivo()).asObject());

        choiceFiltroStatus.getItems().addAll("Todos", "Ativos", "Inativos");
        choiceFiltroStatus.setValue("Todos");

        // Listener que observam/checam sempre alterações de filtro em pesquisa e mudança de filtro por status na choicebox.
        choiceFiltroStatus.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> aplicarFiltros());
        txtPesquisaCurso.textProperty().addListener((obs, antigo, novo) -> aplicarFiltros());

        atualizarTabela();
    }

    public void atualizarTabela() {
        // Carrega todos os cursos do banco e armazena numa observableList
        CursoDAO dao = new CursoDAO();
        listaCursos = FXCollections.observableArrayList(dao.getAllCursos());

        // Lista Filtrada usando a original
        listaFiltrada = new FilteredList<>(listaCursos, p -> true);

        // Cria uma lista ordenada para manter a ordenação da tabela
        SortedList<Curso> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tabelaCursos.comparatorProperty());

        // Define a tabela após a ordenação e os filtros do listener
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

    private void aplicarFiltros() {
        // Obter valor dos componentes
        String textoPesquisa = txtPesquisaCurso.getText().toLowerCase();
        String statusSelecionado = choiceFiltroStatus.getValue();

        // Setagem de filtros
        listaFiltrada.setPredicate(curso -> {
            boolean filtroPesquisa = curso.getNomeCurso().toLowerCase().contains(textoPesquisa);

            boolean filtroStatus = switch (statusSelecionado) {
                case "Ativos" -> curso.isAtivo();
                case "Inativos" -> !curso.isAtivo();
                default -> true; // "Todos"
            };

            return filtroPesquisa && filtroStatus;
        });
    }
}
