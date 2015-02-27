package fx.browser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayOutputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tbuczko
 */
public class HttpClassLoader extends ClassLoader
{
    private static final Logger logger = Logger.getLogger(HttpClassLoader.class.getName());
    private static final HashMap<URL, HttpClassLoader> loaders = new HashMap<>();
    private final HashMap<String, Class> classes = new HashMap<>();
    private final List<String> notFound = new ArrayList<>();
    private final String urlWithQuery;

    private HttpClassLoader(URL url)
    {
        this.urlWithQuery = url.toString() + "?class=";
    }

    public static HttpClassLoader getInstance(URL url)
    {
        HttpClassLoader loader = loaders.get(url);

        if (loader == null)
        {
            loader = new HttpClassLoader(url);
            loaders.put(url, loader);
        }

        return loader;
    }

    @Override public Class findClass(String name) throws ClassNotFoundException
    {
        if (notFound.contains(name))
        {
            throw new ClassNotFoundException("Class already tried to be loaded from the server but response code was 404: " + name);
        }

        Class result = classes.get(name);

        if (result != null)
        {
            if (logger.isLoggable(Level.INFO))
            {
                logger.log(Level.INFO, "Class found in cache: {0}", name);
            }

            return result;
        }

        if (logger.isLoggable(Level.INFO))
        {
            logger.log(Level.INFO, "Class not found in cache. Try to download from remote server: {0}", name);
        }

        try
        {
            HttpGet httpGet = new HttpGet(urlWithQuery + name);

            try(CloseableHttpResponse response = Browser.getHttpClient().execute(httpGet))
            {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    if (logger.isLoggable(Level.INFO))
                    {
                        logger.log(Level.INFO, "Class loaded from server: {0}", name);
                    }

                    HttpEntity entity = response.getEntity();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    entity.writeTo(buffer);  // closes streams
                    byte[] bytes = buffer.toByteArray();

                    result = defineClass(name, bytes, 0, bytes.length, null);

                    classes.put(name, result);

                    return result;
                }

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND)
                {
                    notFound.add(name);
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error downloading class: " + name, e);
        }

        throw new ClassNotFoundException("Error downloading class: " + name);
    }

    @Override public Class loadClass(String name) throws ClassNotFoundException
    {
        try
        {
            Class c = getParent().loadClass(name);

            if (logger.isLoggable(Level.INFO))
            {
                logger.log(Level.INFO, "Class loaded by parent: {0}", name);
            }

            return c;
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
