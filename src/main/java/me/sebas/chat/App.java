package me.sebas.chat;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import me.sebas.chat.contact.ContactEntry;
import me.sebas.chat.io.FileIO;
import javafx.scene.input.MouseEvent;

/**
 * JavaFX App
 */
public class App extends Application {
	String friends[] = {"Friend 1", "Friend 2", "Friend 3"};
	final static int PORT = 9988;
	/**
	 * Reescribe el método {@link start} para generar los elementos de la GUI
	 */
    @Override
    public void start(Stage stage) {
    	/* Friend list */
    	var names = FXCollections.observableArrayList(friends);
    	var listView = new ListView<String>(names);
    	listView.setMinHeight(600);
    	listView.setPadding(new Insets(10, 10, 10 ,10));
    	/*Chat textarea */
    	var chatTextArea = new TextArea();
    	chatTextArea.setMinHeight(550);
    	chatTextArea.setPadding(new Insets(10, 10, 10, 10));
    	/*Chat response input */
    	var chatInput = new TextField();
    	chatInput.setMinWidth(400);
    	chatInput.setPadding(new Insets(10, 10, 10, 10));
    	/* Send button */
    	var sendButton = new Button("Send");
    	sendButton.setMinWidth(200);
    	sendButton.setMinHeight(10);
    	sendButton.setPadding(new Insets(10, 10, 10, 10));
    	/*Grid Panel*/
    	var panel = new GridPane();
    	GridPane.setConstraints(listView, 0, 0, 1, 3);
    	GridPane.setConstraints(chatTextArea, 1, 0);
    	GridPane.setConstraints(chatInput, 1, 1);
    	GridPane.setConstraints(sendButton, 1, 2);
    	panel.getChildren().addAll(listView, chatTextArea, chatInput, sendButton);
        /* Dimensiones del panel */
    	panel.getColumnConstraints().add(0, new ColumnConstraints(240));
    	panel.getColumnConstraints().add(1, new ColumnConstraints(420));
    	/* Event handlers */
    	sendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				chatTextArea.setText(chatInput.getText());
			}
    		
    	});
    	/* Mostrar escena */
    	var scene = new Scene(panel, 660, 700);
        stage.setScene(scene);
        stage.setTitle("Chatt");
        stage.show();
    }
    
    public static void main(String[] args) {
    	var sc = new Scanner(System.in);
        var contacts = FileIO.readContacts();
        ChatClientRunner currentChat = null;
        ChatManager chatManager = null;
        try {
        	chatManager = new ChatManager(contacts, "me");
			(new Thread(chatManager)).start();
        } catch (IOException e) {
			 System.err.println("Error escuchando conexiones ...");
			e.printStackTrace();
		}
        System.out.println("Terminando inicialización ... ");
        for(;;) {
        	String comm = sc.nextLine();
        	String tokens[] = comm.split("\\s");
//        	for(String s : tokens) {
//        		System.out.println(s);
//        	}
        	if(tokens[0].equals("exit")) {
        		System.out.println(">exit");
        		System.exit(0);
        	}
        	else if(tokens[0].contentEquals("list")) {
        		System.out.println(">list");
        		for(Map.Entry<String, Boolean> e : chatManager.getConnected().entrySet()) {
    				System.out.println("[ " + e.getKey() + " ] := " + e.getValue());
    	    	}
        	}
        	else if(tokens[0].equals("cd")) {
        		if(chatManager.getConnected().containsKey(tokens[1])) {
        			currentChat = chatManager.getChatThreads().get(tokens[1]);
        		}else {
        			System.out.println("Error cambiando a " + tokens[1]);
        		}
        	}
        	else {
        		currentChat.sendMsg(comm);
        	}
        }
    }

}