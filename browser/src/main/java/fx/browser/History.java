package fx.browser;

import java.net.URL;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author tbuczko
 */
public class History
{
    Queue<URL> back = new LinkedList<>();
    URL current;
    Queue<URL> forward = new LinkedList<>();

    public URL back()
    {
        forward.offer(current);
        current = back.poll();

        return current;
    }

    public URL get()
    {
        return current;
    }

    public URL next()
    {
        back.offer(current);
        current = forward.poll();

        return current;
    }

    public void set(URL url)
    {
        if (!current.equals(url))
        {
            current = url;
            back.offer(url);
        }
    }
}
