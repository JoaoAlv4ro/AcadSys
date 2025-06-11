package gui;

import dao.AlunoDAO;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Aluno;
import model.Curso;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class AlunosController {

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
    @FXML private TableColumn<Aluno, String> colNascimento;
    @FXML private TableColumn<Aluno, Void> colAcoes;

    private Curso curso;
    private ObservableList<Aluno> listaAlunos;
    private FilteredList<Aluno> listaFiltrada;

    public void setCurso(Curso curso) {
        this.curso = curso;

        // Preenche os dados do curso nos labels
        lblNomeCurso.setText(curso.getNomeCurso());
        lblCargaHoraria.setText(String.valueOf(curso.getCargaHoraria()));
        lblLimiteAlunos.setText(String.valueOf(curso.getLimiteAlunos()));
        lblStatusCurso.setText(curso.isAtivo() ? "Ativo" : "Inativo");

        atualizarTabela();
    }

    @FXML
    public void initialize() {
        // Inicializa filtros
        choiceFiltroStatus.getItems().addAll("Todos", "Ativos", "Inativos");
        choiceFiltroStatus.setValue("Todos");

        // Listeners de filtros
        txtPesquisaAluno.textProperty().addListener((obs, old, novo) -> aplicarFiltro());
        choiceFiltroStatus.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> aplicarFiltro());

        colStatus.setCellFactory(column -> new TableCell<>() {
            private final Label statusLabel = new Label();

            {
                statusLabel.setMinSize(16, 16);
                statusLabel.setMaxSize(16, 16);
                statusLabel.setStyle("-fx-background-radius: 8; -fx-border-color: #ccc; -fx-border-radius: 8;");
                setAlignment(Pos.CENTER);

                statusLabel.setOnMouseClicked(e -> {
                    Aluno aluno = getTableView().getItems().get(getIndex());
                    if (aluno != null) {
                        AlunoDAO dao = new AlunoDAO();
                        if (aluno.isAtivo()) {
                            dao.disableAluno(aluno.getCpf());
                            aluno.setAtivo(false);
                        } else {
                            dao.enableAluno(aluno.getCpf());
                            aluno.setAtivo(true);
                        }
                        getTableView().refresh();
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
                    statusLabel.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 8; -fx-border-color: #000000; -fx-border-radius: 8; -fx-cursor: hand;" );
                    setGraphic(statusLabel);
                }
            }
        });

        // Configura colunas
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colCpf.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCpf()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colNascimento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        );
        colStatus.setCellValueFactory(data -> new SimpleBooleanProperty(data.getValue().isAtivo()).asObject());

        colAcoes.setCellFactory(column -> new TableCell<Aluno, Void>() {
            private final Button btnEditar = new Button();
            private final Button btnExcluir = new Button();
            private final HBox box = new HBox(5);

            {
                // Ícone editar
                ImageView iconEditar = new ImageView(new Image(getClass().getResourceAsStream("/icons/note-pencil.png")));
                iconEditar.setFitWidth(20);
                iconEditar.setFitHeight(20);
                btnEditar.setGraphic(iconEditar);
                btnEditar.setStyle("-fx-background-color: #EFECF1; -fx-cursor: hand; -fx-padding: 2;");

                btnEditar.setOnAction(e -> {
                    Aluno aluno = getTableView().getItems().get(getIndex());
                    if (aluno != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarAluno.fxml"));
                            Parent root = loader.load();

                            EditarAlunoController controller = loader.getController();
                            controller.setAluno(aluno);

                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Editar Aluno");
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.showAndWait();

                            atualizarTabela();
                            aplicarFiltro();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                // Ícone excluir
                ImageView iconExcluir = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/trash.png"))));
                iconExcluir.setFitWidth(20);
                iconExcluir.setFitHeight(20);
                btnExcluir.setGraphic(iconExcluir);
                btnExcluir.setStyle("-fx-background-color: #FECACA; -fx-cursor: hand; -fx-padding: 2;");

                btnExcluir.setOnAction(e -> {
                    Aluno aluno = getTableView().getItems().get(getIndex());
                    if (aluno != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmar exclusão");
                        alert.setHeaderText("Deseja realmente excluir o aluno?");
                        alert.setContentText("Aluno: " + aluno.getNome());

                        alert.showAndWait().ifPresent(resposta -> {
                            if (resposta == ButtonType.OK) {
                                new AlunoDAO().deleteAluno(aluno.getCpf());
                                atualizarTabela();
                            }
                        });
                    }
                });

                box.getChildren().addAll(btnEditar, btnExcluir);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

    }

    private void aplicarFiltro() {
        if (listaFiltrada == null) return;

        String texto = txtPesquisaAluno.getText().toLowerCase();
        String status = choiceFiltroStatus.getValue();

        listaFiltrada.setPredicate(aluno -> {
            boolean correspondeTexto = aluno.getNome().toLowerCase().contains(texto);

            boolean correspondeStatus = switch (status) {
                case "Ativos" -> aluno.isAtivo();
                case "Inativos" -> !aluno.isAtivo();
                default -> true;
            };

            return correspondeTexto && correspondeStatus;
        });
    }

    private void atualizarTabela() {
        if (curso == null) return;

        AlunoDAO dao = new AlunoDAO();
        List<Aluno> alunos = dao.getAlunosByCurso(curso.getIdCurso());
        listaAlunos = FXCollections.observableArrayList(alunos);
        listaFiltrada = new FilteredList<>(listaAlunos, p -> true);
        SortedList<Aluno> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tabelaAlunos.comparatorProperty());

        tabelaAlunos.setItems(listaOrdenada);
    }

    @FXML
    public void abrirCadastrarAluno() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CadastrarAluno.fxml"));
            Parent root = loader.load();

            CadastrarAlunoController controller = loader.getController();
            controller.setCurso(curso); // envia o curso para o aluno ser vinculado

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Cadastrar Aluno");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            atualizarTabela();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
