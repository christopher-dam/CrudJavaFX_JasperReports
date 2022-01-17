module com.mycompany.crudhibernate {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires org.hibernate.orm.core;
    requires javafx.swing;
    requires java.naming;
    requires jasperreports;
    requires java.sql;
    requires java.persistence; //   IMPORTANTE PONER SIEMPRE PUTISIMA MIERDA ASQUEROSA CLOACA DE LOS COJONES

    opens com.mycompany.crudhibernate to javafx.fxml, org.hibernate.orm.core, java.sql, javafx.swing;
    opens models;
    exports com.mycompany.crudhibernate;
}
