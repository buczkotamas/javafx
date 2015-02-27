/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx.browser;

import java.net.URL;

/**
 *
 * @author tbuczko
 */
public class Window
{
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
        this.location = location;
    }
}
