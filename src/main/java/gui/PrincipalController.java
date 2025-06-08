package gui;

import dao.CursoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Curso;

import java.io.IOException;
import java.util.List;

public class PrincipalController {
    @FXML private TableView<Curso> tabelaCursos;
    @FXML private TableColumn<Curso, String> colNome;
    @FXML private TableColumn<Curso, Integer> colCargaHoraria;
    @FXML private TableColumn<Curso, Integer> colLimiteAlunos;
    @FXML private TableColumn<Curso, Boolean> colStatus;
    @FXML private Button btnEditar;

    private ObservableList<Curso> listaCursos;

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNomeCurso()));
        colCargaHoraria.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCargaHoraria()).asObject());
        colLimiteAlunos.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getLimiteAlunos()).asObject());
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isAtivo()).asObject());

        tabelaCursos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnEditar.setDisable(newSel == null);
        });

        atualizarTabela();
    }

    public void atualizarTabela() {
        CursoDAO dao = new CursoDAO();
        List<Curso> cursos = dao.getAllCursos();
        listaCursos = FXCollections.observableArrayList(cursos);
        tabelaCursos.setItems(listaCursos);
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

    @FXML
    public void abrirEditarCurso() {
        // Pega o curso selecionado da Tabela
        Curso cursoSelecionado = tabelaCursos.getSelectionModel().getSelectedItem();


        if(cursoSelecionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarCurso.fxml"));
                Parent root = loader.load();

                // Passa o curso para o controller da tela de edição
                EditarCursoController controller = loader.getController();
                controller.setCurso(cursoSelecionado);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Edição de Curso");
                stage.showAndWait();

                atualizarTabela();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
