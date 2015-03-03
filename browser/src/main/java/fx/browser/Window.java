/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx.browser;

import java.net.URL;

import java.util.UUID;

/**
 *
 * @author tbuczko
 */
public class Window
{
    public static final UUID uuid = UUID.randomUUID();
    History history;
    URL location;

    public History getHistory()
    {
        return history;
    }

    public URL getLocation()
    {
        return location;
    }

    public void setLocation(URL location)
    {
//        this.location = location;
//        HttpGet httpGet = new HttpGet(url.toURI());
//
//        try(CloseableHttpResponse response = Browser.getHttpClient().execute(httpGet))
//        {
//            switch (response.getStatusLine().getStatusCode())
//            {
//
//                case HttpStatus.SC_OK:
//                    FXMLLoader loader = new FXMLLoader();
//                    Header header = response.getFirstHeader("class-loader-url");
//
//                    if (header != null)
//                    {
//                        URL clURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), header.getValue());
//
//                        if (logger.isLoggable(Level.INFO))
//                        {
//                            logger.log(Level.INFO, "Set up remote classloader: {0}", clURL);
//                        }
//
//                        loader.setClassLoader(HttpClassLoader.getInstance(clURL));
//                    }
//
//                    try
//                    {
//                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//
//                        response.getEntity().writeTo(buffer);
//                        response.close();
//
//                        return loader.load(new ByteArrayInputStream(buffer.toByteArray()));
//                    }
//                    catch (Exception e)
//                    {
//                        response.close();
//                        logger.log(Level.INFO, e.toString(), e);
//                        Node node = loader.load(getClass().getResourceAsStream("/fxml/webview.fxml"));
//                        WebViewController controller = (WebViewController) loader.getController();
//
//                        controller.view(url);
//
//                        return node;
//                    }
//
//                case HttpStatus.SC_UNAUTHORIZED:
//                    response.close();
//                    Optional<Pair<String, String>> result = new LoginDialog().showAndWait();
//
//                    if (result.isPresent())
//                    {
//                        Browser.getCredentialsProvider().setCredentials(new AuthScope(url.getHost(), url.getPort()),
//                            new UsernamePasswordCredentials(result.get().getKey(), result.get().getValue()));
//
//                        return getNode(url);
//                    }
//
//                    return null;
//            }
//
//            throw new IOException(response.getStatusLine().toString());
//        }
    }
}
