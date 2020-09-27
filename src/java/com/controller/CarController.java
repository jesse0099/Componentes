package com.controller;


import com.model.Car;
import com.model.Document;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nvidi
 */
@ManagedBean(name = "carcontroller")
@SessionScoped
public class CarController {
    
    private List<Car> cars = new ArrayList<>();
    private List<Car> cars1 = new ArrayList<>();
    private List<Car> cars6 = new ArrayList<>();
    private List<String> cities = new ArrayList<>();
    private DualListModel<String> citiesDual;
    private TreeNode rootDoc;
    
    
    private Car selectedCar = new Car();
    private Document selectedDocument;
    private TreeNode selectedDocNode;
    
    public CarController(){     
        onLoad();
    }
    
    public void onLoad(){
        List<String> citiesSource = new ArrayList<>();
        List<String> citiesTarget = new ArrayList<>();
        
        citiesSource.add("Ciudad 1");
        citiesSource.add("Ciudad 2");
        citiesSource.add("Ciudad 3");
        citiesSource.add("Ciudad 4");
        
        this.citiesDual = new DualListModel<String>(citiesSource,citiesTarget);
        
        this.rootDoc = rootLoad();
        this.cities.add("Managua");
        this.cities.add("San Jose");
        this.cities.add("Tokio");
        
        this.cars.add(new Car(1, 2, "Blue",789,"Audi"));
        this.cars.add(new Car(2, 3, "Red",789,"Jaguar"));
        this.cars.add(new Car(3, 4, "Green",789,"Fiat"));
        
        this.cars6 = this.cars;
        this.cars1 = this.cars6;
    }
    
    public TreeNode rootLoad(){
        TreeNode root  = new DefaultTreeNode(new Document("Files","-","Folder"),null);
        
        //Docs
        TreeNode documents = new DefaultTreeNode(new Document("Documents","","Folder"),root);
        TreeNode pictures = new DefaultTreeNode(new Document("Pictures","","Folder"),root);
        TreeNode others = new DefaultTreeNode(new Document("Others","","Folder"),root);
        //Files
        
        TreeNode file1D = new DefaultTreeNode(new Document("TrabajoFinal","30 KB","Word"),documents);
        TreeNode file1D2 = new DefaultTreeNode(new Document("CV","20 KB","Word"),documents);
        TreeNode file13 = new DefaultTreeNode(new Document("Anotaciones","30 KB","Word"),documents);
        
        TreeNode file14 = new DefaultTreeNode(new Document("IMG_03945734","50 KB","Word"),pictures);
        TreeNode file15 = new DefaultTreeNode(new Document("IMG_0394573434","70 KB","Word"),others);
        
        return root;
    }
    

    public List<Car> getCars() {
        return cars;
    }


    public Document getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public TreeNode getRootDoc() {
        return rootDoc;
    }

    public void setRootDoc(TreeNode rootDoc) {
        this.rootDoc = rootDoc;
    }

    
    
    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public Car getSelectedCar() {
        return selectedCar;
    }

    public void setSelectedCar(Car selectedCar) {
        this.selectedCar = selectedCar;
    }

    public TreeNode getSelectedDocNode() {
        return selectedDocNode;
    }

    public void setSelectedDocNode(TreeNode selectedDocNode) {
        this.selectedDocNode = selectedDocNode;
    }

    
    public List<Car> getCars1() {
        return cars1;
    }

    public void setCars1(List<Car> cars1) {
        this.cars1 = cars1;
    }

    public List<Car> getCars6() {
        return cars6;
    }

    public void setCars6(List<Car> cars6) {
        this.cars6 = cars6;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public DualListModel<String> getCitiesDual() {
        return citiesDual;
    }

    public void setCitiesDual(DualListModel<String> citiesDual) {
        this.citiesDual = citiesDual;
    }
    
 
   

    
    
}
