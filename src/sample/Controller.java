package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    public Label label2;
    public Button bgbutton;
    public TextField url;
    public Button close;
    public BorderPane top;
    public ToolBar bar;
    public Button minimizebtn;
    public Button anchorbtn;
    public Label skipcount;

    private double xOffset = 0;
    private double yOffset = 0;

    private int status = 0;
    // 0 = logged out / 1 = enter username / 2 = logged in / 3 = BGChange
    private boolean anchored = false;

    public Controller getInstance() {
        return this;
    }

    public void send(ActionEvent actionEvent) {
        if (status == 2) {
            //After Login Stuff
            if (!name_field.getCharacters().toString().equalsIgnoreCase("")) {

                if (sendButton_2.getText().equalsIgnoreCase("Check")) {
                    mid_label.setText("Skipper v0.5");
                    sendButton_2.setText("Skip");
                    if (Server.getSkips(name_field.getText()) != 0) {
                        remove_skip.setVisible(true);
                    }
                    skipField.setText(name_field.getText());

                    String perfection = " Skips";
                    if (Server.getSkips(name_field.getText()) == 1) {
                        perfection = " Skip";
                    }

                    hasSkips.setText("has " + Server.getSkips(name_field.getText()) + perfection + " (" + (Server.getMaxSkips() - Server.getSkips(name_field.getText())) + " left)");
                    cancel.setVisible(true);
                } else if (sendButton_2.getText().equalsIgnoreCase("Skip")) {
                    if (Server.getSkips(name_field.getText()) != Server.getMaxSkips()) {
                        String executingStaff = username.getText();
                        String perfection = " Skips";
                        if (Server.getSkips(name_field.getText()) == 0) {
                            perfection = " Skip";
                        }
                        Server.addSkip(name_field.getText(), executingStaff);
                        skipcount.setText(String.valueOf(Integer.parseInt(skipcount.getText().trim()) + 1));
                        remove_skip.setVisible(true);
                        hasSkips.setText("has " + Server.getSkips(name_field.getText()) + perfection + " (" + (Server.getMaxSkips() - Server.getSkips(name_field.getText())) + " left)");
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
                            mid_label.setText("");
                            label2.setText("Please enter your Password");
                            cancel.setVisible(true);
                            status = 1;
                            username.setEditable(false);
                            sendButton.setVisible(false);
                            sendButton_2.setVisible(true);
                            sendButton_2.setText("Login");
                            sendButton_2.setDefaultButton(true);
                            bgbutton.setVisible(false);
                        } else {
                            password.setVisible(true);
                            label2.setText("Please set your Password");
                            cancel.setVisible(true);
                            status = 1;
                            username.setEditable(false);
                            sendButton.setVisible(false);
                            sendButton_2.setText("Login");
                            sendButton_2.setVisible(true);
                            sendButton_2.setDefaultButton(true);
                            bgbutton.setVisible(false);
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
                            label2.setText("Wrong Password");
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
        sendButton_2.setText("Check");
        hasSkips.setText("");
        skipField.setText("");
        remove_skip.setVisible(false);
    }

    public void cancel(ActionEvent actionEvent) {
        if (status == 0) {
            cancel.setVisible(false);
        } else if (status == 1) {
            label2.setVisible(true);
            label2.setText("Please enter your Username");
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
            bgbutton.setVisible(false);
            url.setVisible(false);
            bgbutton.setText("BG");
        } else if (status == 2) {
            if (cancel.getText().equals("Sure?")) {
                status = 0;
                label2.setVisible(true);
                label2.setText("Please enter your Username");
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
                bgbutton.setVisible(false);
            } else {
                cancel.setText("Sure?");
            }
        } else if (status == 3) {
            status = 2;
            url.setVisible(false);
            bgbutton.setText("BG");
            cancel.setText("Log out");
            sendButton_2.setVisible(true);
            name_field.setVisible(true);
            mid_label.setVisible(true);
        }
    }

    private void loginChanges() {
        status = 2;
        System.out.println("Logged in");
        mid_label.setText("Skipper v0.5");
        sendButton.setText("Check");
        sendButton_2.setText("Check");
        password.setText("");
        username.setVisible(false);
        password.setVisible(false);
        cancel.setText("Log out");
        username.setEditable(true);
        name_field.setVisible(true);
        label2.setVisible(false);
        bgbutton.setVisible(true);
        skipcount.setText(String.valueOf(Server.getUserSkips(username.getText())));
        if (Server.getBackground(username.getText()) != null) {
            BackgroundImage myBI = new BackgroundImage(new Image(Server.getBackground(username.getText()), 500, 400, false, true),
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    BackgroundSize.DEFAULT);
            pane.setBackground(new Background(myBI));
            PixelReader reader = myBI.getImage().getPixelReader();
            CornerRadii radii = new CornerRadii(0);
            bar.setBackground(new Background(new BackgroundFill(reader.getColor(249, 199), radii, null)));
        }
    }

    public void rem_skip(ActionEvent actionEvent) {

        Server.removeSkip(name_field.getText(), username.getText());
        skipcount.setText(String.valueOf(Integer.parseInt(skipcount.getText().trim()) - 1));
        String perfection;
        if (Server.getSkips(name_field.getText()) != 1) {
            perfection = " Skips";
        } else {
            perfection = " Skip";
        }
        hasSkips.setText("has " + Server.getSkips(name_field.getText()) + perfection + " (" + (Server.getMaxSkips() - Server.getSkips(name_field.getText())) + " left)");

        if (Server.getSkips(name_field.getText()) == 0) {
            remove_skip.setVisible(false);
        }


    }

    public void bg(ActionEvent actionEvent) {
        if (bgbutton.getText().equals("BG")) {
            url.setVisible(true);
            mid_label.setVisible(false);
            sendButton.setVisible(false);
            username.setVisible(false);
            label2.setVisible(false);
            bgbutton.setText("Save");
            cancel.setText("Cancel");
            name_field.setVisible(false);
            sendButton_2.setVisible(false);
            label2.setText("");
            status = 3;
        } else if (bgbutton.getText().equals("Save")) {
            try {
                BackgroundImage myBI = new BackgroundImage(new Image(url.getText(), 500, 400, false, true),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT);
                PixelReader reader = myBI.getImage().getPixelReader();
                CornerRadii radii = new CornerRadii(0);
                bar.setBackground(new Background(new BackgroundFill(reader.getColor(249, 199), radii, null)));
                pane.setBackground(new Background(myBI));
                Server.setBackground(username.getText(), url.getText());

                ClassLoader classLoader = getClass().getClassLoader();
                for (String line : Files.readAllLines(Paths.get(classLoader.getResource("sample/style.css").getPath()))) {
                    System.out.println(line);
                }

//                File styleFile = new File(classLoader.getResource("../sample/style.css").getFile());
//                if (styleFile.exists()) {
//                    System.out.println("geht");
//                } else {
//                    System.out.println("geht nicht!");
//                }


            } catch (IllegalArgumentException e) {
                mid_label.setVisible(true);
                mid_label.setText("Invalid Link");
            } catch (IOException e) {
                e.printStackTrace();
            }
            label2.setVisible(true);
            label2.setText("Skipper v0.5");
            status = 2;
            sendButton_2.setVisible(true);
            password.setText("");
            cancel.setText("Cancel");
            sendButton.setDefaultButton(true);
            url.setVisible(false);
            bgbutton.setText("BG");
            mid_label.setVisible(true);
            url.setText("");
            name_field.setVisible(true);
            label2.setText("");
        }
    }

    public void exit(ActionEvent actionEvent) {
        try {
            Server.logout();
            Server.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Platform.exit();
        }
        Platform.exit();
    }

    public void toolPressed(MouseEvent mouseEvent) {
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();
    }

    public void toolDragged(MouseEvent mouseEvent) {
        top.getScene().getWindow().setX(mouseEvent.getScreenX() - xOffset);
        top.getScene().getWindow().setY(mouseEvent.getScreenY() - yOffset);
    }

    public void minimize(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void anchor(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        if (anchored) {
            stage.setAlwaysOnTop(false);
            anchored = !anchored;
        } else {
            stage.setAlwaysOnTop(true);
            anchored = !anchored;
        }
    }
}
