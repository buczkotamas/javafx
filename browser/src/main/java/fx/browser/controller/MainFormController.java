package fx.browser.controller;

import fx.browser.TabClassLoader;
import fx.browser.WindowInterface;

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

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

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
    private static final Logger logger = Logger.getLogger(MainFormController.class.getName());
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
        HBox.setHgrow(txtAddress, Priority.ALWAYS);
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
            {
                @Override public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
                {
                    if (newValue == tabAdd)
                    {
                        try
                        {
                            TabClassLoader tabClassLoader = new TabClassLoader();
                            Class winClass = tabClassLoader.loadClass("fx.browser.Window");

                            System.out.println("Window.newInstance()");
                            Tab newTab = (Tab) winClass.newInstance();

                            newTab.setClosable(true);
                            tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTab);
                            tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    @FXML private void go(ActionEvent event)
    {
        try
        {
            URL url = new URL(txtAddress.getText());

            ((WindowInterface) tabPane.getSelectionModel().getSelectedItem()).setLocation(url);
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
