package com.example.sarisaristoreinventorymanagment;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginPage extends Application {

    public interface LoginSuccessHandler {
        void onLoginSuccess(Stage primaryStage);
    }

    private LoginSuccessHandler loginSuccessHandler;

    public LoginPage(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Page");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome Admin");
        scenetitle.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            if (username.equals("admin") && password.equals("123")) {
                actiontarget.setFill(Color.GREEN);
                actiontarget.setText("Login successful!");
                primaryStage.close();
                loginSuccessHandler.onLoginSuccess(primaryStage);
            } else {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Invalid username or password");
            }
        });

        Scene loginScene = new Scene(grid, 500, 300);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}