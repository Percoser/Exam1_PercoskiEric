package sample;
import com.jfoenix.controls.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.*;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;



public class Controller implements Initializable {
    final String AWS_URL = "jdbc:jtds:sqlserver://sandbox.cbuhg6kujbbi.us-east-1.rds.amazonaws.com:1433/NumberDB";
    final String user = "admin";
    final String pass = "password";

    public JFXButton runButton;
    public JFXButton loadButton;
    public JFXListView historyListView;
    public JFXTextField minText;
    public JFXTextField maxText;
    public Label displayNum;
    public JFXButton deleteTableButt;
    public JFXSnackbar notifySnack;
    public AnchorPane pane;


    public void generateNum(String url, String user, String pass){
       try {
           try {
               Class.forName("net.sourceforge.jtds.jdbc.Driver");
           } catch (ClassNotFoundException e) {
               e.printStackTrace();
               System.out.println("Driver Error");
           }
           try {
               Connection conn = DriverManager.getConnection(url, user, pass);
               Statement stmt = conn.createStatement();

               int mini= Integer.parseInt(minText.getText());
               int maxi= Integer.parseInt(maxText.getText());
               int number = 0;

               if(maxi > mini){
                   number = new Random().nextInt(maxi +1 -mini) + mini;
               }else {
                   System.out.println("Max must be larger than the Min");
               }

               System.out.println(number);

               try{
                   stmt.executeUpdate(
                           "INSERT INTO NumberHistory VALUES('" +
                                   number + "', '"
                                    + mini + "', '"
                                    + maxi + "');"
                   );
                   displayNum.setText("Your Random Number is: " + number);


//                   System.out.println("RECORD ADDED SUCCESSFULLY");
                   stmt.close();
                   conn.close();
               }catch(SQLException sqlex) {
                   System.out.println("SQL Error");
                   sqlex.getStackTrace();
               }

           }catch(Exception e){
               System.out.println("Connection Error");
           }


        }catch(Exception ignored){

        }

    }

    private void deleteTable(String url, String user, String pass){
        try {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Driver Error");
            }
            try {
                Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement();

                try{
                    stmt.execute(
                            "DROP TABLE NumberHistory;"
                    );
                    System.out.println("TABLE DROPPED SUCCESSFULLY");
                    stmt.close();
                    conn.close();
                    createTable(url, user, pass);
                }catch(SQLException sqlex) {
                    System.out.println("SQL Error");
                }

            }catch(Exception e){
                System.out.println("Connection Error");
            }

        }catch(Exception e){

        }
    }

    private void loadData(String url, String user, String pass){
        try {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Driver Error");
            }
            try {
                Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement();

                try{
                    ResultSet result = stmt.executeQuery(
                            "SELECT * FROM NumberHistory;"
                    );
                    ObservableList<NumberLine> numList = FXCollections.observableArrayList();
                    while (result.next()) {
                        NumberLine temp = new NumberLine();
                        temp.number= Integer.parseInt(result.getString("number"));
                        temp.mini= Integer.parseInt(result.getString("mini"));
                        temp.maxi= Integer.parseInt(result.getString("maxi"));

                        numList.add(temp);
                    }

                    historyListView.setItems(numList);
                    historyListView.getItems();
                    System.out.println("DATA LOADED SUCCESSFULLY");


                    stmt.close();
                    conn.close();
                }catch(SQLException sqlex) {
                    System.out.println("SQL Error");
                }

            }catch(Exception e){
                System.out.println("Connection Error");
            }

        }catch(Exception e){

        }
    }

    public void createTable(String url, String user, String pass){
        try{
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Driver Error");
            }
            try {
                Connection conn = DriverManager.getConnection(AWS_URL, user, pass);
                Statement stmt = conn.createStatement();

                try{
                    stmt.execute(
                            "CREATE TABLE NumberHistory(" +
                                    "number int," +
                                    "mini int," +
                                    "maxi int);"
                    );
                    System.out.println("TABLE CREATED SUCCESSFULLY");

                    stmt.close();
                    conn.close();
                }catch(SQLException sqlex) {
                    System.out.println("SQL Error, or Table may already Exist, please continue.");

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch(Exception e){
        }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    try{
        createTable(AWS_URL, this.user, this.pass);
    }catch(Exception e){
        System.out.println("Initialize ERROR from Dev Team");
    }



        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                generateNum(AWS_URL, user, pass);
            }
        });

        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadData(AWS_URL, user, pass);
            }
        });

        deleteTableButt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteTable(AWS_URL, user, pass);
            }
        });


    }
}
