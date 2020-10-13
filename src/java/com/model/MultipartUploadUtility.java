/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 *Migrado de gestor de subida de archivos swing a web
 * Clase encargada de crear un modelo para el envio de 
 * solicitudes POST a un servidor web
 * @author Nvidi
 */
public class MultipartUploadUtility {
    
    /*Variables*/
    private final String separador; 
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;
    
    public MultipartUploadUtility(String requestURL, String charset)
            throws IOException {
 
        // separador unico basado en el tiempo
        separador = "===" + System.currentTimeMillis() + "===";
 
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoOutput(true); // POST 
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + separador);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }
    /**
     * Agrega una seccion de subida de archivos a la solicitud
     * 
     * @param fieldName 
     * @param uploadFile
     * @throws IOException 
     */
    public void addFilePart(String fieldName, File uploadFile) 
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + separador).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
    }
    
    /**
    * Escribe un arreglo de bytes para la solicitud del outputStream
    */
    public void writeFileBytes(byte[] bytes, int offset, int length)
            throws IOException {
        outputStream.write(bytes, offset, length);
    }
    
    /**
     * Complete the request and receives response from the server.
     *
     * @throws IOException
     *            Por errores en la red
     *            Cuando el server devuelve un codigo de estado distinto a OK
     */
        public void finish() throws IOException {
            outputStream.flush();
            writer.append(LINE_FEED);
            writer.flush();

            writer.append(LINE_FEED).flush();
            writer.append("--" + separador + "--").append(LINE_FEED);
            writer.close();

            // Revisa el estado retornado por el server
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                while (reader.readLine() != null) {
                    //Que hacer si rersponde
                    System.err.println("====Respuesta===="+httpConn.getContentLength());
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Error : " + status);
            }
        }
    }
    

