package dk.dtu.compute.se.pisd.roborally.dal;

import com.mysql.cj.util.StringUtils;
import dk.dtu.compute.se.pisd.roborally.fileacces.IOUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * @author unknown
 */


public class Connector {


    //HOST er IP-adressen på den server, hvor databasen er placeret. 127.0.0.1
// er den lokale værtsadresse, hvilket betyder, at databasen er på samme maskine som denne kode kører.
    private static final String HOST = "127.0.0.1";

    //PORT er den port, som databasen lytter på. 3306 er standardporten for MySQL databaser.
    private static final int PORT = 3306;

    //DATABASE er navnet på den specifikke database, der skal interageres med
    private static final String DATABASE = "roborally2_0";

    //USERNAME og PASSWORD er legitimationsoplysninger, der bruges til at oprette forbindelse til databasen.
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Rep68hfq";

    //DELIMITER er tegnet, der bruges til at adskille individuelle SQL-kommandoer i en streng med flere kommandoer.
    private static final String DELIMITER = ";";

    //Connection: connection er et Connection objekt fra JDBC API'et,
    // der repræsenterer en session med en specifik database.
    private Connection connection;


    //Constructor: I constructoren forsøger klassen at oprette forbindelse til databasen ved hjælp af de ovennævnte oplysninger
// og kalder derefter createDatabaseSchema() metoden.
    Connector() {
        try {
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?serverTimezone=UTC";
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

            createDatabaseSchema();
        } catch (SQLException e) {

            e.printStackTrace();
            // Platform.exit();
        }
    }
    //createDatabaseSchema: Denne metode læser en fil, der indeholder SQL-kommandoer til oprettelse af databasens schema.
    // Den kører derefter disse kommandoer på databasen.
    // Hvis der opstår en fejl under processen, ruller den tilbage alle ændringer,
    // der er foretaget i denne transaktion, for at sikre databasens konsistens.
    private void createDatabaseSchema() {

        String createTablesStatement =
                IOUtil.readResource("schemas/createschema.sql");

        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            for (String sql : createTablesStatement.split(DELIMITER)) {
                if (!StringUtils.isEmptyOrWhitespaceOnly(sql)) {
                    statement.executeUpdate(sql);
                }
            }

            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {}
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {}
        }
    }
    //getConnection: Denne metode returnerer det aktive Connection objekt,
// hvilket giver andre klasser mulighed for at interagere med databasen.
    Connection getConnection() {
        return connection;
    }

}


