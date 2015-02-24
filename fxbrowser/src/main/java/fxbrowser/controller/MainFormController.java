package fxbrowser.controller;

import fxbrowser.HTTPClassLoader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author tbuczko
 */
public class MainFormController implements Initializable
{
    @FXML private Button btnGo;
    @FXML private Tab tab0;
    @FXML private Tab tabAdd;
    @FXML private TabPane tabPane;
    @FXML private TextField txtAddress;

    /**
     * Initializes the controller class.
     */
    @Override public void initialize(URL url, ResourceBundle rb)
    {
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
            {
                @Override public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
                {
                    if (newValue == tabAdd)
                    {
                        tabPane.getTabs().remove(tabAdd);
                        Tab newTab = new Tab("New Tab");

                        newTab.setClosable(true);
                        tabPane.getTabs().add(new Tab("New Tab"));
                        tabPane.getSelectionModel().select(newTab);
                        tabPane.getTabs().add(tabAdd);
                    }
                }
            });
    }

    @FXML private void go(ActionEvent event)
    {
        try
        {
            URL url = new URL(txtAddress.getText());
            FXMLLoader loader = new FXMLLoader();

            loader.setClassLoader(new HTTPClassLoader(FXMLLoader.getDefaultClassLoader(), url));
            loader.setLocation(url);
            Node node = loader.load();

            tabPane.getSelectionModel().getSelectedItem().setContent(node);
        }
        catch (Exception ex)
        {
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader();
                Node node = fxmlLoader.load(getClass().getResourceAsStream("/fxml/error.fxml"));
                ErrorController controller = (ErrorController) fxmlLoader.getController();

                controller.setException(ex);
                tabPane.getSelectionModel().getSelectedItem().setContent(node);
            }
            catch (IOException ex1)
            {
                Logger.getLogger(MainFormController.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
}
