package fx.browser;

import javafx.application.Application;

import static javafx.application.Application.launch;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Browser extends Application
{
    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private static final CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();

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
        launch(args);
    }

    @Override public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/mainform.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("FXBrowser");
        stage.setScene(scene);
        stage.show();
    }
}
