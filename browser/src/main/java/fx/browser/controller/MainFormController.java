package fx.browser.controller;

import fx.browser.Browser;
import fx.browser.HTTPClassLoader;

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

import javafx.util.Pair;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

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
        CloseableHttpResponse response = Browser.getHttpClient().execute(httpGet);

        switch (response.getStatusLine().getStatusCode())
        {

            case HttpStatus.SC_OK:
                Header header = response.getFirstHeader("class-loader-url");

                if (header == null)
                {
                    throw new IOException("class-loader-url header is missing!");
                }

                URL clURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), header.getValue());

                if (logger.isLoggable(Level.INFO))
                {
                    logger.log(Level.INFO, "Class loder URL: {0}", clURL);
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream((int) response.getEntity().getContentLength());

                response.getEntity().writeTo(buffer);
                EntityUtils.consume(response.getEntity());
                httpGet.reset();
                FXMLLoader loader = new FXMLLoader();

                loader.setClassLoader(new HTTPClassLoader(FXMLLoader.getDefaultClassLoader(), clURL));
                Node node = loader.load(new ByteArrayInputStream(buffer.toByteArray()));

                return node;

            case HttpStatus.SC_UNAUTHORIZED:
                httpGet.reset();
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
