/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx.browser;

import fx.browser.controller.WebViewController;

import fx.browser.dialog.LoginDialog;

import javafx.fxml.FXMLLoader;

import javafx.scene.Node;

import javafx.scene.control.Tab;

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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tbuczko
 */
public class Window extends Tab implements WindowInterface
{
    private static final Logger logger = Logger.getLogger(Window.class.getName());
    public static final UUID uuid = UUID.randomUUID();

    static
    {
        System.out.println("### New Window class: " + uuid.toString());
    }

    History history;
    URL location;

    public Window()
    {
        super("New Tab");
    }

    @Override public History getHistory()
    {
        return history;
    }

    @Override public URL getLocation()
    {
        return location;
    }

    @Override public void setLocation(URL location) throws URISyntaxException
    {
        System.out.println("# Window.classLoader: " + getClass().getClassLoader().toString());
        this.location = location;
        HttpGet httpGet = new HttpGet(location.toURI());

        try(CloseableHttpResponse response = Browser.getHttpClient().execute(httpGet))
        {
            switch (response.getStatusLine().getStatusCode())
            {

                case HttpStatus.SC_OK:
                    FXMLLoader loader = new FXMLLoader();
                    Header header = response.getFirstHeader("class-loader-url");

                    if (header != null)
                    {
                        URL clURL = new URL(location.getProtocol(), location.getHost(), location.getPort(), header.getValue());

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
                        setContent(loader.load(new ByteArrayInputStream(buffer.toByteArray())));
                    }
                    catch (Exception e)
                    {
                        response.close();
                        logger.log(Level.INFO, e.toString(), e);
                        Node node = loader.load(getClass().getResourceAsStream("/fxml/webview.fxml"));
                        WebViewController controller = (WebViewController) loader.getController();

                        controller.view(location);
                        setContent(node);
                    }

                    break;

                case HttpStatus.SC_UNAUTHORIZED:
                    response.close();
                    Optional<Pair<String, String>> result = new LoginDialog().showAndWait();

                    if (result.isPresent())
                    {
                        Browser.getCredentialsProvider().setCredentials(new AuthScope(location.getHost(), location.getPort()),
                            new UsernamePasswordCredentials(result.get().getKey(), result.get().getValue()));
                        setLocation(location);
                    }

                    break;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
