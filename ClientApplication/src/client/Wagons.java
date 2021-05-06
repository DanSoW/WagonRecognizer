package client;

import client.data.DataElementRegister;
import client.data.DataElementWagon;
import client.data.DataRegisterTableView;
import client.data.DataWagonTableView;
import client.network.DataNetwork;
import com.sun.javafx.tk.ImageLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Wagons {
    //элементы управления
    private MenuBar _menuBar = null;
    private TableView<DataWagonTableView> _table = null;

    //взаимосвязи между окнами
    private Stage _thisStage = null;            //ссылка на собственный stage
    public static Stage stageInvoices = null;   //stage окна таблицы накладных
    public static Stage stageRegister= null;    //stage окна таблицы регистрации

    public Button _loadImage = null;
    public ScrollPane _imagePlace = null;
    public CheckBox _deleteImage = null;
    public ImageView _currentImage = null;

    private int _currentRow = (-1); //текущая строка в таблице, которую пользователь выбрал

    private volatile boolean _readMark = true;
    private volatile int _timeRead = 10000;
    private Thread threadReadData = null;

    public void Show(){
        if(this._thisStage == null)
            return;
        this._thisStage.show();
    }

    public void Hide(){
        if(this._thisStage == null)
            return;
        this._thisStage.hide();
        _readMark = false;
    }

    public Stage GetStage(){
        return this._thisStage;
    }

    public Wagons() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/wagons_table_view.fxml"));
        Scene scene = new Scene(root);
        _thisStage = new Stage();
        _thisStage.setScene(scene);
        _thisStage.setTitle("Полувагоны");

        _loadImage = (Button)scene.lookup("#_loadImage");
        _imagePlace = (ScrollPane)scene.lookup("#_imagePlace");
        _deleteImage = (CheckBox)scene.lookup("#_deleteImage");

        _table = (TableView)scene.lookup("#_tableView");
        _table.getColumns().clear();

        _menuBar = (MenuBar)scene.lookup("#_menuBar");
        _menuBar.getMenus().clear();

        Menu menu = new Menu("Таблица");
        MenuItem inv = new MenuItem("Таблица накладных");
        MenuItem reg = new MenuItem("Таблица регистрации");

        inv.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageInvoices.show();
                _readMark = false;
            }
        });

        reg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageRegister.show();
                _readMark = false;
            }
        });

        menu.getItems().add(inv);
        menu.getItems().add(reg);
        _menuBar.getMenus().add(0, menu);

        TableColumn<DataWagonTableView, Integer> attrib1 = new TableColumn<DataWagonTableView, Integer>("Номер полувагона");
        attrib1.setCellValueFactory(new PropertyValueFactory<DataWagonTableView, Integer>("numberWagon"));
        _table.getColumns().add(attrib1);

        TableColumn<DataWagonTableView, String> attrib2 = new TableColumn<DataWagonTableView, String>("Дата прибытия");
        attrib2.setCellValueFactory(new PropertyValueFactory<DataWagonTableView, String>("arrivalDate"));
        _table.getColumns().add(attrib2);

        attrib2 = new TableColumn<DataWagonTableView, String>("Путь к изображению");
        attrib2.setCellValueFactory(new PropertyValueFactory<DataWagonTableView, String>("imagePath"));
        _table.getColumns().add(attrib2);

        TableColumn<DataWagonTableView, Double> attrib3 = new TableColumn<DataWagonTableView, Double>("Уровень распознования");
        attrib3.setCellValueFactory(new PropertyValueFactory<DataWagonTableView, Double>("levelCorrectRecognize"));
        _table.getColumns().add(attrib3);

        _thisStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                _readMark = false;
            }
        });

        _thisStage.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                _readMark = true;
                threadReadData = new Thread(() -> {
                    while(_readMark){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                readDataWagons();
                            }
                        });

                        try {
                            threadReadData.sleep(_timeRead);
                        } catch (InterruptedException e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                                }
                            });
                        }
                    }
                });

                threadReadData.start();
            }
        });

        _table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((_table != null) && (_table.getItems().size() > 0)){
                    try{
                        _currentRow = _table.getSelectionModel().getSelectedIndex();
                    }catch(Exception e){
                        _currentRow = (-1);
                    }
                }
            }
        });

        _loadImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(_currentRow < 0){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Не выбрано строка с изображением для загрузки!");
                    return;
                }

                String[] imageData = _table.getItems().get(_currentRow).getImagePath().split("\\\\");
                String filePath = null;

                try{
                    filePath = DataNetwork.loadImage("http://localhost:8080/load/" + imageData[(imageData.length - 1)]);
                }catch (Exception e){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }

                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Image img = new Image(inputStream);
                _currentImage = new ImageView(img);
                _imagePlace.setContent(_currentImage);
                _imagePlace.setPannable(true);

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(_deleteImage.isSelected()){
                    (new File(filePath)).delete();
                }
            }
        });
    }

    private void readDataWagons(){
        if(_table == null)
            return;
        _table.getItems().clear();
        DataElementWagon[] elements = null;

        try {
            elements = DataNetwork.getListDataWagons("http://localhost:8080/database/wagons/get/all");
        } catch (Exception e) {
            Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
            _readMark = false;
            return;
        }

        for(DataElementWagon i : elements){
            _table.getItems().add(new DataWagonTableView(
                    i.numberWagon,
                    i.arrivalDate,
                    i.imagePath,
                    i.levelCorrectRecognize
            ));
        }
    }
}
