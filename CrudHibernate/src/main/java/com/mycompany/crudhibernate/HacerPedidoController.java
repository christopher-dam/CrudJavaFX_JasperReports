package com.mycompany.crudhibernate;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
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
import org.hibernate.Session;
import org.hibernate.Transaction;
/**
 * FXML Controller class
 *
 * @author chris
 */
public class HacerPedidoController implements Initializable {
    
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEstado;
    @FXML
    private Spinner<Integer> producto;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnRealizar;
    @FXML
    private Button btnInformeCarta;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Inicializo mi spinner
       SpinnerValueFactory svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10,1,1);
       producto.setValueFactory(svf);
    }    
    
    @FXML
    private void volver(ActionEvent event) { 
           try {
            App.setRoot("primary");
        } catch (IOException ex) {
            Logger.getLogger(HacerPedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     @FXML
    private void realizarPedido(ActionEvent event) {
        
        //Hago un nuevo pedido dandoles a los setter el valor de mis campos
        Pedidos p = new Pedidos();
        
        java.util.Date ahora = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(ahora.getTime());
        
        p.setCliente(txtNombre.getText());
        p.setFecha(sqlDate);
        p.setEstado(txtEstado.getText());
        p.setProductoId(producto.getValue());
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        s.save(p);
        tr.commit();
        
        try {
            App.setRoot("primary");
        } catch (IOException ex) {
            Logger.getLogger(HacerPedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void verCarta(ActionEvent event) {
        
        String archivo ="crudCarta.jrxml";
        
        try {
            var parameters = new HashMap();
            parameters.put("Título", "Carta");
                    
            JasperReport informe = JasperCompileManager.compileReport(archivo);
            JasperPrint impresion = JasperFillManager.fillReport(informe, parameters, Conexion.getConexion());
            
            JRViewer visor =  new JRViewer(impresion);
            
            JFrame ventanaInforme = new JFrame("Informe carta La bocatería");
            ventanaInforme.getContentPane().add(visor);
            ventanaInforme.pack();
            ventanaInforme.setVisible(true);
            
            var exportador = new JRPdfExporter();
            exportador.setExporterInput(new SimpleExporterInput(impresion));
            exportador.setExporterOutput(new SimpleOutputStreamExporterOutput("La_bocateria.pdf"));

            var configuracion = new SimplePdfExporterConfiguration();
            exportador.setConfiguration(configuracion);
            
            exportador.exportReport();
            
        } catch (JRException ex) {
            System.out.println(ex);
        }
    }
}
