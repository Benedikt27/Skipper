package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class Controller {

    public Button sendButton;
    public TextField name_field;
    public Label skipField;
    public Label hasSkips;
    public Button cancel;
    public Label mid_label;
    public PasswordField password;
    public TextField username;
    public Button sendButton_2;
    public Button remove_skip;
    public GridPane pane;

    private int status = 0;
    // 0 = logged out / 1 = enter un / 2 = logged in


    public Controller getInstance() {
        return this;
    }

    public void send(ActionEvent actionEvent) {
        if (status == 2) {
            //After Login Stuff
            if (!name_field.getCharacters().toString().equalsIgnoreCase("")) {

                if (sendButton_2.getText().equalsIgnoreCase("Check User")) {
                    sendButton_2.setText("Skip");
                    if (Server.getSkips(name_field.getText()) != 0) {
                        remove_skip.setVisible(true);
                    }
                    skipField.setText(name_field.getText());

                    String perfection = " Skips";
                    if (Server.getSkips(name_field.getText()) == 0) {
                        perfection = " Skip";
                    }

                    hasSkips.setText("has " + Server.getSkips(name_field.getText()) + perfection);
                    cancel.setVisible(true);
                } else if (sendButton_2.getText().equalsIgnoreCase("Skip")) {
                    if (Server.getSkips(name_field.getText()) != Server.getMaxSkips()) {
                        String executingStaff = username.getText();
                        String perfection = " Skips";
                        if (Server.getSkips(name_field.getText()) == 0) {
                            perfection = " Skip";
                        }
                        Server.addSkip(name_field.getText(), executingStaff);
                        remove_skip.setVisible(true);
                        hasSkips.setText("has " + Server.getSkips(name_field.getText()) + perfection);
                    } else {
                        hasSkips.setText("has already skipped " + Server.getMaxSkips() + " times");
                    }

                }


            } else {
                skipField.setText("Please enter a Name");
            }
        } else {
            //Before Login Stuff
            if (!password.isVisible()) {
                //Enter Username
                if (!username.getCharacters().toString().equalsIgnoreCase("")) {
                    if (Server.hasAccount(username.getText())) {
                        if (Server.hasLoggedIn(username.getText())) {
                            password.setVisible(true);
                            mid_label.setText("Please Enter your Password");
                            cancel.setVisible(true);
                            status = 1;
                            username.setEditable(false);
                            sendButton.setVisible(false);
                            sendButton_2.setVisible(true);
                            sendButton_2.setText("Login");
                            sendButton_2.setDefaultButton(true);
                        } else {
                            password.setVisible(true);
                            mid_label.setText("Please set your Password");
                            cancel.setVisible(true);
                            status = 1;
                            username.setEditable(false);
                            sendButton.setVisible(false);
                            sendButton_2.setText("Login");
                            sendButton_2.setVisible(true);
                            sendButton_2.setDefaultButton(true);
                        }
                    } else
                        mid_label.setText("You do not have an account");
                }
            } else {
                //Enter Password
                if (!password.getText().equalsIgnoreCase("")) {
                    if (Server.hasLoggedIn(username.getText())) {

                        String hash = Password.hashPassword(password.getText(), Server.getSalt(username.getText()));

                        if (Server.checkPassword(hash, username.getText())) {
                            loginChanges();
                        } else
                            mid_label.setText("Wrong Password");
                    } else if (!Server.hasLoggedIn(username.getText())) {
                        String salt = Password.generateSalt();
                        String hash2 = Password.hashPassword(password.getText(), salt);
                        Server.setSalt(salt, username.getText());
                        Server.setPassword(hash2, username.getText());


                        String hash = Password.hashPassword(password.getText(), Server.getSalt(username.getText()));
                        if (Server.checkPassword(hash, username.getText())) {
                            loginChanges();
                        }

                    }
                }
            }
        }

    }

    public void redo(KeyEvent keyEvent) {
        sendButton_2.setText("Check User");
        hasSkips.setText("");
        skipField.setText("");
        remove_skip.setVisible(false);
    }

    public void cancel(ActionEvent actionEvent) {
        if (status == 0) {
            cancel.setVisible(false);
        } else if (status == 1) {
            mid_label.setText("Please enter your Username");
            password.setVisible(false);
            cancel.setVisible(false);
            status = 0;
            username.setEditable(true);
            sendButton.setVisible(true);
            sendButton_2.setVisible(false);
            password.setText("");
            cancel.setText("Cancel");
            sendButton_2.setDefaultButton(false);
            sendButton.setDefaultButton(true);
        } else if (status == 2) {
            status = 0;
            mid_label.setText("Please enter your Username");
            password.setVisible(false);
            cancel.setVisible(false);
            name_field.setVisible(false);
            username.setVisible(true);
            sendButton.setText("Login");
            username.setText("");
            name_field.setText("");
            hasSkips.setText("");
            skipField.setText("");
            sendButton.setVisible(true);
            sendButton_2.setVisible(false);
            cancel.setText("Cancel");
            sendButton_2.setDefaultButton(false);
            sendButton.setDefaultButton(true);
            Server.logout();
            System.out.println("Logged out");
        }
    }

    private void loginChanges() {
        status = 2;
        System.out.println("Logged in");
        mid_label.setText("Skipper v0.1");
        sendButton.setText("Check User");
        sendButton_2.setText("Check User");
        password.setText("");
        username.setVisible(false);
        password.setVisible(false);
        cancel.setText("Log out");
        username.setEditable(true);
        name_field.setVisible(true);
    }

    public void rem_skip(ActionEvent actionEvent) {

        Server.removeSkip(name_field.getText());
        String perfection;
        if (Server.getSkips(name_field.getText()) != 1) {
            perfection = " Skips";
        } else {
            perfection = " Skip";
        }
        hasSkips.setText("has " + Server.getSkips(name_field.getText()) + perfection);

        if (Server.getSkips(name_field.getText()) == 0) {
            remove_skip.setVisible(false);
        }


    }
}
