package client;

import client.data.*;
import client.network.DataNetwork;
import client.setting.DataSetting;
import client.validator.DataValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

//**************************************************************
//Программирование окна, взаимодействующего с таблицей
//соответствия конкретного полувагона определённой накладной
//**************************************************************

public class Register {

    //элементы управления
    private MenuBar _menuBar = null;
    private TableView<DataRegisterTableView> _table = null;

    private TextField _txtNumberInvoice = null;
    private TextField _txtNumberWagon = null;
    private TextField _txtSerialNumber = null;
    private TextField _txtSd = null;

    private Button _addData = null;
    private Button _updateData = null;
    private Button _deleteData = null;

    //взаимосвязи между окнами
    private Stage _thisStage = null;            //ссылка на собственный stage
    public static Stage stageInvoices = null;   //stage окна таблицы накладных
    public static Stage stageWagons = null;     //stage окна таблицы полувагонов
    public static Stage stageSetting = null;    //stage окна настройки приложения

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

    public Register() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/register_table_view.fxml"));
        Scene scene = new Scene(root);
        _thisStage = new Stage();
        _thisStage.setScene(scene);
        _thisStage.setTitle("Регистрация");

        _txtNumberInvoice = (TextField)scene.lookup("#_txtNumberInvoice");
        _txtNumberWagon = (TextField)scene.lookup("#_txtNumberWagon");
        _txtSerialNumber = (TextField)scene.lookup("#_txtSerialNumber");
        _txtSd = (TextField)scene.lookup("#_txtSd");
        _addData = (Button)scene.lookup("#_addData");
        _updateData = (Button)scene.lookup("#_updateData");
        _deleteData = (Button)scene.lookup("#_deleteData");

        _table = (TableView)scene.lookup("#_tableView");
        _table.getColumns().clear();

        _menuBar = (MenuBar)scene.lookup("#_menuBar");
        _menuBar.getMenus().clear();

        Menu menu = new Menu("Таблица");
        MenuItem inv = new MenuItem("Таблица накладных");
        MenuItem wag = new MenuItem("Таблица полувагонов");
        MenuItem sett = new MenuItem("Настройки");

