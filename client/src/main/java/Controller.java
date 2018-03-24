import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authorized;
    private String login;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passField;

    @FXML
    TextArea mainTextArea;

    @FXML
    HBox authPanel;

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if (this.authorized) {
            authPanel.setVisible(false);
            authPanel.setManaged(false);
        } else {
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            login = "";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 9000);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/authok ")) {
                            login = str.split(" ")[1];
                            setAuthorized(true);
                            break;
                        }
                        mainTextArea.appendText(str);
                        mainTextArea.appendText("\n");
                    }
                    while (true) {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                    showAlert("Disconnect from server!");
                    setAuthorized(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) connect();
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
            loginField.clear();
            passField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Please check connection");
        }
    }

    public void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            alert.showAndWait();
        });
    }
}
