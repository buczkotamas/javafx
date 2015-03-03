package fx.browser;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

//import org.apache.webbeans.config.WebBeansContext;
//import org.apache.webbeans.spi.ContainerLifecycle;
//
//import javax.enterprise.inject.spi.Bean;
//import javax.enterprise.inject.spi.BeanManager;
public class Browser extends Application
{
    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private static final CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
    //private static ContainerLifecycle lifecycle = null;

    public static CredentialsProvider getCredentialsProvider()
    {
        return credentialsProvider;
    }

    public static CloseableHttpClient getHttpClient()
    {
        return httpClient;
    }

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/mainform.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("FXBrowser");
        stage.setScene(scene);
        stage.show();

        /*
        lifecycle = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
        lifecycle.startApplication(null);
        BeanManager beanManager = lifecycle.getBeanManager();
        Bean<?> bean = beanManager.getBeans("browser").iterator().next();
        Browser browser = (Browser) lifecycle.getBeanManager().getReference(bean, Browser.class, beanManager.createCreationalContext(bean));
        */
    }
}