        inv.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageInvoices.show();
                _readMark = false;
            }
        });

        wag.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageWagons.show();
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
        menu.getItems().add(wag);
        menu.getItems().add(sett);
        _menuBar.getMenus().add(0, menu);

        TableColumn<DataRegisterTableView, String> attrib1 = new TableColumn<DataRegisterTableView, String>("Номер накладной");
        attrib1.setCellValueFactory(new PropertyValueFactory<DataRegisterTableView, String>("numberInvoice"));
        _table.getColumns().add(attrib1);

        TableColumn<DataRegisterTableView, Integer> attrib2 = new TableColumn<DataRegisterTableView, Integer>("Номер полувагона");
        attrib2.setCellValueFactory(new PropertyValueFactory<DataRegisterTableView, Integer>("numberWagon"));
        _table.getColumns().add(attrib2);

        TableColumn<DataRegisterTableView, Boolean> attrib3 = new TableColumn<DataRegisterTableView, Boolean>("Отметка о прибытии");
        attrib3.setCellValueFactory(new PropertyValueFactory<DataRegisterTableView, Boolean>("arrivalMark"));
        _table.getColumns().add(attrib3);

        attrib2 = new TableColumn<DataRegisterTableView, Integer>("Номер в составе");
        attrib2.setCellValueFactory(new PropertyValueFactory<DataRegisterTableView, Integer>("serialNumber"));
        _table.getColumns().add(attrib2);

        attrib2 = new TableColumn<DataRegisterTableView, Integer>("Фактический номер в составе");
        attrib2.setCellValueFactory(new PropertyValueFactory<DataRegisterTableView, Integer>("actualSerialNumber"));
        _table.getColumns().add(attrib2);

        TableColumn<DataRegisterTableView, Float> attrib4 = new TableColumn<DataRegisterTableView, Float>("Sd%");
        attrib4.setCellValueFactory(new PropertyValueFactory<DataRegisterTableView, Float>("sD"));
        _table.getColumns().add(attrib4);

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
                                readDataRegister();
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
                            _readMark = false;
                        }
                    }
                });

                _threadReadData.start();
            }
        });

        _table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((event.getButton() == MouseButton.PRIMARY) && (_table.getItems().size() > 0)){
                    try{
                        int row = _table.getSelectionModel().getSelectedIndex();
                        DataRegisterTableView data = _table.getItems().get(row);
                        _txtNumberInvoice.setText(data.getNumberInvoice());
                        _txtNumberWagon.setText(String.valueOf(data.getNumberWagon()));
                        _txtSerialNumber.setText(String.valueOf(data.getSerialNumber()));
                        _txtSd.setText(String.valueOf(data.getsD()));
                    }catch(Exception e){}
                }
            }
        });

        _addData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!DataValidator.requiredValidator(new String[]{
                        _txtNumberInvoice.getText(),
                        _txtNumberWagon.getText(),
                        _txtSerialNumber.getText(),
                        _txtSd.getText()
                })){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Все поля должны быть заполнены!");
                    return;
                }else if(!DataValidator.isAllNumber(_txtNumberWagon.getText())){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Номер полувагона должен состоять только из цифр!");
                    return;
                }else if(!DataValidator.isAllNumber(_txtSerialNumber.getText())){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Номер полувагона в составе должен быть целым числом и больше 0");
                    return;
                }else if(!DataValidator.isFloatNumber(_txtSd.getText())){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Процент сыпучести вещества должен быть вещественным числом и больше либо равен 0");
                    return;
                }else if(_txtNumberWagon.getText().length() != DataSetting.sizeNumberWagon){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Длина последовательности цифр номера полувагона должна быть равна " +
                            DataSetting.sizeNumberWagon);
                    return;
                }

                for(int i = 0; i < _table.getItems().size(); i++){
                    if((_table.getItems().get(i).getNumberInvoice().equals(_txtNumberInvoice.getText()))
                            && (_table.getItems().get(i).getNumberWagon() == Integer.valueOf(_txtNumberWagon.getText()))){
                        Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Запись с данным номером накладной и номером полувагона уже присутствует в базе данных!");
                        return;
                    }
                }

                //логика добавления в базу данных на сервере:
                DataElementRegisterInsert dataElement = new DataElementRegisterInsert(
                        _txtNumberInvoice.getText(),
                        Integer.valueOf(_txtNumberWagon.getText()),
                        Short.valueOf(_txtSerialNumber.getText()),
                        Float.valueOf(_txtSd.getText())
                );

                try{
                    DataNetwork.updateDataElement("http://localhost:8080/database/register/insert", dataElement);
                }catch (Exception e){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }

                //логика добавления в таблицу:
                _table.getItems().add(new DataRegisterTableView(
                        _txtNumberInvoice.getText(),
                        Integer.valueOf(_txtNumberWagon.getText()),
                        "Не прибыл",
                        Short.valueOf(_txtSerialNumber.getText()),
                        (short) 0,
                        Float.valueOf(_txtSd.getText())
                ));
            }
        });

        _updateData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!DataValidator.requiredValidator(new String[]{
                        _txtNumberInvoice.getText(),
                        _txtNumberWagon.getText(),
                        _txtSerialNumber.getText(),
                        _txtSd.getText()
                })){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Все поля должны быть заполнены!");
                    return;
                }else if((!DataValidator.isAllNumber(_txtNumberWagon.getText()))
                || (Integer.valueOf(_txtNumberWagon.getText()) <= 0)){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Номер полувагона должен состоять только из цифр!");
                    return;
                }else if(!DataValidator.isAllNumber(_txtSerialNumber.getText())
                        || (Integer.valueOf(_txtSerialNumber.getText()) <= 0)){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Номер полувагона в составе должен быть целым числом и больше 0");
                    return;
                }else if(!DataValidator.isFloatNumber(_txtSd.getText())){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Процент сыпучести вещества должен быть вещественным числом и больше либо равен 0");
                    return;
                }else if(_txtNumberWagon.getText().length() != DataSetting.sizeNumberWagon){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Длина последовательности цифр номера полувагона должна быть равна " +
                            DataSetting.sizeNumberWagon);
                    return;
                }

                int index = (-1);
                for(int i = 0; i < _table.getItems().size(); i++){
                    if((_table.getItems().get(i).getNumberInvoice().equals(_txtNumberInvoice.getText()))
                    && (_table.getItems().get(i).getNumberWagon() == Integer.valueOf(_txtNumberWagon.getText()))){
                        index = i;
                        break;
                    }
                }

                if(index < 0){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Записи с данным номером накладной и номером полувагона в базе данных нет!");
                    return;
                }

                //логика обновления записи в базе данных на сервере:
                DataElementRegisterInsert dataElement = new DataElementRegisterInsert(
                        _txtNumberInvoice.getText(),
                        Integer.valueOf(_txtNumberWagon.getText()),
                        Short.valueOf(_txtSerialNumber.getText()),
                        Float.valueOf(_txtSd.getText())
                );

                try{
                    DataNetwork.updateDataElement("http://localhost:8080/database/register/update", dataElement);
                }catch (Exception e){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }

                //логика обновления данных в таблице:
                _table.getItems().set(index, new DataRegisterTableView(
                        _txtNumberInvoice.getText(),
                        Integer.valueOf(_txtNumberWagon.getText()),
                        "Не прибыл",
                        Short.valueOf(_txtSerialNumber.getText()),
                        (short) 0,
                        Float.valueOf(_txtSd.getText())
                ));
            }
        });

        _deleteData.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int index = (-1);
                for(int i = 0; i < _table.getItems().size(); i++){
                    if((_table.getItems().get(i).getNumberInvoice().equals(_txtNumberInvoice.getText()))
                            && (_table.getItems().get(i).getNumberWagon() == Integer.valueOf(_txtNumberWagon.getText()))){
                        index = i;
                        break;
                    }
                }

                if(index < 0){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Записи с данным номером накладной и номером полувагона в базе данных нет!");
                    return;
                }

                //логика удаления записи в базе данных на сервере:
                DataElementRegisterDelete dataElement = new DataElementRegisterDelete(
                        _txtNumberInvoice.getText(),
                        Integer.valueOf(_txtNumberWagon.getText())
                );

                try{
                    DataNetwork.updateDataElement("http://localhost:8080/database/register/delete", dataElement);
                }catch (Exception e){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }

                //логика удаления данных в таблице:
                _table.getItems().remove(index);
                _txtNumberInvoice.setText("");
                _txtNumberWagon.setText("");
                _txtSerialNumber.setText("");
                _txtSd.setText("");
            }
        });
    }

    private void readDataRegister(){
        if(_table == null)
            return;
        _table.getItems().clear();
        DataElementRegister[] elements = null;

        try {
            elements = DataNetwork.getListDataRegister("http://localhost:8080/database/register/get/all");
        } catch (Exception e) {
            Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
            _readMark = false;
            return;
        }

        for(DataElementRegister i : elements){
            _table.getItems().add(new DataRegisterTableView(
                    i.fkNumberInvoice,
                    i.numberWagon,
                    (i.arrivalMark)? "Прибыл" : "Не прибыл",
                    i.serialNumber,
                    i.actualSerialNumber,
                    i.sD
            ));
        }
    }
}
