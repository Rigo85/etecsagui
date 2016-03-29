package org.etecsagui.mvc.view;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.etecsadao.DB;
import org.etecsagui.mvc.model.SearchService;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Author Rigoberto Leander Salgado Reyes <rlsalgado2006@gmail.com>
 * <p>
 * Copyright 2016 by Rigoberto Leander Salgado Reyes.
 * <p>
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.
 */
public class EtecsaGUIPresenter {
    EtecsaGUIView etecsaGUIView;
    SearchService searchService;

    public EtecsaGUIPresenter(EtecsaGUIView etecsaGUIView) {
        this.etecsaGUIView = etecsaGUIView;

        searchService = new SearchService();

        this.etecsaGUIView.searchButton.setOnAction(e -> searchAction());

        this.etecsaGUIView.searchButton.disableProperty().bind(searchService.stateProperty().isEqualTo(Worker.State.RUNNING));
        this.etecsaGUIView.searchTextField.disableProperty().bind(searchService.stateProperty().isEqualTo(Worker.State.RUNNING));

        this.etecsaGUIView.searchTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchAction();
            }
        });

        aboutAction();

        exitAction();

        chooseDbAction(etecsaGUIView);
    }

    private void chooseDbAction(EtecsaGUIView etecsaGUIView) {
        this.etecsaGUIView.chooseDb.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar el directorio telefónico");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

            File file = fileChooser.showOpenDialog(etecsaGUIView.getScene().getWindow());
            if (file != null) {
                try {
                    if (etecsaGUIView.db == null)
                        etecsaGUIView.db = new DB(file.getAbsolutePath());
                    else
                        etecsaGUIView.db.setUrl(file.getAbsolutePath());

                    etecsaGUIView.saveConfigurations(file.getAbsolutePath());
                } catch (SQLException e1) {
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                alert.setResizable(true);
                ButtonType buttonOk = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(buttonOk);
                stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/icon.png").toExternalForm()));
                alert.setHeaderText("Información");
                alert.setContentText("¡NO SELECCIONASTE NADA!");
                alert.setTitle("Información");
                alert.showAndWait();
            }
        });
    }

    private void exitAction() {
        this.etecsaGUIView.exit.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Intentaste salir de la aplicación, ¿es así o fue un dedazo mal dado?");
            Stage stage1 = (Stage) alert.getDialogPane().getScene().getWindow();
            alert.getDialogPane().setPrefSize(430, 80);
            ButtonType buttonTypeCancel = new ButtonType("No, fue un dedazo", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeOk = new ButtonType("Sí, quiero salir", ButtonBar.ButtonData.YES);
            alert.setHeaderText("Confirmación");
            alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeOk);
            stage1.getIcons().add(new Image(
                    getClass().getClassLoader().getResource("images/icon.png").toExternalForm()));
            alert.setTitle("Confirmación");
            alert.showAndWait().filter(b -> b == buttonTypeOk).ifPresent(e -> Platform.exit());
        });
    }

    private void aboutAction() {
        this.etecsaGUIView.about.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(600, 300);
            ButtonType buttonOk = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(buttonOk);
            stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/icon.png").toExternalForm()));
            alert.setHeaderText("Directorio ETECSA para Teresa");
            alert.setContentText("Author Rigoberto Leander Salgado Reyes <rlsalgado2006@gmail.com>" +
                    "\n\n" +
                    "Copyright 2016 by Rigoberto Leander Salgado Reyes." +
                    "\n" +
                    "This program is licensed to you under the terms of version 3 of the\n" +
                    "GNU Affero General Public License. This program is distributed WITHOUT\n" +
                    "ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,\n" +
                    "MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the\n" +
                    "AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.");
            alert.setTitle("Acerca de");
            alert.showAndWait();
        });
    }

    private void searchAction() {
        if (etecsaGUIView.searchTextField.getText().trim().isEmpty() || etecsaGUIView.db == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Viejita, " + (etecsaGUIView.db == null ? "debes seleccionar un directorio telefónico" : "debes escribir algo para buscar"));
            Stage stage1 = (Stage) alert.getDialogPane().getScene().getWindow();
            alert.getDialogPane().setPrefSize(430, 80);
            ButtonType buttonTypeCancel = new ButtonType("Si, " + (etecsaGUIView.db == null ? "deja seleccionar un directorio" : "deja escribir algo"), ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.setHeaderText("Confirmación");
            alert.getButtonTypes().setAll(buttonTypeCancel);
            stage1.getIcons().add(new Image(
                    getClass().getClassLoader().getResource("images/icon.png").toExternalForm()));
            alert.setTitle("Confirmación");
            alert.showAndWait();
        } else {

            searchService.setDb(etecsaGUIView.db);
            searchService.setPartiaQuery(etecsaGUIView.searchTextField.getText());

            searchService.setOnSucceeded(e -> createTables((List<List<Map<String, String>>>) e.getSource().getValue()));

            if (searchService.getState() == Worker.State.SUCCEEDED ||
                    searchService.getState() == Worker.State.FAILED ||
                    searchService.getState() == Worker.State.CANCELLED) searchService.reset();
            searchService.start();
        }
    }

    private void createTables(List<List<Map<String, String>>> objsList) {
        if (etecsaGUIView.centerPanel.getChildren().size() > 1)
            etecsaGUIView.centerPanel.getChildren().remove(1, etecsaGUIView.centerPanel.getChildren().size());
        objsList.stream().forEach(list -> {
            TableView tableView = createTable(list);

            VBox.setMargin(tableView, new Insets(0, 8, 8, 8));
            VBox.setVgrow(tableView, Priority.ALWAYS);

            etecsaGUIView.centerPanel.getChildren().add(tableView);
        });
    }

    private TableView createTable(List<Map<String, String>> objs) {
        TableView tableView = new TableView();

        objs.get(0).keySet().stream().forEach(key -> {
            TableColumn col = new TableColumn<>(key.substring(0, 1).toUpperCase() + key.substring(1));
            col.setCellValueFactory(new MapValueFactory<>(key));
            tableView.getColumns().add(col);
        });

        tableView.getItems().addAll(objs);
        return tableView;
    }
}
