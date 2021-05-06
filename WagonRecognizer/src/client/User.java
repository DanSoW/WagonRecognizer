package client;

import client.data.DataInvoiceTableView;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import static org.bytedeco.opencv.global.opencv_calib3d.Rodrigues;

public class User extends Application {

    private Camera _camera = null;
    private Stage _thisStage = null;
    private MenuBar _menuBar = null;
    private TableView<DataInvoiceTableView> _table;

    public static void MessageShow(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public void start(Stage stage) throws Exception {
        _thisStage = stage;

        Parent root = FXMLLoader.load(getClass().getResource("view/user_view.fxml"));
        Scene scene = new Scene(root);
        _thisStage = new Stage();
        _thisStage.setScene(scene);
        _thisStage.setTitle("Пользователь");

        _camera = new Camera();
        Camera.mainStage = _thisStage;
        _menuBar = (MenuBar)scene.lookup("#_menuBar");
        _table = (TableView)scene.lookup("#_tableView");

        _table.getColumns().clear();
        _menuBar.getMenus().clear();

        Menu menu = new Menu("Смена модуля");
        MenuItem c = new MenuItem("Камера");

        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                _camera.Show();
            }
        });

        menu.getItems().add( c);
        _menuBar.getMenus().add(0, menu);

        TableColumn<DataInvoiceTableView, String> attrib1 = new TableColumn<DataInvoiceTableView, String>("Номер накладной");
        attrib1.setCellValueFactory(new PropertyValueFactory<DataInvoiceTableView, String>("numberInvoices"));
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

        _thisStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
