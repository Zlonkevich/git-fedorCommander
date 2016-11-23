package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import objects.CollectionMyFile;
import objects.MyFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MainController {

    private Stack<MyFile> leftStack = new Stack<>();
    private Stack<MyFile> rightStack = new Stack<>();
    private CollectionMyFile collectionMyFile = new CollectionMyFile();
    private MyFile selectedMyFile;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<MyFile> tableLeft;
    @FXML
    private TableView<MyFile> tableRight;
    @FXML
    private TableColumn<MyFile, String> nameColumnLeft;
    @FXML
    private TableColumn<MyFile, String> nameColumnRight;
    @FXML
    private TableColumn<MyFile, String> pathColumnLeft;
    @FXML
    private TableColumn<MyFile, String> pathColumnRight;
    @FXML
    private TableColumn<MyFile, Long> sizeColumnLeft;
    @FXML
    private TableColumn<MyFile, Long> sizeColumnRight;

    @FXML
    private void initialize() {
        nameColumnLeft.setCellValueFactory(new PropertyValueFactory<MyFile, String>("name"));
        nameColumnRight.setCellValueFactory(new PropertyValueFactory<MyFile, String>("name"));
        pathColumnLeft.setCellValueFactory(new PropertyValueFactory<MyFile, String>("path"));
        pathColumnRight.setCellValueFactory(new PropertyValueFactory<MyFile, String>("path"));
        sizeColumnLeft.setCellValueFactory(new PropertyValueFactory<MyFile, Long>("size"));
        sizeColumnRight.setCellValueFactory(new PropertyValueFactory<MyFile, Long>("size"));

        tableLeft.setItems(collectionMyFile.getFirstMyFileList());
        tableRight.setItems(collectionMyFile.getFirstMyFileList());
    }

/* Метод задействуется по нажатию кнопки "In ->" в окне программы.
Сначала определяется в какой из таблиц была выбрана строка. Затем, если строка содержала папку,
эта папка записывается в стэк для реализации выхода на уровни выше. Далее выводится в таблицу содержимое этой папки.
Если был выбран файл, в командную строку выводится сообщение "Please, choose directory!"
Если выбранная папка с ограниченными правами доступа, срабатывает Exception и выводится сообщение
"Access denied to this folder!" Но тут есть косяк - она все равно записывается в стэк столько раз,
сколько будет нажата кнопка. Это можно решить, написав свой стэк на базе Set<E>*/



    public void startNavigationButton(ActionEvent ae) {
        try {
                doubleClick(tableLeft, leftStack);
                doubleClick(tableRight, rightStack);
        } catch (Exception e) {
            System.out.println("Access denied to this folder!");
        }
    }

    private void doubleClick(TableView<MyFile> table, Stack<MyFile> stack){
        table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        selectedMyFile = (MyFile) table.getSelectionModel().getSelectedItem();
                        if (selectedMyFile.isDirectory()) {
                            stack.push(selectedMyFile); // загружает в стек выбранную директорию
                            File[] children = selectedMyFile.listFiles();
                            table.setItems(getList(children));
                        } else {
                            System.out.println("Please, choose directory!");
                        }
                    }
                }
            }
        });
    }


    /*Следующие два метода работают по нажатию кнопок "<- Out", обращаются к своим стэкам и извлекают из них
    файлы-директории, выводя в таблицу их содержимое, и так до тех пор, пока не будет достигнут корневой каталог*/
    public void leftOutButton(ActionEvent ae) {
        try {
            if (!leftStack.empty()) {
                File[] children = leftStack.pop().listFiles();
                tableLeft.setItems(getList(children));
            } else {
                tableLeft.setItems(collectionMyFile.getFirstMyFileList());
                System.out.println("Left Stack is empty!");
            }
        } catch (Exception e) {
            System.out.println("Try again!");
        }
    }


    private File source, dest;
//    private MyFile

    public void rightOutButton(ActionEvent ae) {
        try {
            if (!rightStack.empty()) {
                File[] children = rightStack.pop().listFiles();
                tableRight.setItems(getList(children));
            } else {
                tableRight.setItems(collectionMyFile.getFirstMyFileList());
                System.out.println("Right Stack is empty!");
            }
        } catch (Exception e) {
            System.out.println("Try again!");
        }
    }

    public void copyButton(ActionEvent ae) throws IOException {
        try {
            if (tableLeft.getSelectionModel().getSelectedItem() != null) {
                source = tableLeft.getSelectionModel().getSelectedItem();
                dest = rightStack.peek();

                //проверка на совпадение ссылок source и dest
                if (!Files.isSameFile(source.toPath(), dest.toPath()))
                    copy(source, dest);
            } else if (tableRight.getSelectionModel().getSelectedItem() != null) {
                source = tableRight.getSelectionModel().getSelectedItem();
                dest = leftStack.peek();

                if (!Files.isSameFile(source.toPath(), dest.toPath()))
                    copy(source, dest);
            }
        } catch (IOException e) {
            System.out.println("Copying failed");
        }
    }

    public void copy(File source, File dest) throws IOException {
        File source1 = new File(source.toString());
        File dest1 = new File(dest.toString() + "\\" + source.getName());
        Files.copy(source1.toPath(), dest1.toPath(), StandardCopyOption.REPLACE_EXISTING);

    }

    public void createDirButton(ActionEvent ae) {
        try {
            MyFile selected;
            if (tableLeft.getSelectionModel().getSelectedItem() != null) {
                selected = leftStack.peek();
                Path selectedPath = selected.toPath();
                Files.createDirectory(selectedPath);
            } else if (tableRight.getSelectionModel().getSelectedItem() != null) {
                selected = rightStack.peek();
                Path newPath = selected.toPath();
                Files.createDirectory(newPath);
            }
        } catch (IOException e) {
            System.out.println("Exception in create Directory!");
        }

    }

    public void deleteButton(ActionEvent ae) {
        try {
            if (tableLeft.getSelectionModel().getSelectedItem() != null) {
                delete(tableLeft.getSelectionModel().getSelectedItem());
            } else if (tableRight.getSelectionModel().getSelectedItem() != null) {
                delete(tableRight.getSelectionModel().getSelectedItem());
            }
        } catch (Exception e) {
            System.out.println("Delete exception!");
        }
    }

    public void delete(MyFile file) {
        if (file.exists())
            file.delete();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ObservableList<MyFile> getList(File[] fileList) {
        ObservableList<MyFile> childList = FXCollections.observableArrayList();
        for (int i = 0; i < fileList.length; i++) {
            childList.add(new MyFile(fileList[i]));
        }
        return childList;
    }
}















