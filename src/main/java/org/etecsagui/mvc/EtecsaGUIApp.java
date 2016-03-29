package org.etecsagui.mvc;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.etecsagui.mvc.view.EtecsaGUIPresenter;
import org.etecsagui.mvc.view.EtecsaGUIView;

/**
 * Author Rigoberto Leander Salgado Reyes <rlsalgado2006@gmail.com>
 * <p/>
 * Copyright 2016 by Rigoberto Leander Salgado Reyes.
 * <p/>
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.
 */
public class EtecsaGUIApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        EtecsaGUIView etecsaGUIView = new EtecsaGUIView();
        new EtecsaGUIPresenter(etecsaGUIView);

        Scene scene = new Scene(etecsaGUIView);
        stage.setScene(scene);
        stage.setTitle("Directorio de ETECSA para María Teresa Reyes Vázquez");

        stage.setMaximized(true);

        stage.setOnCloseRequest(event -> {
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
            alert.showAndWait().filter(b -> b == buttonTypeCancel).ifPresent(e -> event.consume());
        });

        stage.getIcons().add(new Image(
                getClass().getClassLoader().getResource("images/icon.png").toExternalForm()));

        stage.show();
    }
}
