package fx.browser;

import org.apache.commons.io.IOUtils;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tbuczko
 */
public class WindowClassLoader extends ClassLoader
{
    private static final Logger logger = Logger.getLogger(WindowClassLoader.class.getName());
    private final List<String> tabStaticClassNames = Arrays.asList("fx.browser.WindowImpl");

    @Override public Class loadClass(String name) throws ClassNotFoundException
    {
        System.out.println("# " + this.toString() + ").loadClass: " + name);
        if (tabStaticClassNames.contains(name))
        {
            try
            {
                byte[] bytes = IOUtils.toByteArray(getResourceAsStream(name.replace(".", "/") + ".class"));
                Class c = defineClass(name, bytes, 0, bytes.length, null);

                return c;
            }
            catch (Exception ex)
            {
                logger.log(Level.SEVERE, "Error loading class: {0}", name);
            }
        }

        return super.loadClass(name);
    }
}
