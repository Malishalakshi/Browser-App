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

        //Get protocol
        if((i = url.indexOf( "://" )) != -1){
            protocol = url.substring( 0,i );
        }else protocol = "http";

    }
}