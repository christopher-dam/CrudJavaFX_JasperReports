package com.mycompany.crudhibernate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Carta;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class PrimaryController implements Initializable {

    @FXML
    private TableView<Carta> tabla;
    @FXML
    private TableColumn<Carta, Long> colId;
    @FXML
    private TableColumn<Carta, String> colNombre;
    @FXML
    private TableColumn<Carta, String> colDescripcion;
    @FXML
    private TableColumn<Carta, Double> colPrecio;
    @FXML
    private Label hora;
    @FXML
    private Button btnSalir;
    @FXML
    private Button btnVer;
    @FXML
    private Button btnNuevo;
    @FXML
    private Label pendientes;

    @Override
    // https://openjfx.io/javadoc/11/javafx.controls/javafx/scene/control/TableView.html
    public void initialize(URL url, ResourceBundle rb) {

//        ObservableList<Carta> contenido = FXCollections.observableArrayList();
//        tabla.setItems(contenido);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        actualizarTabla();
        
        hora();
    }

    private void actualizarTabla() throws HibernateException {
        
        //abro una sesion y hago una query para imprimir todos los datos de la carta
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("FROM Carta");
        ArrayList<Carta> carta = (ArrayList<Carta>) q.list();
        tabla.getItems().addAll(carta);
        s.close();
    }

    private void hora() {
        
        //Realizo un timer en el cual voy a imprimir la hora
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Date fecha = new Date();
                        hora.setText(fecha.toString());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    @FXML
    private void salir(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void realizarPedido(ActionEvent event) {
        try {
            App.setRoot("hacerPedido");
        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @FXML
    private void verPedidos(ActionEvent event)  {
        try {
            App.setRoot("secondary");
        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
