package client;

import client.data.DataElementNumberWagon;
import client.data.DataElementWagon;
import client.network.DataNetwork;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

//**********************************************************************
//Программируемость главного окна, в котором определён пользовательский
//интерфейс для загрузки изображений и начала их распознования
//**********************************************************************

public class Camera extends Application {
    private ComboBox<Integer> _cmbNumberWagon = null;
    private Button _btnSelectedNumber = null;
    private TextField _textResult = null;
    private TextField _textLevelCorrect = null;
    private Button _btnLoadImage = null;
    private Button _btnRecognize = null;
    private ScrollPane _scPane = null;
    private ScrollPane _scPaneImageNumberWagon = null;
    private ImageView _currentImage = null;

    private String _currentFilePath = null;

    private Mat _currentMat = null;
    private Recognizer _recognizer = null;

    private volatile boolean _readMark = true;    //метка о старте/завершении считывания данных с сервера
    private volatile int _timeRead = 10000;       //время через которое будет считаны данные с сервера (обновление данных)
    private Thread threadReadData = null;         //поток для обновления данных в таблице через определённый промежуток времени

    public static void MessageShow(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/camera_view.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Камера");

        _textResult = (TextField) scene.lookup("#_txtResult");
        _btnLoadImage = (Button) scene.lookup("#_btnLoadImage");
        _btnRecognize = (Button) scene.lookup("#_btnRecognize");
        _scPane = (ScrollPane) scene.lookup("#_imagePlace");
        _cmbNumberWagon = (ComboBox) scene.lookup("#_cmbNumberWagon");
        _btnSelectedNumber = (Button) scene.lookup("#_btnSelectedNumber");
        _scPaneImageNumberWagon = (ScrollPane) scene.lookup("#_scPaneImageNumberWagon");
        _textLevelCorrect = (TextField) scene.lookup("#_txtLevelCorrect");

        _cmbNumberWagon.getItems().clear();

        _btnSelectedNumber.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(_cmbNumberWagon.getValue() == null){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка", "Необходимо выбрать номер полувагона!");
                    return;
                }
                _textResult.setText(String.valueOf(_cmbNumberWagon.getValue()));

                insertDataWagon(false);
            }
        });

        ArrayList<String> directories = new ArrayList<>();
        directories.add("C:\\Files\\VALUES\\0");
        directories.add("C:\\Files\\VALUES\\1");
        directories.add("C:\\Files\\VALUES\\2");
        directories.add("C:\\Files\\VALUES\\3");
        directories.add("C:\\Files\\VALUES\\4");
        directories.add("C:\\Files\\VALUES\\5");
        directories.add("C:\\Files\\VALUES\\6");
        directories.add("C:\\Files\\VALUES\\7");
        directories.add("C:\\Files\\VALUES\\8");
        directories.add("C:\\Files\\VALUES\\9");
        _recognizer = new Recognizer(directories);

        _btnLoadImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Чтение данных");
                FileChooser.ExtensionFilter extFilter =
                        new FileChooser.ExtensionFilter("IMAGE files (*.jpg|*.bmp|*.png)", "*.jpg", "*.bmp",
                                "*.png");
                fileChooser.getExtensionFilters().add(extFilter);

                try{
                    File file = fileChooser.showOpenDialog(stage);
                    _currentMat = Imgcodecs.imread(file.getAbsolutePath());
                    _currentFilePath = file.getAbsolutePath();
                    if(_currentMat.empty()){
                        MessageShow(Alert.AlertType.ERROR, "Ошибка", "Не удалось загрузить изображение!");
                        _currentMat = null;
                        return;
                    }

                    Image img = CvUtils.MatToImageFX(_currentMat);
                    _currentImage = new ImageView(img);
                    _scPane.setContent(_currentImage);
                    _scPane.setPannable(true);

                }catch (Exception e){
                    System.out.println(e.getMessage());
                    MessageShow(Alert.AlertType.ERROR, "Ошибка", "Изображение не загружено");
                    _currentFilePath = null;
                    _currentImage = null;
                    _currentMat = null;
                    _scPane.setContent(null);
                    _scPaneImageNumberWagon.setContent(null);
                    return;
                }
            }
        });

        _btnRecognize.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                insertDataWagon(true);
            }
        });

        //обработка события закрытия окна
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                _readMark = false;
            }
        });

        //обработка события открытия окна
        stage.addEventHandler(WindowEvent.WINDOW_SHOWING, new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                _readMark = true;
                //создание потока, который через определённый интервал времени считывает
                //данных из базы данных на сервере (обновление). Для работы приложения
                //необходимо постоянное подключение к серверной части приложения, поскольку
                //только данный модуль имеет доступ к базе данных и предоставляет интерфейс
                //позволяющий другим модулям обращаться к базе данных и взаимодействовать с
                //данными
                threadReadData = new Thread(() -> {
                    while(_readMark){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                readWagonNumbers();
                            }
                        });

                        try {
                            threadReadData.sleep(_timeRead); //ожидание определённый промежуток времени
                        } catch (InterruptedException e) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
                                }
                            });
                        }
                    }
                });

                threadReadData.start();
            }
        });

        stage.show();
    }

    //добавление информации о полувагоне в базу данных
    private void insertDataWagon(boolean recognize){
        if((_currentImage == null) || (_currentFilePath == null)
                || (_currentFilePath.length() == 0)){
            MessageShow(Alert.AlertType.ERROR, "Ошибка", "Необходимо выбрать изображение для распознования!");
            return;
        }

        if((_currentMat == null) || (_currentMat.empty())){
            MessageShow(Alert.AlertType.ERROR, "Ошибка", "Не удалось обработать изображение!");
            return;
        }

        if(recognize){
            try{

                Rect r = _recognizer.getDetectImageRect(_currentMat);
                Mat detect = _recognizer.getDetectImageMat(_currentMat);

                Imgproc.rectangle(_currentMat, new Point(r.x, r.y),
                        new Point(r.x + r.width, r.y + r.height),
                        CvUtils.COLOR_RED, 2);

                Image img = CvUtils.MatToImageFX(_currentMat);
                _currentImage = new ImageView(img);
                _scPane.setContent(_currentImage);
                _scPane.setPannable(true);

                Image imgDetect = CvUtils.MatToImageFX(detect);
                ImageView detectedImage = new ImageView(imgDetect);
                _scPaneImageNumberWagon.setContent(detectedImage);
                _scPaneImageNumberWagon.setPannable(true);

                //распознование номера полувагона
                _textResult.setText(_recognizer.recognizeNumber(detect));
                _textLevelCorrect.setText(String.valueOf(_recognizer.getLevelCorrect()));
                if(_recognizer.getLevelCorrect() >= 50){
                    MessageShow(Alert.AlertType.INFORMATION, "Информация", "Слишком высокий уровень корректного распознования," +
                            " возможны искажения или помехи в процессе обработки изображения!");
                }
                _recognizer.updateLevelCorrect();
            }catch (Exception e){
                MessageShow(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
            }
        }else{
            if((_textResult == null) || (_textResult.getText().length() == 0)){
                MessageShow(Alert.AlertType.ERROR, "Ошибка", "Необходимо определить номер полувагона!");
                return;
            }
        }

        try{
            Integer value = null;
            try{
                value = Integer.valueOf(_textResult.getText());
            }catch (Exception e){
                MessageShow(Alert.AlertType.ERROR, "Ошибка", "Номер полувагона распознан не корректно! Полувагона" +
                        " с данным номером не присутствует в базе данных! Необходимо выбрать номер полувагона из имеющихся" +
                        " номеров!");
                return;
            }

            boolean isRecognize = DataNetwork.isNumberWagon("http://localhost:8080/database/register/numberwagon/is",
                    value);

            if((!isRecognize) && (!recognize)){
                MessageShow(Alert.AlertType.ERROR, "Ошибка", "Полувагона с данным номером не присутствует" +
                        " в базе данных!");
                return;
            }else if((!isRecognize) && (recognize)){
                MessageShow(Alert.AlertType.ERROR, "Ошибка", "Номер полувагона распознан не корректно! Полувагона" +
                        " с данным номером не присутствует в базе данных! Необходимо выбрать номер полувагона из имеющихся" +
                        " номеров!");
                return;
            }
        }catch (Exception e){
            MessageShow(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
            return;
        }

        String filePath = null;
        try{
            filePath = DataNetwork.uploadImage("http://localhost:8080/upload", _currentFilePath);
        }catch (Exception e){
            MessageShow(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
            return;
        }

        Date date = new Date();
        String strDateFormat = "yyyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);

        DataElementWagon data = new DataElementWagon(
                Integer.valueOf(_textResult.getText()),
                formattedDate,
                filePath,
                (recognize)? Double.valueOf(_textLevelCorrect.getText()) : 0.0
        );

        try {
            DataNetwork.updateDataElement("http://localhost:8080/database/wagons/insert", data);
        } catch (Exception e) {
            MessageShow(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
            return;
        }

        _recognizer.updateLevelCorrect();
    }

    //чтение данных номеров всех не прибывших полувагонов
    private void readWagonNumbers(){
        if(_cmbNumberWagon == null)
            return;
        int currentNumber = (_cmbNumberWagon.getValue() != null)? _cmbNumberWagon.getValue() : 0;
        _cmbNumberWagon.getItems().clear();
        DataElementNumberWagon[] elements = null;

        try {
            elements = DataNetwork.getListDataElementNumberWagon("http://localhost:8080/database/register/get/all/numbers");
        } catch (Exception e) {
            MessageShow(Alert.AlertType.ERROR, "Ошибка!", e.getMessage());
            _readMark = false;
            return;
        }

        for(DataElementNumberWagon i : elements){
            _cmbNumberWagon.getItems().add(i.numberWagon);
        }

        if(currentNumber > 0){
            boolean flag = false;
            for(Integer i : _cmbNumberWagon.getItems()){
                if(i.intValue() == currentNumber){
                    flag = true;
                    break;
                }
            }

            if(flag){
                _cmbNumberWagon.setValue(currentNumber);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
