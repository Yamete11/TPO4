package com.example.serverchannel;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIAdmin extends Application {
    GridPane grid, secondGrid, thirdGrid;
    ListView<String> listView;
    Button removeButton, addButton, newsButton, acceptButton, cancelButton,
            thirdAcceptButton, thirdCancelButton;
    Scene mainScene, addScene, newsScene;
    ButtonBar buttonBar, secondButtonBar, thirdButtonBar;
    Label topicLabel, updateTopic, warningLabel;
    TextField topicField;
    String nameOfTopic;
    TextArea updateField;


    @Override
    public void start(Stage stage) throws Exception {

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(5));

        buttonBar = new ButtonBar();
        removeButton = new Button("Remove");
        addButton = new Button("Add");
        newsButton = new Button("Update");
        warningLabel = new Label();

        addButton.setOnAction(event -> {
            stage.setScene(addScene);
        });
        removeButton.setOnAction(event -> {
            String word = listView.getSelectionModel().getSelectedItem();
            try {
                Admin.addTopic(word, "remove", "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());

        });
        newsButton.setOnAction(event -> {
            String topic = listView.getSelectionModel().getSelectedItem();
            String text = "";
            if(topic != null){
                try {
                    text = Admin.updateTopic(topic, "getText");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (text.equals(" ")) {
                    updateField.setText("");
                } else {
                    updateField.setText(text);
                }
                updateTopic.setText(topic);
                stage.setScene(newsScene);
            } else {
                warningLabel.setText("You have to pick a topic!");
            }
        });
        buttonBar.getButtons().addAll(removeButton, addButton);

        listView = new ListView<>();
        listView.getItems().addAll(Admin.getTopic().split(",,"));

        grid.add(warningLabel, 0, 0);
        grid.add(listView, 0, 1);
        grid.add(newsButton, 2, 1);
        grid.add(buttonBar, 0, 2);

        mainScene = new Scene(grid, 400, 300);

        //======================================================

        secondGrid = new GridPane();
        secondGrid.setAlignment(Pos.CENTER);
        secondGrid.setHgap(10);
        secondGrid.setVgap(10);
        secondGrid.setPadding(new Insets(5));

        topicLabel = new Label("New Topic : ");
        topicField = new TextField();

        acceptButton = new Button("Accept");
        cancelButton = new Button("Cancel");
        secondButtonBar = new ButtonBar();
        secondButtonBar.getButtons().addAll(acceptButton, cancelButton);

        acceptButton.setOnAction(event -> {
            nameOfTopic = topicField.getText();
            if(listView.getItems().contains(nameOfTopic)){
                warningLabel.setText("Topic already exists!");
                stage.setScene(mainScene);
            }   else if(nameOfTopic.equals("")){
                warningLabel.setText("You have to enter something");
                stage.setScene(mainScene);
            } else {
                listView.getItems().add(nameOfTopic);
                topicField.clear();
                stage.setScene(mainScene);
                try {
                    Admin.addTopic(nameOfTopic, "add", " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        cancelButton.setOnAction(event -> {
            topicField.clear();
            stage.setScene(mainScene);
        });


        secondGrid.add(topicLabel, 0,0);
        secondGrid.add(topicField, 1, 0);
        secondGrid.add(secondButtonBar, 1, 2);

        thirdGrid = new GridPane();
        thirdGrid.setAlignment(Pos.CENTER);
        thirdGrid.setHgap(10);
        thirdGrid.setVgap(10);
        thirdGrid.setPadding(new Insets(5));

        thirdAcceptButton = new Button("Accept");
        thirdAcceptButton.setOnAction(event -> {
            warningLabel.setText("The content has been updated");
            try {
                String text = updateField.getText();
                updateField.clear();
                Admin.addTopic(updateTopic.getText(), "update", text);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(mainScene);
        });
        thirdCancelButton = new Button("Cancel");
        thirdCancelButton.setOnAction(event -> {
            updateField.clear();
            stage.setScene(mainScene);
        });
        thirdButtonBar = new ButtonBar();
        thirdButtonBar.getButtons().addAll(thirdAcceptButton, thirdCancelButton);

        updateField = new TextArea();
        updateTopic = new Label();


        thirdGrid.add(updateTopic, 0, 0);
        thirdGrid.add(updateField, 0, 1);
        thirdGrid.add(thirdButtonBar, 0, 2);

        addScene = new Scene(secondGrid, 300, 200);
        newsScene = new Scene(thirdGrid, 300, 200);


        stage.setTitle("Admin");
        stage.setScene(mainScene);
        stage.show();


    }
}

