package client;

import client.data.DataElementWagon;
import client.data.DataWagonTableView;
import client.network.DataNetwork;
import client.setting.DataSetting;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//**************************************************************
//Программирование окна, взаимодействующего с таблицей
//зарегистрированных камерой полувагонов
//**************************************************************

public class Wagons {
    //элементы управления
    private MenuBar _menuBar = null;
    private TableView<DataWagonTableView> _table = null;

    //взаимосвязи между окнами
    private Stage _thisStage = null;            //ссылка на собственный stage
    public static Stage stageInvoices = null;   //stage окна таблицы накладных
    public static Stage stageRegister = null;   //stage окна таблицы регистрации
    public static Stage stageSetting  = null;   //stage окна настройки

    private Button _loadImage = null;
    private ScrollPane _imagePlace = null;
    private CheckBox _deleteImage = null;
    private ImageView _currentImage = null;

    private int _currentRow = (-1);             //текущая строка в таблице, которую пользователь выбрал

    private volatile boolean _readMark = true;
    private Thread _threadReadData = null;

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
        MenuItem sett = new MenuItem("Настройки");

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

        sett.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageSetting.show();
                _readMark = false;
            }
        });

        menu.getItems().add(inv);
        menu.getItems().add(reg);
        menu.getItems().add(sett);
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
                _threadReadData = new Thread(() -> {
                    while(_readMark){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                readDataWagons();
                            }
                        });

                        try {
                            _threadReadData.sleep(DataSetting.timeRead);
                        } catch (Exception e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                                }
                            });
                        }
                    }
                });

                _threadReadData.start();
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

        //обработка события, при нажатии кнопки с помощью которой изображение загружается из
        //локального хранилища на серверной части приложения
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

                //если выбран пункт "удалить после загрузки", то изображение будет удалено из локального
                //хранилища клиентского приложения
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
