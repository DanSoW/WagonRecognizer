package client;

import client.data.DataElementRegisterInsert;
import client.data.DataElementSetting;
import client.data.DataRegisterTableView;
import client.network.DataNetwork;
import client.setting.DataSetting;
import client.validator.DataValidator;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

//**************************************************************
//Программирование окна, с помощью которого можно изменять
//некоторые настройки серверной части приложения
//**************************************************************

public class Setting {

    //элементы управления
    private MenuBar _menuBar = null;

    private TextField _txtSizeNumberWagon = null;
    private TextField _txtMinSizeNumberInvoice = null;
    private TextField _txtTimeReader = null;

    private Button _btnSaveSetting = null;

    //взаимосвязи между окнами
    private Stage _thisStage = null;            //ссылка на собственный stage
    public static Stage stageInvoices = null;   //stage окна таблицы накладных
    public static Stage stageWagons = null;     //stage окна таблицы полувагонов
    public static Stage stageRegister = null;   //stage окна таблицы соответствия полувагонов накладным

    public void Show(){
        if(this._thisStage == null)
            return;
        this._thisStage.show();
    }

    public void Hide(){
        if(this._thisStage == null)
            return;
        this._thisStage.hide();
    }

    public Stage GetStage(){
        return this._thisStage;
    }

    public Setting() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/setting_view.fxml"));
        Scene scene = new Scene(root);
        _thisStage = new Stage();
        _thisStage.setScene(scene);
        _thisStage.setTitle("Настройки");

        _txtSizeNumberWagon = (TextField)scene.lookup("#_txtSizeNumberWagon");
        _txtMinSizeNumberInvoice = (TextField)scene.lookup("#_txtMinSizeNumberInvoice");
        _txtTimeReader = (TextField)scene.lookup("#_txtTimeReader");
        _btnSaveSetting = (Button)scene.lookup("#_btnSaveSetting");

        _menuBar = (MenuBar)scene.lookup("#_menuBar");
        _menuBar.getMenus().clear();

        Menu menu = new Menu("Таблица");
        MenuItem inv = new MenuItem("Таблица накладных");
        MenuItem wag = new MenuItem("Таблица полувагонов");
        MenuItem reg = new MenuItem("Таблица регистрации");

        inv.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageInvoices.show();
            }
        });

        wag.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageWagons.show();
            }
        });

        reg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                stageRegister.show();
            }
        });

        menu.getItems().add(inv);
        menu.getItems().add(wag);
        menu.getItems().add(reg);
        _menuBar.getMenus().add(0, menu);

        _txtSizeNumberWagon.setText(String.valueOf(DataSetting.sizeNumberWagon));
        _txtTimeReader.setText(String.valueOf(DataSetting.timeRead / 1000));
        _txtMinSizeNumberInvoice.setText(String.valueOf(DataSetting.sizeMinNumberInvoice));

        _btnSaveSetting.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String[] data = new String[]{
                        _txtSizeNumberWagon.getText(),
                        _txtTimeReader.getText(),
                        _txtMinSizeNumberInvoice.getText()
                };

                if(!DataValidator.requiredValidator(data)){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Все поля должны быть заполнены!");
                    return;
                }

                for(String i : data){
                    if(!DataValidator.isAllNumber(i)){
                        Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "В текстовых полях должны быть" +
                                " целочисленные значения!");
                        return;
                    }
                }

                if((_txtSizeNumberWagon.getText().length() >= 2) && (Integer.valueOf(_txtSizeNumberWagon.getText()) > 10)){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Максимальная длина последовательности номера" +
                            " полувагона должна быть не более 10 цифр!");
                    return;
                }

                if((_txtMinSizeNumberInvoice.getText().length() >= 2) && (Integer.valueOf(_txtMinSizeNumberInvoice.getText()) >
                        DataSetting.sizeMaxNumberInvoice)){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Максимальная длина последовательности номера" +
                            " накладной должна быть не более " + DataSetting.sizeMaxNumberInvoice + " символов!");
                    return;
                }

                if(Integer.valueOf(_txtTimeReader.getText()) < 5){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", "Минимальная частота обновления данных - 5 секунд!");
                    return;
                }

                DataSetting.sizeNumberWagon = Short.valueOf(_txtSizeNumberWagon.getText());
                DataSetting.sizeMinNumberInvoice = Short.valueOf(_txtMinSizeNumberInvoice.getText());
                DataSetting.timeRead = (Integer.valueOf(_txtTimeReader.getText()) * 1000);

                try{
                    DataNetwork.updateDataElement("http://localhost:8080/database/settings/sizenumberwagon",
                            new DataElementSetting(Short.valueOf(_txtSizeNumberWagon.getText())));
                    DataNetwork.updateDataElement("http://localhost:8080/database/settings/minsizenumberinvoice",
                            new DataElementSetting(Short.valueOf(_txtMinSizeNumberInvoice.getText())));
                }catch (Exception e){
                    Invoices.MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                    return;
                }
            }
        });
    }
}
