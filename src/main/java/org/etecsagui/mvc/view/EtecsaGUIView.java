package org.etecsagui.mvc.view;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.etecsadao.DB;
import org.etecsagui.mvc.model.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.SQLException;


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
public class EtecsaGUIView extends BorderPane {
    TextField searchTextField;
    Button searchButton;
    DB db = null;
    HBox topPanel;
    MenuItem chooseDb;
    MenuItem exit;
    MenuItem about;
    VBox centerPanel;
    Configuration conf;

    public EtecsaGUIView() {
        loadConfigurations();

        if (!conf.getUrl().isEmpty()) {
            try {
                db = new DB(conf.getUrl());
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                Stage stage1 = (Stage) alert.getDialogPane().getScene().getWindow();
                alert.getDialogPane().setPrefSize(430, 80);
                ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.setHeaderText("Error");
                alert.getButtonTypes().setAll(buttonTypeCancel);
                stage1.getIcons().add(new Image(
                        getClass().getClassLoader().getResource("images/icon.png").toExternalForm()));
                alert.setTitle("Error");
                alert.showAndWait();
            }
        }

        createMenus();
        createCenterPanel();
    }

    private void createCenterPanel() {
        topPanel = new HBox(10);

        searchTextField = new TextField();
        searchTextField.setPromptText("Escribe parte del número o parte del nombre o de la dirección a buscar");
        searchTextField.setPrefWidth(550);

        searchButton = new Button("Buscar");
        Platform.runLater(() -> searchButton.requestFocus());

        Label expander1 = new Label();
        expander1.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(expander1, Priority.ALWAYS);

        Label expander2 = new Label();
        expander2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(expander2, Priority.ALWAYS);

        topPanel.getChildren().addAll(expander1, searchTextField, searchButton, expander2);

        centerPanel = new VBox();

        VBox.setMargin(topPanel, new Insets(8, 8, 8, 8));
        centerPanel.getChildren().addAll(topPanel);

        setCenter(centerPanel);
    }

    private void loadConfigurations() {
        try (FileReader fr = new FileReader(new File("config.json"))) {
            Gson g = new Gson();
            conf = g.fromJson(fr, Configuration.class);
        } catch (Exception ex) {
        } finally {
            if (conf == null) {
                conf = new Configuration();
            }
        }
    }

    public void saveConfigurations(String url) {
        try (FileWriter fw = new FileWriter(new File("config.json"))) {
            conf.setUrl(url);
            new Gson().toJson(conf, fw);
        } catch (Exception ex) {
        }
    }

    private void createMenus() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("_Archivo");
        file.setMnemonicParsing(true);
        chooseDb = new MenuItem("Seleccionar directorio telefónico");

        exit = new MenuItem("Salir");

        file.getItems().addAll(chooseDb, new SeparatorMenuItem(), exit);

        Menu help = new Menu("_?");
        help.setMnemonicParsing(true);
        about = new MenuItem("Acerca de");

        help.getItems().add(about);

        menuBar.getMenus().addAll(file, help);

        setTop(menuBar);
    }
}
