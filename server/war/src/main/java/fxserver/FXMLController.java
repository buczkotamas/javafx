package fxserver;

import fx.browser.Window;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;

import java.net.URL;

import java.util.ResourceBundle;

public class FXMLController implements Initializable
{
    @FXML private Label label;

    @Override public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }

    @FXML private void handleButtonAction(ActionEvent event)
    {
        label.setText("Window: " + Window.uuid.toString() + "\nWindow.classLoader.parent: " + Window.class.getClassLoader().getParent());
    }
}
