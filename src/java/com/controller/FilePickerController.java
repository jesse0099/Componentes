/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.controller;

import com.model.UploadTask;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.Part;

/**
 *
 * @author Nvidi
 */
@ManagedBean
@ViewScoped
public class FilePickerController  implements Serializable{
    
    private Part uploadedFile;
    private String folder = "c:\\UploaderTest";
    private String serverURL;
    public static int progress;
    
    public String getServerURL() {
        return serverURL;
    }
    
    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }
    
    public Part getUploadedFile() {
        return uploadedFile;
    }
    
    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    
    public String getFolder() {
        return folder;
    }
    
    public void setFolder(String folder) {
        this.folder = folder;
    }
    
    public void sendFile() {
        
        if (serverURL.equals("")) {
            System.err.println("====El URL no es valido====");
            return;
        }
        if (uploadedFile == null) {
            System.err.println("====Ningun archivo seleccionado====");
            return;
        }
        
        try {
            InputStream streamFile = uploadedFile.getInputStream();
            String fileName = uploadedFile.getSubmittedFileName();
            
            File newFile = new File(folder, fileName);
            
            Files.copy(streamFile, newFile.toPath());
            
            UploadTask task = new UploadTask(serverURL, newFile);
            task.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress" == evt.getPropertyName()) {
                        int progress = (Integer) evt.getNewValue();
                        FilePickerController.progress = progress; 
                    }
                }
            });
            task.execute();
            
   
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(""+ex.getMessage());
        }

//        try (InputStream input = uploadedFile.getInputStream()) {
//            String fileName = uploadedFile.getSubmittedFileName();
//            Files.copy(input, new File(folder, fileName).toPath());
//            System.err.println("Archivo copiado");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    
}
