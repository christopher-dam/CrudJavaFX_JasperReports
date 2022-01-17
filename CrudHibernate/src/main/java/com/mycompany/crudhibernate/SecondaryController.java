package com.mycompany.crudhibernate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javax.swing.JFrame;
import models.Pedidos;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.swing.JRViewer;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class SecondaryController implements Initializable {

    @FXML
    private TableView<Pedidos> tabla;
    @FXML
    private TableColumn<Pedidos, Long> colId;
    @FXML
    private TableColumn<Pedidos, String> colCliente;
    @FXML
    private TableColumn<Pedidos, Date> colFecha;
    @FXML
    private TableColumn<Pedidos, String> colEstado;
    @FXML
    private TableColumn<Pedidos, String> colProducto;
    @FXML
    private Label hora;
    @FXML
    private Label pendientes;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnInformePedidos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        ObservableList<Pedidos> contenido = FXCollections.observableArrayList();
//        tabla.setItems(contenido);
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("productoId"));
        
        actualizarTabla();

        refresco();
        count();
    }

    private void actualizarTabla() throws HibernateException {
        
        /* Establezco la fecha, abro la sesion, realizo la query, establezco 
           el parametro fecha e imprimo los items en la tabla */
        java.util.Date ahora = new java.util.Date();
        java.sql.Date sqlFecha = new java.sql.Date(ahora.getTime());
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("FROM Pedidos p WHERE p.fecha=:fecha");
        q.setParameter("fecha", sqlFecha);
        ArrayList<Pedidos> pedidos = (ArrayList<Pedidos>) q.list();
        tabla.getItems().clear();
        tabla.getItems().addAll(pedidos);
        s.close();
    }

    private void refresco() {
        
        //Realizo un timer en el cual va a ir la hora y hago un refresco de la base de datos
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        java.util.Date fecha = new java.util.Date();
                        hora.setText(fecha.toString());
                        actualizarTabla();
                        count();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 5000);
    }

    private void count() {
        
        //Hago una query para contar los pedidos pendientes de hoy
        java.util.Date ahora = new java.util.Date();
        java.sql.Date sqlFecha = new java.sql.Date(ahora.getTime());
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT count(*) FROM Pedidos p WHERE p.estado='Pendiente' and p.fecha=:fecha");
        q.setParameter("fecha", sqlFecha);
        pendientes.setText("Quedan " + q.uniqueResult() + " pedidos pendientes");
    }

    @FXML
    private void cambiarEstado(MouseEvent event) {
        
        //Cambio el estado de los pedidos pendientes a recogidos
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        if (event.getButton() == MouseButton.SECONDARY) {
            eliminar(event);
        } else if (event.getButton() == MouseButton.PRIMARY) {
            //Pedidos p = s.load(Pedidos.class,tabla.getSelectionModel().getSelectedItem() );
            Pedidos p = tabla.getSelectionModel().getSelectedItem();

            p.setEstado("Recogido");
            s.update(p);
            tr.commit();
            s.close();

            actualizarTabla();

        }
    }

    @FXML
    private void volver(ActionEvent event) {
        try {
            App.setRoot("primary");
        } catch (IOException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void eliminar(MouseEvent event) {
        Pedidos p = tabla.getSelectionModel().getSelectedItem();
        System.out.println(p);
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();

        s.remove(p);
        tr.commit();
        s.close();
        
        actualizarTabla();
    }

    @FXML
    private void eliminar(ActionEvent event) {
    }

    @FXML
    private void verInformePedidos(ActionEvent event){
        
        String archivo ="crudPedidos.jrxml";
        
        try {
            var parameters = new HashMap();
            parameters.put("Título", "Listado de los pedidos de hoy");
                    
            JasperReport informe = JasperCompileManager.compileReport(archivo);
            JasperPrint impresion = JasperFillManager.fillReport(informe, parameters, Conexion.getConexion());
            
            JRViewer visor =  new JRViewer(impresion);
            
            JFrame ventanaInforme = new JFrame("Pedidos de hoy");
            ventanaInforme.getContentPane().add(visor);
            ventanaInforme.pack();
            ventanaInforme.setVisible(true);
            
            var exportador = new JRPdfExporter();
            exportador.setExporterInput(new SimpleExporterInput(impresion));
            exportador.setExporterOutput(new SimpleOutputStreamExporterOutput("Pedidos.pdf"));

            var configuracion = new SimplePdfExporterConfiguration();
            exportador.setConfiguration(configuracion);
            
            exportador.exportReport();
            
        } catch (JRException ex) {
            System.out.println(ex);
        }
    }
}
