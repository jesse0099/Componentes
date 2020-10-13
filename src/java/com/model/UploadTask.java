/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 * @author Nvidi
 */
public class UploadTask extends SwingWorker<Void, Integer>{

    private String uploadURL;
    private File uploadFile;
    
    public UploadTask(String uploadURL, File uploadFile){
        this.uploadURL = uploadURL;
        this.uploadFile = uploadFile;
    }
    
    
    /*Metodos swing worker*/
    @Override
    protected Void doInBackground() {
           try {
            MultipartUploadUtility util = new MultipartUploadUtility(uploadURL,
                    "UTF-8");
            util.addFilePart("uploadFile", uploadFile);
 
            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = uploadFile.length();
 
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                util.writeFileBytes(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                percentCompleted = (int) (totalBytesRead * 100 / fileSize);
                setProgress(percentCompleted);
            }
 
            inputStream.close();
            util.finish();
        } catch (IOException ex) {       
            ex.printStackTrace();
            setProgress(0);
            cancel(true);
        }
 
        return null;
    }
    
    @Override
    protected void done() {
        if (!isCancelled()) {
            System.out.println("===Archivo enviado correctamente===");
        }
    }
    
}
