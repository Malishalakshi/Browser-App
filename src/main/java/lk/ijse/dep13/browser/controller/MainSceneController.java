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
            if (newValue) txtAddress.selectAll();
        } );

    }

    public void txtAddressOnAction(ActionEvent actionEvent)  {
        String url = txtAddress.getText();
        if(url.isBlank()) return;
        loadWebpage(url);
    }
    //Get URL Details
    private void loadWebpage(String url)  {
        int i = 0;
        String protocol = null;
        String host = null;
        int port = -1;

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

    }
}