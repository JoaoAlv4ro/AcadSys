package gui;

import dao.CursoDAO;
import javafx.application.Platform;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Curso;

import java.io.IOException;
import java.util.Objects;

public class PrincipalController {
    @FXML private TableView<Curso> tabelaCursos;
    @FXML private TableColumn<Curso, Boolean> colStatus;
    @FXML private TableColumn<Curso, String> colNome;
    @FXML private TableColumn<Curso, Integer> colCargaHoraria;
    @FXML private TableColumn<Curso, Integer> colLimiteAlunos;
    @FXML private TableColumn<Curso, Void> colAcoes;

    @FXML private TextField txtPesquisaCurso;

    @FXML private ChoiceBox<String> choiceFiltroStatus;

    @FXML private Button btnCadastrarCurso;

    @FXML private Label lblTotalCursos;


    private FilteredList<Curso> listaFiltrada;

    @FXML
    public void initialize() {
        // Configura as colunas
        colStatus.setCellFactory(column -> new TableCell<>() {
            private final Label statusLabel = new Label();

            {
                statusLabel.setMinSize(16, 16);
                statusLabel.setMaxSize(16, 16);
                statusLabel.setStyle("-fx-background-radius: 8;");
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
                    String cor = ativo ? "#65a30d" : "#dc2626";
                    String borda = ativo ? "8" : "2";
                    statusLabel.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: " + borda + "; -fx-border-radius: " + borda + "; -fx-cursor: hand;" );
                    setGraphic(statusLabel);
                }
            }
        });


        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNomeCurso()));
        colCargaHoraria.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCargaHoraria()).asObject());
        colLimiteAlunos.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLimiteAlunos()).asObject());
        colStatus.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isAtivo()).asObject());

        colAcoes.setCellFactory(column -> new TableCell<Curso, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button();
            private final Button btnVisualizar = new Button("Visualizar");
            private final HBox box = new HBox(5);

            {
                // Ícone de edição
                ImageView editIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/note-pencil.png"))));
                editIcon.setFitWidth(20);
                editIcon.setFitHeight(20);
                btnEditar.setGraphic(editIcon);
                btnEditar.setStyle("-fx-background-color: #EFECF1; -fx-cursor: hand; -fx-padding: 2;");

                // Ação de edição
                btnEditar.setOnAction(e -> {
                    Curso curso = getTableView().getItems().get(getIndex());
                    if (!curso.isAtivo()) {
                        mostrarAlerta("Curso Inativo", "Não é possível editar cursos inativos! \nCaso precise editar algo do curso ative nos Status!");
                        return;
                    }
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarCurso.fxml"));
                        Parent root = loader.load();

                        EditarCursoController controller = loader.getController();
                        controller.setCurso(curso);

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setTitle("Editar Curso");
                        stage.showAndWait();

                        aplicarFiltros();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                // Ícone de exclusão
                ImageView iconExcluir = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/trash.png"))));
                iconExcluir.setFitWidth(20);
                iconExcluir.setFitHeight(20);
                btnExcluir.setGraphic(iconExcluir);
                btnExcluir.setStyle("-fx-background-color: #FECACA; -fx-cursor: hand; -fx-padding: 2;");

                // Ação de excluir
                btnExcluir.setOnAction(e -> {
                    Curso curso = getTableView().getItems().get(getIndex());
                    if (curso != null) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Confirmar exclusão");
                        confirm.setHeaderText("Deseja realmente excluir este curso?");
                        confirm.setContentText("Curso: " + curso.getNomeCurso());

                        confirm.showAndWait().ifPresent(resposta -> {
                            if (resposta == ButtonType.OK) {
                                new CursoDAO().deleteCurso(curso.getIdCurso());
                                atualizarTabela();
                                aplicarFiltros();
                            }
                        });
                    }
                });

                // Ícone visualizar
                ImageView iconVisualizar = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/eye.png"))));
                iconVisualizar.setFitWidth(20);
                iconVisualizar.setFitHeight(20);
                btnVisualizar.setGraphic(iconVisualizar);
                btnVisualizar.setStyle("-fx-background-color: #bae6fd; -fx-cursor: hand; -fx-padding: 2;");

                // Ação de visualizar
                btnVisualizar.setOnAction(e -> {
                    Curso curso = getTableView().getItems().get(getIndex());
                    if (curso != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("TelaAlunos.fxml"));
                            Parent root = loader.load();

                            AlunosController controller = loader.getController();
                            controller.setCurso(curso); // Envia o curso

                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Alunos do Curso: " + curso.getNomeCurso());
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.showAndWait();
                            atualizarTabela();
                            aplicarFiltros();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                box.getChildren().addAll(btnVisualizar, btnEditar, btnExcluir);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        choiceFiltroStatus.getItems().addAll("Todos", "Ativos", "Inativos");
        choiceFiltroStatus.setValue("Todos");

        // Listener que observam/checam sempre alterações de filtro em pesquisa e mudança de filtro por status na choicebox.
        choiceFiltroStatus.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> aplicarFiltros());
        txtPesquisaCurso.textProperty().addListener((obs, antigo, novo) -> aplicarFiltros());

        ImageView iconMaisCurso = new ImageView(new Image(getClass().getResourceAsStream("/icons/plus.png")));
        iconMaisCurso.setFitWidth(16);
        iconMaisCurso.setFitHeight(16);

        btnCadastrarCurso.setGraphic(iconMaisCurso);
        btnCadastrarCurso.setContentDisplay(ContentDisplay.LEFT);

        atualizarTabela();
        aplicarFiltros();
    }

    public void atualizarTabela() {
        // Carrega todos os cursos do banco e armazena numa observableList
        CursoDAO dao = new CursoDAO();
        ObservableList<Curso> listaCursos = FXCollections.observableArrayList(dao.getAllCursos());

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

            aplicarFiltros();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        // Obter valor dos componentes
        String textoPesquisa = txtPesquisaCurso.getText().toLowerCase();
        String statusSelecionado = choiceFiltroStatus.getValue();

        Platform.runLater(() -> {
            lblTotalCursos.setText("Total de cadastros: " + listaFiltrada.size());
        });

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

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
