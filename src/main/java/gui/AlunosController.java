package gui;

import dao.AlunoDAO;
import dao.CursoDAO;
import javafx.application.Platform;
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
import javafx.stage.FileChooser;
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
    @FXML private HBox boxStatusCurso;

    @FXML private TextField txtPesquisaAluno;
    @FXML private ChoiceBox<String> choiceFiltroStatus;
    @FXML private TableView<Aluno> tabelaAlunos;
    @FXML private TableColumn<Aluno, Boolean> colStatus;
    @FXML private TableColumn<Aluno, String> colNome;
    @FXML private TableColumn<Aluno, String> colCpf;
    @FXML private TableColumn<Aluno, String> colEmail;
    @FXML private TableColumn<Aluno, String> colNascimento;
    @FXML private TableColumn<Aluno, Void> colAcoes;
    @FXML private Button btnCadastrarAluno;
    @FXML private Button btnExportar;
    @FXML private Label lblTotalAlunos;

    private Curso curso;
    private ObservableList<Aluno> listaAlunos;
    private FilteredList<Aluno> listaFiltrada;

    public void setCurso(Curso curso) {
        this.curso = curso;

        // Preenche os dados do curso nos labels
        lblNomeCurso.setText(curso.getNomeCurso());
        lblCargaHoraria.setText(String.valueOf(curso.getCargaHoraria()));
        lblLimiteAlunos.setText(String.valueOf(curso.getLimiteAlunos()));
        lblLimiteAlunos.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand; -fx-underline: true;");
        boxStatusCurso.getChildren().clear();

        Label bolinha = new Label();
        bolinha.setMinSize(16, 16);
        bolinha.setMaxSize(16, 16);
        bolinha.setStyle(
                "-fx-background-color: " + (curso.isAtivo() ? "#65a30d" : "#dc2626") + ";" +
                        "-fx-background-radius: " + (curso.isAtivo() ? "8" : "2") + ";" +
                        "-fx-border-radius: " + (curso.isAtivo() ? "8" : "2") + ";"
        );

        Label texto = new Label(curso.isAtivo() ? "Ativo" : "Inativo");

        boxStatusCurso.getChildren().addAll(bolinha, texto);
        boxStatusCurso.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        atualizarStatusCurso();
        atualizarTabela();
    }

    @FXML
    public void initialize() {
        // Inicializa filtros
        choiceFiltroStatus.getItems().addAll("Todos", "Ativos", "Inativos");
        choiceFiltroStatus.setValue("Todos");

        // Listeners de filtros
        txtPesquisaAluno.textProperty().addListener((obs, old, novo) -> aplicarFiltros());
        choiceFiltroStatus.getSelectionModel().selectedItemProperty().addListener((obs, old, novo) -> aplicarFiltros());

        colStatus.setCellFactory(column -> new TableCell<>() {
            private final Label statusLabel = new Label();

            {
                statusLabel.setMinSize(16, 16);
                statusLabel.setMaxSize(16, 16);
                statusLabel.setStyle("-fx-background-radius: 8;");
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
                    String borda = ativo ? "8" : "2";
                    statusLabel.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: " + borda + "; -fx-border-radius: " + borda + "; -fx-cursor: hand;" );
                    setGraphic(statusLabel);
                }
            }
        });

        // Configura colunas
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        colCpf.setCellValueFactory(data -> {
            String cpfBruto = data.getValue().getCpf();
            String cpfFormatado = formatarCpf(cpfBruto);
            return new SimpleStringProperty(cpfFormatado);
        });
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
                    if(!curso.isAtivo()) {
                        mostrarAlerta("Curso Inativo", "Não é possível editar Alunos em Cursos inativos! \nCaso precise editar algo do curso ative nos Status!");
                        return;
                    }
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
                            aplicarFiltros();
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

        ImageView iconExportar = new ImageView(new Image(getClass().getResourceAsStream("/icons/download-simple.png")));
        iconExportar.setFitWidth(20);
        iconExportar.setFitHeight(20);

        btnExportar.setGraphic(iconExportar);

        ImageView iconMaisCurso = new ImageView(new Image(getClass().getResourceAsStream("/icons/plus.png")));
        iconMaisCurso.setFitWidth(16);
        iconMaisCurso.setFitHeight(16);

        btnCadastrarAluno.setGraphic(iconMaisCurso);
        btnCadastrarAluno.setContentDisplay(ContentDisplay.LEFT);

        lblLimiteAlunos.setOnMouseClicked(e -> {
            if (!curso.isAtivo()) {
                mostrarAlerta("Curso Inativo", "Não é possível editar o limite de alunos em cursos inativos!");
                return;
            }

            TextInputDialog dialog = new TextInputDialog(String.valueOf(curso.getLimiteAlunos()));
            dialog.setTitle("Editar Limite de Alunos");
            dialog.setHeaderText("Digite o novo limite de alunos");
            dialog.setContentText("Limite:");

            dialog.showAndWait().ifPresent(novoLimite -> {
                try {
                    int limite = Integer.parseInt(novoLimite);
                    if (limite < 1) {
                        mostrarAlerta("Valor inválido", "O limite deve ser maior que 0");
                        return;
                    }

                    // Verifica se o novo limite é menor que o número de alunos ativos
                    int alunosAtivos = new AlunoDAO().getAlunosAtivosByCurso(curso.getIdCurso()).size();
                    if (limite < alunosAtivos) {
                        mostrarAlerta("Limite inválido",
                                "O novo limite (" + limite + ") é menor que o número de alunos ativos (" + alunosAtivos + ")");
                        return;
                    }

                    // Atualiza no banco de dados
                    curso.setLimiteAlunos(limite);
                    new CursoDAO().updateCurso(curso);

                    // Atualiza a exibição
                    lblLimiteAlunos.setText(String.valueOf(limite));

                } catch (NumberFormatException ex) {
                    mostrarAlerta("Valor inválido", "Digite um número inteiro válido");
                }
            });
        });

        boxStatusCurso.setOnMouseClicked(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Alterar Status do Curso");
            confirm.setHeaderText("Deseja " + (curso.isAtivo() ? "desativar" : "ativar") + " este curso?");

            confirm.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.OK) {
                    CursoDAO dao = new CursoDAO();
                    if (curso.isAtivo()) {
                        dao.disableCurso(curso.getIdCurso());
                        curso.setAtivo(false);
                    } else {
                        dao.enableCurso(curso.getIdCurso());
                        curso.setAtivo(true);
                    }

                    // Atualiza a exibição
                    atualizarStatusCurso();
                }
            });
        });

        atualizarTabela();
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (listaFiltrada == null) return;

        String texto = txtPesquisaAluno.getText().toLowerCase();
        String status = choiceFiltroStatus.getValue();

        Platform.runLater(() -> {
            lblTotalAlunos.setText("Total de cadastros: " + listaFiltrada.size());
        });

        listaFiltrada.setPredicate(aluno -> {
            boolean correspondeTexto = aluno.getNome().toLowerCase().contains(texto) || aluno.getCpf().toLowerCase().startsWith(texto);
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

        Platform.runLater(() -> {
            lblTotalAlunos.setText("Total de cadastros: " + listaFiltrada.size());
        });
    }

    @FXML
    public void abrirCadastrarAluno() {
        if (!curso.isAtivo()) {
            mostrarAlerta("Curso Inativo", "Não é possível adicionar alunos a cursos inativos! \nCaso precise editar algo do curso ative nos Status!");
            return;
        }
        AlunoDAO dao = new AlunoDAO();
        int alunosDoCurso = dao.getAlunosAtivosByCurso(curso.getIdCurso()).size();

        if (alunosDoCurso >= curso.getLimiteAlunos()) {
            mostrarAlerta("Limite de Alunos Ativos Atingido", "Não é possível cadastrar mais alunos neste curso.\nO limite é de " + curso.getLimiteAlunos() + " aluno(s). \nCaso queira cadastrar novos alunos aumente o limite \nou desative alguns alunos!");
            return;
        }
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

    @FXML
    public void exportarDados() {
        if (curso == null || listaFiltrada == null) return;

        // Monta o conteúdo do arquivo
        StringBuilder sb = new StringBuilder();

        sb.append("=== Dados do Curso ===\n");
        sb.append("Nome: ").append(curso.getNomeCurso()).append("\n");
        sb.append("Carga Horária: ").append(curso.getCargaHoraria()).append("h\n");
        sb.append("Limite de Alunos: ").append(curso.getLimiteAlunos()).append("\n");
        sb.append("Status: ").append(curso.isAtivo() ? "Ativo" : "Inativo").append("\n\n");

        sb.append("=== Alunos (" + (choiceFiltroStatus.getValue()) + ") ===\n");

        if (listaFiltrada.isEmpty()) {
            sb.append("Nenhum aluno correspondente ao filtro.\n");
        } else {
            for (Aluno aluno : listaFiltrada) {
                sb.append("Nome: ").append(aluno.getNome()).append("\n");
                sb.append("CPF: ").append(aluno.getCpf()).append("\n");
                sb.append("Email: ").append(aluno.getEmail()).append("\n");
                sb.append("Nascimento: ").append(aluno.getDataNascimento()).append("\n");
                sb.append("Status: ").append(aluno.isAtivo() ? "Ativo" : "Inativo").append("\n");
                sb.append("-------------------------------\n");
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar arquivo");
        fileChooser.setInitialFileName("Alunos_" + curso.getNomeCurso() + "_" + choiceFiltroStatus.getValue() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos de texto", "*.txt"));

        Stage stage = (Stage) tabelaAlunos.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                java.nio.file.Files.writeString(file.toPath(), sb.toString());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportação Concluída");
                alert.setHeaderText(null);
                alert.setContentText("Arquivo exportado com sucesso:\n" + file.getAbsolutePath());
                alert.showAndWait();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro na Exportação");
                alert.setHeaderText("Não foi possível salvar o arquivo.");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;

        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9, 11);
    }

    private void atualizarStatusCurso() {
        boxStatusCurso.getChildren().clear();

        Label bolinha = new Label();
        bolinha.setMinSize(16, 16);
        bolinha.setMaxSize(16, 16);
        bolinha.setStyle(
                "-fx-background-color: " + (curso.isAtivo() ? "#65a30d" : "#dc2626") + ";" +
                        "-fx-background-radius: " + (curso.isAtivo() ? "8" : "2") + ";" +
                        "-fx-border-radius: " + (curso.isAtivo() ? "8" : "2") + ";" +
                        "-fx-cursor: hand;"
        );

        Label texto = new Label(curso.isAtivo() ? "Ativo" : "Inativo");
        texto.setStyle("-fx-cursor: hand;");

        boxStatusCurso.getChildren().addAll(bolinha, texto);
        boxStatusCurso.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    }
}
