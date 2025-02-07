package com.sate.inv;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class HelloController {

    @FXML
    private Button close;
    public void close(){
        System.exit(0);
    }

    @FXML
    private Button loginBtn;
    //database tools
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private double x = 0;
    private double y = 0;
    public void loginAdmin(){
        String sql = "SELECT * FROM admin WHERE username = ? and password = ?";

        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, username.getText());
            prepare.setString(2, password.getText());

            result = prepare.executeQuery();
            Alert alert;
            if (username.getText().isEmpty() || password.getText().isEmpty()){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Warning Message");
                alert.setHeaderText(null);
                alert.setContentText("Please provide all the necessary information required");
                alert.showAndWait();
            }
            else{
                if (result.next()){
                    //if password and username are correct then proceed to the next form: Dashboard
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Login Successful");
                    alert.showAndWait();

                    loginBtn.getScene().getWindow().hide();


                    //invoke dashboard.fxml here
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml"))); //link to the dashboard
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    scene.setOnMousePressed((MouseEvent event) -> {
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    scene.setOnMouseDragged((MouseEvent event) -> {
                        stage.setX(event.getScreenX() -x);
                        stage.setY(event.getScreenY() -y);
                        stage.setOpacity(.8);
                    });

                    scene.setOnMouseReleased((MouseEvent event) -> {
                        stage.setOpacity(1);
                    });

                    stage.initStyle(StageStyle.TRANSPARENT);

                    stage.setScene(scene);
                    stage.show();

                }else {
                    //display error message
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Warning Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong Password or Username please verify and try again");
                    alert.showAndWait();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private AnchorPane main_form;
}