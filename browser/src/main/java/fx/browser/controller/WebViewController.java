package fx.browser.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.web.WebView;

import java.net.URL;

import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author tbuczko
 */
public class WebViewController implements Initializable
{
    @FXML private WebView webView;

    @Override public void initialize(URL url, ResourceBundle rb)
    {
    }

    public void view(URL url)
    {
        webView.getEngine().load(url.toString());
    }
}
