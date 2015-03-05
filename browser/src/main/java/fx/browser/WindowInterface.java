/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx.browser;

import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 * @author tbuczko
 */
public interface WindowInterface {

    History getHistory();

    URL getLocation();

    void setLocation(URL location) throws URISyntaxException;
    
}
