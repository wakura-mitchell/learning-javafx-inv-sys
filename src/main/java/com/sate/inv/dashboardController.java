package com.sate.inv;

import javafx.beans.Observable;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class dashboardController implements Initializable {

    @FXML
    private AnchorPane addProductForm;

    @FXML
    private ImageView addProduct_Img;

    @FXML
    private Button addProduct_addBtn;

    @FXML
    private TableColumn<productData, String> addProduct_col_brand;

    @FXML
    private TableColumn<productData, String> addProduct_col_price;

    @FXML
    private TableColumn<productData, String> addProduct_col_productID;

    @FXML
    private TableColumn<productData, String> addProduct_col_productName;

    @FXML
    private TableColumn<productData, String> addProduct_col_status;

    @FXML
    private TableColumn<productData, String> addProduct_col_type;

    @FXML
    private Button addProduct_deleteBtn;

    @FXML
    private Button addProduct_importBtn;

    @FXML
    private TextField addProduct_productBrand;

    @FXML
    private TextField addProduct_productID;

    @FXML
    private TextField addProduct_productName;

    @FXML
    private TextField addProduct_productPrice;

    @FXML
    private Button addProduct_resetBtn;

    @FXML
    private TextField addProduct_search;

    @FXML
    private ComboBox<?> addProduct_status;

    @FXML
    private TableView<productData> addProduct_table;

    @FXML
    private ComboBox<?> addProduct_type;

    @FXML
    private Button addProduct_updateBtn;

    @FXML
    private Button add_productBtn;

    @FXML
    private Button close;

    @FXML
    private Button homeBtn;

    @FXML
    private AnchorPane homeForm;

    @FXML
    private Label home_availableProducts;

    @FXML
    private AreaChart<?, ?> home_incomeChart;

    @FXML
    private BarChart<?, ?> home_orderChart;

    @FXML
    private Label home_totalIncome;

    @FXML
    private Label home_totalOrders;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane mainForm;

    @FXML
    private Button minimize;

    @FXML
    private ComboBox<?> order_brand;

    @FXML
    private ComboBox<?> order_productName;

    @FXML
    private ComboBox<?> order_productType;

    @FXML
    private TableView<?> order_tableView;

    @FXML
    private Button ordersBtn;

    @FXML
    private AnchorPane ordersForm;

    @FXML
    private TextField orders_amount;

    @FXML
    private Label orders_balance;

    @FXML
    private TableColumn<?, ?> orders_col_brand;

    @FXML
    private TableColumn<?, ?> orders_col_ouantity;

    @FXML
    private TableColumn<?, ?> orders_col_price;

    @FXML
    private TableColumn<?, ?> orders_col_productName;

    @FXML
    private TableColumn<?, ?> orders_col_type;

    @FXML
    private Spinner<?> orders_quantity;

    @FXML
    private Button orders_recieptBtn;

    @FXML
    private Button orders_resetBtn;

    @FXML
    private Label orders_total;

    @FXML
    private Button orders_updateBtn;

    @FXML
    private Label username;

    @FXML
    private Button orders_addBtn;

    //Database tools
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;

    private Image image;

    public void addProductsAdd(){
        String sql = "INSERT INTO product(product_id, type, brand, productName, price, status, image, date)"
                + "VALUES(?,?,?,?,?,?,?,?)";

        connect = database.connectDb();

        try {

            Alert alert;

            if (
                    addProduct_productID.getText().isEmpty()
                    || addProduct_type.getSelectionModel().getSelectedItem() == null
                    || addProduct_productBrand.getText().isEmpty()
                    || addProduct_productName.getText().isEmpty()
                    || addProduct_productPrice.getText().isEmpty()
                    || addProduct_status.getSelectionModel().getSelectedItem() == null
                    || getData.path == ""

            ){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all required Information and proceed");
                alert.showAndWait();
            }

            else {

                //checking if the product is already in the database
                String checkDate = "SELECT product_id FROM product WHERE product_id = '"+addProduct_productID.getText()+"'";

                statement = connect.createStatement();
                result = statement.executeQuery(checkDate);

                //prompting the availability of the product
                if(result.next()){
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product ID "+addProduct_productID.getText()+"Was already available");
                    alert.showAndWait();
                }

                else {
                    prepare = connect.prepareStatement(sql);
                    prepare.setString(1, addProduct_productID.getText());
                    prepare.setString(2, (String) addProduct_type.getSelectionModel().getSelectedItem());
                    prepare.setString(3, addProduct_productBrand.getText());
                    prepare.setString(4, addProduct_productName.getText());
                    prepare.setString(5, addProduct_productPrice.getText());
                    prepare.setString(6, (String) addProduct_status.getSelectionModel().getSelectedItem());

                    String uri = getData.path;
                    uri =   uri.replace("\\", "\\\\");
                    prepare.setString(7, uri);

                    Date date = new Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                    prepare.setString(8, String.valueOf(sqlDate));

                    prepare.executeUpdate();

                    //update table
                    addProductsShowListData();

                    //clear form
                    addProductResetBtn();
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addProduct_updateBtn(){

        String uri = getData.path;
        uri = uri.replace("\\", "\\\\");

        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String sql = "UPDATE product SET type = '"+addProduct_type.getSelectionModel().getSelectedItem()
                +"', brand = '"+addProduct_productBrand.getText()
                +"', productName = '"+addProduct_productName.getText()
                +"', price = '"+addProduct_productPrice.getText()
                +"', status = '"+addProduct_status.getSelectionModel().getSelectedItem()
                +"', image = '"+uri
                +"', date = '"+sqlDate
                +"' WHERE product_id = '"+addProduct_productID.getText()+"'";

        connect = database.connectDb();

        try{
            Alert alert;

            if (
                    addProduct_productID.getText().isEmpty()
                            || addProduct_type.getSelectionModel().getSelectedItem() == null
                            || addProduct_productBrand.getText().isEmpty()
                            || addProduct_productName.getText().isEmpty()
                            || addProduct_productPrice.getText().isEmpty()
                            || addProduct_status.getSelectionModel().getSelectedItem() == null
                            || getData.path == ""

            ){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all required Information and proceed");
                alert.showAndWait();
            }

            else {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Conformation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to UPDATE Product ID: "+addProduct_productID.getText()+"?");

                Optional<ButtonType> option = alert.showAndWait();

                if(option.get().equals(ButtonType.OK)){
                    statement = connect.createStatement();
                    statement.executeUpdate(sql);

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product UPDATE was Successful");
                    alert.showAndWait();

                    addProductsShowListData();
                    addProductResetBtn();

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addProduct_deleteBtn(){
        String sql = "DELETE FROM product WHERE product_id = '"+addProduct_productID.getText()+"'";

        connect = database.connectDb();

        try {
            Alert alert;

            if (
                    addProduct_productID.getText().isEmpty()
                            || addProduct_type.getSelectionModel().getSelectedItem() == null
                            || addProduct_productBrand.getText().isEmpty()
                            || addProduct_productName.getText().isEmpty()
                            || addProduct_productPrice.getText().isEmpty()
                            || addProduct_status.getSelectionModel().getSelectedItem() == null
                            || getData.path == ""

            ){
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all required Information and proceed");
                alert.showAndWait();
            }

            else {

                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Conformation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to DELETE Product ID: "+addProduct_productID.getText()+"?");

                Optional<ButtonType> option = alert.showAndWait();

                if(option.get().equals(ButtonType.OK)){
                    statement = connect.createStatement();
                    statement.executeUpdate(sql);

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Product DELETE was Successful");
                    alert.showAndWait();

                    addProductsShowListData();
                    addProductResetBtn();

                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addProductResetBtn(){
        addProduct_productID.setText("");
        addProduct_type.getSelectionModel().clearSelection();
        addProduct_productBrand.setText("");
        addProduct_productName.setText("");
        addProduct_productPrice.setText("");
        addProduct_status.getSelectionModel().clearSelection();
        addProduct_Img.setImage(null);
        getData.path = "";

    }

    public void addProductImportImage(){
        FileChooser open = new FileChooser();
        open.setTitle("Select Product image");
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File"
                , "*.jpg"
                , "*.png"
                , "*.btn"));

        File file = open.showOpenDialog(mainForm.getScene().getWindow());

        if(file != null){

            getData.path = file.getAbsolutePath();

            image = new Image(file.toURI().toString(), 115, 130, false, true);
            addProduct_Img.setImage(image);

        }
    }

    private String[] listType = {"Snacks", "Drinks", "Stationary", "Hardware","Upfu", "Clothes", "Gadgets", "Other"};

    public void addProductslistStyle(){

        List<String> listT = new ArrayList<>();

        for (String data: listType){
            listT.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listT);
        addProduct_type.setItems(listData);
    }

    private String[] listStatus = {"Available", "Out Of Stock"};

    public void addProductslistStatus(){
        List<String> listS = new ArrayList<>();

        for (String data: listStatus){
            listS.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listS);
        addProduct_status.setItems(listData);
    }

    //search functionality
    public void addProductsSearch(){
        FilteredList<productData> filter = new FilteredList<>(addProductList, e -> true);

        addProduct_search.textProperty().addListener((Observable, oldValue, newValue) -> {
                filter.setPredicate(predicateProductDate -> {

                    if (oldValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String searchKey = newValue.toLowerCase();

                    if (predicateProductDate.getProductId().toString().contains(searchKey)) {
                        return true;
                    } else if (predicateProductDate.getType().toLowerCase().contains(searchKey)) {
                        return  true;                        
                    } else if (predicateProductDate.getBrand().toLowerCase().contains(searchKey)) {
                        return true;
                    } else if (predicateProductDate.getProductName().toLowerCase().contains(searchKey)) {
                        return true;
                    } else if (predicateProductDate.getPrice().toString().contains(searchKey)) {
                        return true;
                    } else if (predicateProductDate.getStatus().toLowerCase().contains(searchKey)) {
                        return true;
                    }

                    else return false;
                });
        });

        SortedList<productData> sortList = new SortedList<>(filter);
        sortList.comparatorProperty().bind(addProduct_table.comparatorProperty());
        addProduct_table.setItems(sortList);
    }
    private ObservableList<productData> addProductListData(){
        ObservableList<productData> productList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM product";

        connect = database.connectDb();

        try {

            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            productData prodD;

            while (result.next()){
                prodD = new productData(result.getInt("product_id")
                        , result.getString("type")
                        , result.getString("brand")
                        , result.getString("productName")
                        , result.getDouble("price")
                        , result.getString("status")
                        , result.getString("image")
                        , result.getDate("date"));
                productList.add(prodD);

            }

        }catch (Exception e){e.printStackTrace();}

        return productList;
    }

    //displaying data
    private ObservableList<productData> addProductList;
    public void addProductsShowListData(){
        addProductList = addProductListData();

        addProduct_col_productID.setCellValueFactory(new PropertyValueFactory<>("productId"));
        addProduct_col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        addProduct_col_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        addProduct_col_productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        addProduct_col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        addProduct_col_status.setCellValueFactory(new PropertyValueFactory<>("status"));

        addProduct_table.setItems(addProductList);

    }

    public void addProductsSelect(){
        productData prodD = addProduct_table.getSelectionModel().getSelectedItem();
        int num = addProduct_table.getSelectionModel().getFocusedIndex();

        if ((num - 1) < -1){return;}

        addProduct_productID.setText(String.valueOf(prodD.getProductId()));
        addProduct_productBrand.setText(prodD.getBrand());
        addProduct_productName.setText(prodD.getProductName());
        addProduct_productPrice.setText(String.valueOf(prodD.getPrice()));

        String uri = "file:" + prodD.getImage();

        image = new Image(uri, 115, 130, false, true);
        addProduct_Img.setImage(image);
        getData.path = prodD.getImage();

    }


    //switching forms
    public  void switchForm(ActionEvent event){
        if(event.getSource() == homeBtn){
            homeForm.setVisible(true);
            addProductForm.setVisible(false);
            ordersForm.setVisible(false);

            //setting styles to show what is selected
            homeBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #25a473, #89892b);");
            add_productBtn.setStyle("-fx-background-color: transparent");
            ordersBtn.setStyle("-fx-background-color: transparent");
        }
        else if (event.getSource() == add_productBtn) {
            homeForm.setVisible(false);
            addProductForm.setVisible(true);
            ordersForm.setVisible(false);

            //setting styles to show what is selected
            add_productBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #25a473, #89892b);");
            homeBtn.setStyle("-fx-background-color: transparent");
            ordersBtn.setStyle("-fx-background-color: transparent");

            addProductsShowListData();
            addProductslistStatus();
            addProductslistStyle();
            addProductsSearch();

        }

        else if (event.getSource() == ordersBtn) {
            homeForm.setVisible(false);
            addProductForm.setVisible(false);
            ordersForm.setVisible(true);

            //setting styles to show what is selected
            ordersBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #25a473, #89892b);");
            add_productBtn.setStyle("-fx-background-color: transparent");
            homeBtn.setStyle("-fx-background-color: transparent");
        }
    }

    public double x = 0;
    public double y = 0;
    //logout
    public void logout(){
        try {
            //alert message after logout button is pressed
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();

            if(option.get().equals(ButtonType.OK)){

                logout.getScene().getWindow().hide();

                //link to login form
                Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

                Stage stage = new Stage();
                Scene scene = new Scene(root);

                scene.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });

                scene.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() -x);
                    stage.setY(event.getScreenY() -y);
                    stage.setOpacity(.8);
                });

                scene.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();

            }

            else return;

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    //close and minimize
    public void close() {
        System.exit(0);
    }

    public void minimize(){
        Stage stage = (Stage)mainForm.getScene().getWindow();
        stage.setIconified(true);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //to display the data in table
        addProductsShowListData();
        addProductslistStatus();
        addProductslistStyle();

    }
}
