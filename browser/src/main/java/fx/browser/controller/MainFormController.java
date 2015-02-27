package fx.browser.controller;

import fx.browser.Browser;
import fx.browser.HttpClassLoader;

import fx.browser.dialog.LoginDialog;

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

import javafx.util.Pair;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.Optional;
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
                        Tab newTab = new Tab("New Tab");

                        newTab.setClosable(true);
                        tabPane.getTabs().add(tabPane.getTabs().size() - 1, new Tab("New Tab"));
                        tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
                    }
                }
            });
    }

    private Node getNode(URL url) throws URISyntaxException, IOException
    {
        HttpGet httpGet = new HttpGet(url.toURI());

        try(CloseableHttpResponse response = Browser.getHttpClient().execute(httpGet))
        {
            switch (response.getStatusLine().getStatusCode())
            {

                case HttpStatus.SC_OK:
                    FXMLLoader loader = new FXMLLoader();
                    Header header = response.getFirstHeader("class-loader-url");

                    if (header != null)
                    {
                        URL clURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), header.getValue());

                        if (logger.isLoggable(Level.INFO))
                        {
                            logger.log(Level.INFO, "Set up remote classloader: {0}", clURL);
                        }

                        loader.setClassLoader(HttpClassLoader.getInstance(clURL));
                    }

                    try
                    {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                        response.getEntity().writeTo(buffer);
                        response.close();

                        return loader.load(new ByteArrayInputStream(buffer.toByteArray()));
                    }
                    catch (Exception e)
                    {
                        response.close();
                        logger.log(Level.INFO, e.toString(), e);
                        Node node = loader.load(getClass().getResourceAsStream("/fxml/webview.fxml"));
                        WebViewController controller = (WebViewController) loader.getController();

                        controller.view(url);

                        return node;
                    }

                case HttpStatus.SC_UNAUTHORIZED:
                    response.close();
                    Optional<Pair<String, String>> result = new LoginDialog().showAndWait();

                    if (result.isPresent())
                    {
                        Browser.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort()),
                            new UsernamePasswordCredentials(result.get().getKey(), result.get().getValue()));

                        return getNode(url);
                    }

                    return null;
            }

            throw new IOException(response.getStatusLine().toString());
        }
    }

    @FXML private void go(ActionEvent event)
    {
        try
        {
            URL url = new URL(txtAddress.getText());

            tabPane.getSelectionModel().getSelectedItem().setContent(getNode(url));
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
