/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxbrowser.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.net.URL;

import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author tbuczko
 */
public class ErrorController implements Initializable
{
    @FXML private TextArea txtExceptionStackTrace;

    public void setException(Exception e)
    {
        StringWriter sw = new StringWriter();

        e.printStackTrace(new PrintWriter(sw));
        txtExceptionStackTrace.setText(sw.toString());
    }

    @Override public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }
}
