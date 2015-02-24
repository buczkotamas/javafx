package fxbrowser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tbuczko
 */
public class HTTPClassLoader extends ClassLoader
{
    private static final Logger logger = Logger.getLogger(HTTPClassLoader.class.getName());
    ClassLoader defaultClassLoader;
    private final HashMap<String, Class> classes = new HashMap<>();
    private final String host;
    private final String path;
    private final int port;

    public HTTPClassLoader(ClassLoader defaultClassLoader, URL url)
    {
        this.defaultClassLoader = defaultClassLoader;
        this.host = url.getHost();
        this.port = url.getPort();
        this.path = url.getPath().substring(0, url.getPath().indexOf("/", 1)) + "/FXClassLoader";
    }

    @Override public Class findClass(String name) throws ClassNotFoundException
    {
        if (logger.isLoggable(Level.INFO))
        {
            logger.log(Level.INFO, "Load remote class: {0}", name);
        }

        Class result = classes.get(name);  //checks in cached classes

        if (result != null)
        {
            return result;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try
        {
            URIBuilder builder = new URIBuilder();

            builder.setScheme("http");
            builder.setHost(host);
            builder.setPort(port);
            builder.setPath(path);
            builder.setParameter("class", name);
            HttpGet httpGet = new HttpGet(builder.build());

            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

                entity.writeTo(byteStream);
                byte[] bytes = byteStream.toByteArray();

                result = defineClass(name, bytes, 0, bytes.length, null);

                classes.put(name, result);

                return result;
            }
            else
            {
                throw new ClassNotFoundException(response.getStatusLine().toString());
            }
        }
        catch (IOException | URISyntaxException ioe)
        {
            throw new ClassNotFoundException(ioe.getMessage(), ioe);
        }
        finally
        {
            try
            {
                if (response != null)
                {
                    response.close();
                }
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, null, ex);
            }

            try
            {
                httpClient.close();
            }
            catch (IOException ex)
            {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override public Class loadClass(String name) throws ClassNotFoundException
    {
        try
        {
            return defaultClassLoader.loadClass(name);
        }
        catch (ClassNotFoundException e)
        {

            // Do not try to load-define from prohibited packages, like java.*
            if ((name != null) && name.startsWith("java."))
            {
                if (logger.isLoggable(Level.INFO))
                {
                    logger.log(Level.INFO, "Prohibited package name: {0}", name.substring(0, name.lastIndexOf('.')));
                }

                throw e;
            }

            return findClass(name);
        }
    }
}
