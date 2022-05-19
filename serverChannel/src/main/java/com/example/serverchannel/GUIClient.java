package com.example.serverchannel;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;


public class GUIClient extends Application {

    GridPane grid;
    static ListView<String> listView, secondList;
    Button subscribeButton, unsubscribeButton, updateButton;
    static TextArea textArea;
    Scene mainScene;
    ButtonBar buttonBar;
    Label topicLabel;
    TextField topicField;
    String nameOfTopic;



    @Override
    public void start(Stage stage) throws Exception {
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(3);
        grid.setVgap(3);
        grid.setPadding(new Insets(5));
        listView = new ListView<>();
        listView.getItems().addAll(Client.getTopic().split(",,"));
        secondList = new ListView<>();
        subscribeButton = new Button("Subscribe");
        unsubscribeButton = new Button("Unsubscribe");
        updateButton = new Button("Update");
        buttonBar = new ButtonBar();
        buttonBar.getButtons().addAll(subscribeButton, unsubscribeButton, updateButton);

        textArea = new TextArea();
        textArea.setPrefHeight(400);
        textArea.setEditable(false);

        grid.add(listView, 0, 1);
        grid.add(secondList, 1, 1);
        grid.add(textArea, 0, 2, 2, 1);
        grid.add(buttonBar, 0, 3, 2, 1);

        subscribeButton.setOnAction(event -> {
            if(listView.getSelectionModel().getSelectedItem() != null){
                secondList.getItems().add(listView.getSelectionModel().getSelectedItem());
            }
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());

        });

        unsubscribeButton.setOnAction(event -> {
            if(secondList.getSelectionModel().getSelectedItem() != null){
                listView.getItems().add(secondList.getSelectionModel().getSelectedItem());

            }
            secondList.getItems().remove(secondList.getSelectionModel().getSelectedItem());
        });

        updateButton.setOnAction(evenet -> {
            try {
                Client.updateTopic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Scene scene = new Scene(grid, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Client");
        stage.show();
    }


}

