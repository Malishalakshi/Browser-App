package lk.ijse.dep13.browser.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.io.*;
import java.net.Socket;

public class MainSceneController {

    public AnchorPane root;
    public TextField txtAddress;
    public WebView webDisplay;
    public void initialize()  {
        txtAddress.focusedProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue) Platform.runLater(()-> txtAddress.selectAll() ) ;
        } );

    }

    public void txtAddressOnAction(ActionEvent actionEvent) throws IOException {
        String url = txtAddress.getText();
        if(url.isBlank()) return;
        loadWebpage(url);
    }
    //Get URL Details
    private void loadWebpage(String url) throws IOException {
        int i = 0;
        String protocol = null;
        String host = null;
        int port = -1;
        String path = "/";

        //Get protocol
        if((i = url.indexOf( "://" )) != -1){
            protocol = url.substring( 0,i );
        }else protocol = "http";

        //Get host and port number
        int j = url.indexOf( "/", i == -1 ? (i = 0) : (i =  i + 3));
        host = j == -1 ? url.substring( i ) : url.substring( i, j );
        int portIndex = host.indexOf( ":" );
        if(portIndex != -1){
            port = Integer.parseInt( host.substring( portIndex + 1 ) );
            host = host.substring( 0, portIndex );
        }else {
            port = protocol.equalsIgnoreCase( "http" ) ? 80 : protocol.equalsIgnoreCase( "https" ) ? 443 : -1;
        }
        //Get path
        if((j != -1) && (j != url.length() - 1)){
            path = url.substring(j+1);
        }
        if(host.isBlank()|| port == -1){
            throw new RuntimeException( "Invalid URL");
        }
        System.out.println("protocol = " + protocol);
        System.out.println("host = " + host);
        System.out.println("port = " + port);
        System.out.println("path = " + path);

        //Create socket
        Socket socket = new Socket(host,port);
        System.out.println(socket.getInetAddress());

        //Read response
        new Thread(()->{
            try{
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader( is );
                BufferedReader br = new BufferedReader( isr );

                String statusLine = br.readLine();
                int statusCode = Integer.parseInt(statusLine.split(" ")[1]);
                boolean redirection = 300 <= statusCode && statusCode < 400;

                String line;
                String contentType = null;
                while((line = br.readLine()) != null && !line.isBlank()){
                    String header = line.split(":")[0];
                    String value = line.split( ":" )[1];
                    if(redirection){
                        System.out.println("redirection");
                    }else {
                        if(header.equalsIgnoreCase( "content-type" )){
                            contentType = value;
                        }
                    }
                }
                String content = null;
                if(contentType != null && contentType.contains("text/html")){
                    while ((line = br.readLine()) != null && !line.isBlank()) {
                        content += line;
                    }
                }else {
                    System.out.println("We don't support not-html content");
                }
                String finalContent = content;
                Platform.runLater(()->{
                    webDisplay.getEngine().loadContent( finalContent );
                });

            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }).start();
        //Write request
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream( os );
        String httpRequest = """
                GET %s HTTP/1.1
                Host: %s
                User-Agent: dep-browser
                Connection: close
                Accept: text/html;
                
                """.formatted( path,host );
        bos.write( httpRequest.getBytes() );
        bos.flush();

    }
}