package fx.browser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayOutputStream;

import java.net.URL;

import java.util.HashMap;
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

    static
    {
        System.out.println("# New HttpClassLoader Class");
    }

    private final String urlWithQuery;

    private HttpClassLoader(URL url, ClassLoader parent)
    {
        super(parent);
        this.urlWithQuery = url.toString() + "?class=";
    }

    public static HttpClassLoader getInstance(URL url, ClassLoader parent)
    {
        HttpClassLoader loader = loaders.get(url);

        if (loader == null)
        {
            loader = new HttpClassLoader(url, parent);
//            loaders.put(url, loader);
        }

        return loader;
    }

    @Override public Class findClass(String name) throws ClassNotFoundException
    {
        System.out.println("# " + this.toString() + ".findClass(" + name + ")" + " this.Classloader: "
              + getClass().getClassLoader().toString());

        //Do not try to load-define from prohibited packages, like java.*
        if ((name != null) && name.startsWith("java."))
        {

//            if (logger.isLoggable(Level.INFO))
//            {
//                logger.log(Level.INFO, "Prohibited package name: {0}", name);
//            }
            throw new ClassNotFoundException("Prohibited package name: " + name);
        }

//        if (logger.isLoggable(Level.INFO))
//        {
//            logger.log(Level.INFO, "Class not loaded yet, download from remote server: {0}", name);
//        }
        try
        {
            HttpGet httpGet = new HttpGet(urlWithQuery + name);

            try(CloseableHttpResponse response = Browser.getHttpClient().execute(httpGet))
            {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {

//                    if (logger.isLoggable(Level.INFO))
//                    {
//                        logger.log(Level.INFO, "Class loaded from server: {0}", name);
//                    }
                    HttpEntity entity = response.getEntity();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    entity.writeTo(buffer);  // auto closes streams
                    byte[] bytes = buffer.toByteArray();

                    return defineClass(name, bytes, 0, bytes.length, null);
                }

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND)
                {
                    ;
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error downloading class: " + name, e);
        }

        throw new ClassNotFoundException("Error downloading class: " + name);
    }
}
