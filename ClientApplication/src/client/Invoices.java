package client;

import client.data.DataElementInvoiceDelete;
import client.data.DataElementInvoice;
import client.data.DataInvoiceTableView;
import client.network.DataNetwork;
import client.validator.DataValidator;
import javafx.application.Application;
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

//**************************************************************
//Программирование главного окна, с которого начинается запуск
//приложения и установка взаимосвязей между всеми окнами
//клиентского приложения
//**************************************************************

public class Invoices extends Application {

    //private Camera _camera = null;

    //взаимосвязи между окнами
    private Register _register = null;
    private Stage _thisStage = null;


    //Элементы управления
    private MenuBar _menuBar = null;
    private TableView<DataInvoiceTableView> _table = null;

    private TextField _txtNumberInvoice = null;
    private TextField _txtNameSupplier = null;
    private TextField _txtTotalWagons = null;
    private TextField _txtDateArrivalTrain = null;
    private TextField _txtDateDepartureTrain = null;

    private Button _addInvoice = null;
    private Button _updateInvoice = null;
    private Button _deleteInvoice = null;

    @Override
    public void start(Stage stage) throws Exception {
        _thisStage = stage;

        Parent root = FXMLLoader.load(getClass().getResource("view/invoices_table_view.fxml"));
        Scene scene = new Scene(root);
        _thisStage = new Stage();
        _thisStage.setScene(scene);
        _thisStage.setTitle("Накладные");

        _register = new Register();
        Register.stageInvoices = _thisStage;

        _txtNumberInvoice = (TextField)scene.lookup("#_txtNumberInvoice");
        _txtNameSupplier = (TextField)scene.lookup("#_txtNameSupplier");
        _txtTotalWagons = (TextField)scene.lookup("#_txtTotalWagons");
        _txtDateArrivalTrain = (TextField)scene.lookup("#_txtDateArrivalTrain");
        _txtDateDepartureTrain = (TextField)scene.lookup("#_txtDateDepartureTrain");

        _addInvoice = (Button)scene.lookup("#_addInvoice");
        _updateInvoice = (Button)scene.lookup("#_updateInvoice");
        _deleteInvoice = (Button)scene.lookup("#_deleteInvoice");

        _menuBar = (MenuBar)scene.lookup("#_menuBar");
        _table = (TableView)scene.lookup("#_tableView");

        _table.getColumns().clear();
        _menuBar.getMenus().clear();

        Menu menu = new Menu("Таблица");
        MenuItem reg = new MenuItem("Таблица регистрации");
        MenuItem wag = new MenuItem("Таблица полувагонов");

        reg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                _register.Show();
            }
        });

        wag.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                //wagon.Show();
            }
        });

        menu.getItems().add(wag);
        menu.getItems().add(reg);
        _menuBar.getMenus().add(0, menu);

        TableColumn<DataInvoiceTableView, String> attrib1 = new TableColumn<DataInvoiceTableView, String>("Номер накладной");
        attrib1.setCellValueFactory(new PropertyValueFactory<DataInvoiceTableView, String>("numberInvoice"));
        _table.getColumns().add(attrib1);

        attrib1 = new TableColumn<DataInvoiceTableView, String>("Название поставщика");
        attrib1.setCellValueFactory(new PropertyValueFactory<DataInvoiceTableView, String>("nameSupplier"));
        _table.getColumns().add(attrib1);

        TableColumn<DataInvoiceTableView, Integer> attrib2 = new TableColumn<DataInvoiceTableView, Integer>("Общее число полувагонов");
        attrib2.setCellValueFactory(new PropertyValueFactory<DataInvoiceTableView, Integer>("totalWagons"));
        _table.getColumns().add(attrib2);

        attrib1 = new TableColumn<DataInvoiceTableView, String>("Дата прибытия состава");
        attrib1.setCellValueFactory(new PropertyValueFactory<DataInvoiceTableView, String>("arrivalTrainDate"));
        _table.getColumns().add(attrib1);

        attrib1 = new TableColumn<DataInvoiceTableView, String>("Дата выезда состава");
        attrib1.setCellValueFactory(new PropertyValueFactory<DataInvoiceTableView, String>("departureTrainDate"));
        _table.getColumns().add(attrib1);

        readDataInvoices();

        _table.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((event.getButton() == MouseButton.PRIMARY) && (_table.getItems().size() > 0)){
                    try{
                        int row = _table.getSelectionModel().getSelectedIndex();
                        DataInvoiceTableView data = _table.getItems().get(row);
                        _txtNumberInvoice.setText(data.getNumberInvoice());
                        _txtNameSupplier.setText(data.getNameSupplier());
                        _txtTotalWagons.setText(String.valueOf(data.getTotalWagons()));
                        _txtDateArrivalTrain.setText(data.getArrivalTrainDate());
                        _txtDateDepartureTrain.setText(data.getDepartureTrainDate());
                    }catch (Exception e){}
                }
            }
        });

        _addInvoice.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!DataValidator.requiredValidator(new String[]{
                        _txtNumberInvoice.getText(),
                        _txtNameSupplier.getText(),
                        _txtTotalWagons.getText(),
                        _txtDateArrivalTrain.getText(),
                        _txtDateDepartureTrain.getText()
                })){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Все поля должны быть заполнены!");
                    return;
                }else if(!DataValidator.isAllNumber(_txtTotalWagons.getText())){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Общее число полувагонов должно быть целым числом и больше 0!");
                    return;
                }else if((!DataValidator.dateTextValidator(_txtDateArrivalTrain.getText())) ||
                        (!DataValidator.dateTextValidator(_txtDateDepartureTrain.getText()))){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Формат даты должен быть в виде гггг-мм-дд");
                    return;
                }else if(!DataValidator.dateTimeValidator(_txtDateDepartureTrain.getText(), _txtDateArrivalTrain.getText())){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Дата приезда состава не может быть раньше даты отправки состава!");
                    return;
                }

                for(int i = 0; i < _table.getItems().size(); i++){
                    if(_table.getItems().get(i).getNumberInvoice().equals(_txtNumberInvoice.getText())){
                        MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Накладная с данным номером уже присутствует в базе данных!");
                        return;
                    }
                }

                //логика добавления в базу данных на сервере:
                DataElementInvoice dataElement = new DataElementInvoice(
                        _txtNumberInvoice.getText(),
                        _txtNameSupplier.getText(),
                        Short.valueOf(_txtTotalWagons.getText()),
                        _txtDateArrivalTrain.getText(),
                        _txtDateDepartureTrain.getText()
                );

                try{
                    DataNetwork.updateDataElement("http://localhost:8080/database/invoices/insert", dataElement);
                }catch (Exception e){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }

                //логика добавления в таблицу:
                _table.getItems().add(new DataInvoiceTableView(
                        _txtNumberInvoice.getText(),
                        _txtNameSupplier.getText(),
                        Short.valueOf(_txtTotalWagons.getText()),
                        _txtDateArrivalTrain.getText(),
                        _txtDateDepartureTrain.getText()
                ));
            }
        });

        _updateInvoice.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!DataValidator.requiredValidator(new String[]{
                        _txtNumberInvoice.getText(),
                        _txtNameSupplier.getText(),
                        _txtTotalWagons.getText(),
                        _txtDateArrivalTrain.getText(),
                        _txtDateDepartureTrain.getText()
                })){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Все поля должны быть заполнены!");
                    return;
                }else if(!DataValidator.isAllNumber(_txtTotalWagons.getText())){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Общее число полувагонов должно быть целым числом и больше 0!");
                    return;
                }else if((!DataValidator.dateTextValidator(_txtDateArrivalTrain.getText())) ||
                        (!DataValidator.dateTextValidator(_txtDateDepartureTrain.getText()))){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Формат даты должен быть в виде гггг-мм-дд");
                    return;
                }else if(!DataValidator.dateTimeValidator(_txtDateDepartureTrain.getText(), _txtDateArrivalTrain.getText())){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Дата приезда состава не может быть раньше даты отправки состава!");
                    return;
                }

                int index = (-1);
                for(int i = 0; i < _table.getItems().size(); i++){
                    if(_table.getItems().get(i).getNumberInvoice().equals(_txtNumberInvoice.getText())){
                        index = i;
                        break;
                    }
                }

                if(index < 0){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Накладная с данным номером не присутствует в базе данных!");
                    return;
                }

                //логика обновления записи в базе данных на сервере:
                DataElementInvoice dataElement = new DataElementInvoice(
                        _txtNumberInvoice.getText(),
                        _txtNameSupplier.getText(),
                        Short.valueOf(_txtTotalWagons.getText()),
                        _txtDateArrivalTrain.getText(),
                        _txtDateDepartureTrain.getText()
                );

                try{
                    DataNetwork.updateDataElement("http://localhost:8080/database/invoices/update", dataElement);
                }catch (Exception e){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }

                //логика обновления данных в таблице:
                _table.getItems().set(index, new DataInvoiceTableView(
                        _txtNumberInvoice.getText(),
                        _txtNameSupplier.getText(),
                        Short.valueOf(_txtTotalWagons.getText()),
                        _txtDateArrivalTrain.getText(),
                        _txtDateDepartureTrain.getText()
                ));
            }
        });

        _deleteInvoice.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int index = (-1);
                for(int i = 0; i < _table.getItems().size(); i++){
                    if(_table.getItems().get(i).getNumberInvoice().equals(_txtNumberInvoice.getText())){
                        index = i;
                        break;
                    }
                }

                if(index < 0){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Накладная с данным номером не присутствует в базе данных!");
                    return;
                }

                //логика удаления записи в базе данных на сервере:
                DataElementInvoiceDelete dataElement = new DataElementInvoiceDelete(
                        _txtNumberInvoice.getText()
                );

                try{
                    DataNetwork.updateDataElement("http://localhost:8080/database/invoices/delete", dataElement);
                }catch (Exception e){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }

                //логика удаления данных в таблице:
                _table.getItems().remove(index);
                _txtNumberInvoice.setText("");
                _txtNameSupplier.setText("");
                _txtTotalWagons.setText("");
                _txtDateDepartureTrain.setText("");
                _txtDateArrivalTrain.setText("");
            }
        });

        _thisStage.show();
    }

    private void readDataInvoices(){
        if(_table == null)
            return;
        DataElementInvoice[] elements = null;

        try {
            elements = DataNetwork.getListDataInvoices("http://localhost:8080/database/invoices/get/all");
        } catch (Exception e) {
            MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
            return;
        }

        for(DataElementInvoice i : elements){
            _table.getItems().add(new DataInvoiceTableView(
                    i.numberInvoice,
                    i.nameSupplier,
                    i.totalWagons,
                    i.arrivalTrainDate,
                    i.departureTrainDate
            ));
        }
    }

    public static void MessageShow(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
