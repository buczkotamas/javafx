package fxserver;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 *
 * @author tbuczko
 */
@WebServlet(
    name = "FXClassLoader",
    urlPatterns = { "/FXClassLoader" }
)
public class HTTPClassLoaderServlet extends HttpServlet
{
    private static final Logger logger = Logger.getLogger(HTTPClassLoaderServlet.class.getName());

    @Override public String getServletInfo()
    {
        return "Short description";
    }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        processRequest(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String className = request.getParameter("class");
        InputStream in = null;

        try
        {
            Class clazz = this.getClass().getClassLoader().loadClass(className);

            in = clazz.getResourceAsStream(clazz.getSimpleName() + ".class");

            IOUtils.copy(in, response.getOutputStream());
            in.close();
            response.setContentType("application/octet-stream");
            response.setStatus(SC_OK);
        }
        catch (ClassNotFoundException ex)
        {
            response.sendError(SC_NOT_FOUND, ex.toString());
            logger.log(Level.INFO, null, ex);
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
    }
}
