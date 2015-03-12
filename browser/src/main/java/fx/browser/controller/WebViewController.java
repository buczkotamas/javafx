package fx.browser.controller;

import javafx.fxml.FXML;

import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author tbuczko
 */
public class WebViewController
{
    @FXML private WebView webView;

    public void view(String url)
    {
        webView.getEngine().load(url);
    }
}
