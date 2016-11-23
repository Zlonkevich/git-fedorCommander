package objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.*;

public class CollectionMyFile {

    private ObservableList<MyFile> firstFileList;
    private LinkedList <MyFile> rootList;

    //Constructor
    public CollectionMyFile(){
        setFirstFilesList();
    }

    public ObservableList<MyFile> getFirstMyFileList(){
        return firstFileList;
    }


    public void setFirstFilesList(){
        File[] roots = File.listRoots();
        rootList = new LinkedList<>();
        for(int i = 0; i < roots.length; i++){
            rootList.add(new MyFile(roots[i]));
        }
        firstFileList = FXCollections.observableList(rootList);
    }



}














