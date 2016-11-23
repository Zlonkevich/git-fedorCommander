package objects;

import java.io.File;

public class MyFile extends File{

    private File    file;
    private String  path;
    private String  name;
    private Long    size;


    public MyFile(File file){
        super(String.valueOf(file));
        path = file.getPath();
        this.file = file;
        name = file.getName();
        size = (Long)file.getTotalSpace()/1024;
    }

    public boolean isDirectory(){
        if(file.isDirectory() == true)
            return true;
        return false;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Long getSize(){return size;}


    public File getFile() {
        return file;
    }

}
