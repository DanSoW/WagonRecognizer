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
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.bytedeco.opencv.global.opencv_calib3d.Rodrigues;

public class Camera extends Application {

    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {
        Application.launch(args);
    }
    public void start(Stage stage) throws Exception {
        VBox root = new VBox(15.0);
        root.setAlignment(Pos.CENTER);
        Button button = new Button("Выполнить");
        button.setOnAction(this::onClickButton);
        root.getChildren().add(button);
        Scene scene = new Scene(root, 400.0, 150.0);
        stage.setTitle("OpenCV " + Core.VERSION);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });
        stage.show();

        Mat img = Imgcodecs.imread("C:\\Files\\image.png");
        if (img.empty()) {
            System.out.println("Не удалось загрузить изображение");
            return;
        }

        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imgGray, imgGray, 100, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Mat edges = new Mat();
        Imgproc.Canny(imgGray, edges, 80, 200);
        CvUtils.showImageFX(edges, "Canny");
        Mat edgesCopy = edges.clone(); // Создаем копию
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(edgesCopy, contours, hierarchy,
                Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_NONE);

        /*boolean flag = true;
        while(flag){
            flag = false;
            int index = (-1);
            for(int i = 0; i < contours.size(); i++){
                if(Imgproc.contourArea(contours.get(i)) == 0){
                    flag = true;
                    index = i;
                    break;
                }
            }

            if(index >= 0)
                contours.remove(index);
        }*/

        System.out.println(contours.size());
        System.out.println(hierarchy.size());
        System.out.println(hierarchy.dump());

        Imgproc.drawContours(img, contours, -1, CvUtils.COLOR_BLUE);
        CvUtils.showImageFX(img, "drawContours");

       for(int i = 0; i < contours.size(); i++){
            double len1 = Imgproc.arcLength(
                    new MatOfPoint2f(contours.get(i).toArray()), false);
            double len2 = Imgproc.arcLength(
                    new MatOfPoint2f(contours.get(i).toArray()), true
            );

            System.out.println(Imgproc.contourArea(contours.get(i)) + " = " + Imgproc.isContourConvex(
                    contours.get(i)
            ));
        }

        img.release(); imgGray.release();
        edges.release(); edgesCopy.release();
        hierarchy.release();
        //CvUtils.showImageFX
    }
    private void onClickButton(ActionEvent e) {
        Mat img = Imgcodecs.imread("C:\\Files\\image.png");
        if (img.empty()) {
            System.out.println("Не удалось загрузить изображение");
            return;
        }
        CvUtils.showImageFX(img, "Текст в заголовке окна");
    }

    /*public int isAreaMin(ArrayList<MatOfPoint> contours, double minArea){
        for(int i = 0; i < contours.size(); i++){
            if(Imgproc.contourArea(contours.get(i)) < minArea)
                return i;
        }

        return (-1);
    }

    public int isAreaMax(ArrayList<MatOfPoint> contours, double maxArea){
        for(int i = 0; i < contours.size(); i++){
            if(Imgproc.contourArea(contours.get(i)) > maxArea)
                return i;
        }

        return (-1);
    }


    private ComboBox<Integer> _cmbNumberWagon = null;
    private Button _btnSelectedNumber = null;
    private TextField _textResult = null;
    private Button _btnLoadImage = null;
    private Button _btnRecognize = null;
    private ScrollPane _scPane = null;
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

        _cmbNumberWagon.getItems().clear();
        _cmbNumberWagon.getItems().add(2);
        _cmbNumberWagon.getItems().add(3);

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
                        new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
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
                    MessageShow(Alert.AlertType.ERROR, "Ошибка", "Изображение не загружено");
                    _currentFilePath = null;
                    _currentImage = null;
                    _currentMat = null;
                    _scPane.setContent(null);
                    _scPane.setPannable(true);
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

        /*Scene scene = new Scene(root, 400.0, 150.0);
        stage.setTitle("Recognizer" + Core.VERSION);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });

        Mat img22 = Imgcodecs.imread("C:\\Files\\VALUES\\7\\70_0.jpg");
        if(img22.empty()){
            System.out.println("Не удалось загрузить изображение!");
            return;
        }

        Mat imgGray22 = new Mat(); //матрица, одержащая изображение
        //в оттенках серого
        Imgproc.cvtColor(img22, imgGray22, Imgproc.COLOR_BGR2GRAY);

        Mat edges22 = new Mat();
        Imgproc.Canny(imgGray22, edges22, 80, 200);
        Mat edgesCopy22 = edges22.clone();
        ArrayList<MatOfPoint> contours22 = new ArrayList<MatOfPoint>(); //список
        //содержащий все найденные контуры
        Mat hierarchy22 = new Mat();
        Imgproc.findContours(edgesCopy22, contours22, hierarchy22,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE);
        System.out.println(contours22.size());

        Mat img = Imgcodecs.imread("C:\\Files\\data1.jpg");
        if(img.empty()){
            System.out.println("Не удалось загрузить изображение!");
            return;
        }

        Mat imgGray = new Mat(); //матрица, одержащая изображение
        //в оттенках серого
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        CvUtils.showImageFX(imgGray, "GRAY");

        Mat edges = new Mat();
        Imgproc.Canny(imgGray, edges, 80, 200);
        CvUtils.showImageFX(edges, "Canny");
        Mat edgesCopy = edges.clone();
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //список
        //содержащий все найденные контуры
        Mat hierarchy = new Mat();
        Imgproc.findContours(edgesCopy, contours, hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE);
        int id = 0;//22
        MatOfPoint shape = new MatOfPoint();
        shape = contours22.get(id);
        //double idArea = Imgproc.contourArea(contours.get(id));

        double min = Double.MAX_VALUE, value = 0;
        int index = (-1);

        int k = 0;
        while((k = isAreaMin(contours, 80)) != (-1))
            contours.remove(k);

        k = 0;
        while((k = isAreaMax(contours, 300)) != (-1))
            contours.remove(k);

        for(int i = 0, j = contours.size(); i < j; i++){
            //double area = Imgproc.contourArea(contours22.get(i));
            if(!((area < (idArea + 100)) && (area > (idArea - 100))))
                continue;*/
            /*Imgproc.drawContours(img, contours, i, CvUtils.COLOR_RED);
            double[] values = new double[]{
                    Imgproc.matchShapes(contours.get(i), shape,
                            Imgproc.CV_CONTOURS_MATCH_I1, 0),
                    Imgproc.matchShapes(contours.get(i), shape,
                            Imgproc.CV_CONTOURS_MATCH_I2, 0),
                    Imgproc.matchShapes(contours.get(i), shape,
                            Imgproc.CV_CONTOURS_MATCH_I3, 0)
            };
            value = (values[0] < values[1])? (values[0] < values[2])? values[0]:
                    ((values[1] < values[0])? ((values[1] < values[2])? values[1]:
                            values[2]) : values[2]) : values[2];

            if(value < min){
                min = value;
                index = i;
            }

            System.out.println("CV_CONTOURS_MATCH_I1: " + i + " " +
                    Imgproc.matchShapes(contours.get(i), shape,
                            Imgproc.CV_CONTOURS_MATCH_I1, 0));
            System.out.println("CV_CONTOURS_MATCH_I2: " + i + " " +
                    Imgproc.matchShapes(contours.get(i), shape,
                            Imgproc.CV_CONTOURS_MATCH_I2, 0));
            System.out.println("CV_CONTOURS_MATCH_I3: " + i + " " +
                    Imgproc.matchShapes(contours.get(i), shape,
                            Imgproc.CV_CONTOURS_MATCH_I3, 0));
        }

        Rect r = Imgproc.boundingRect(contours22.get(id));
        Imgproc.drawContours(img22, contours22, id, CvUtils.COLOR_RED);
        Imgproc.drawContours(img, contours, index, CvUtils.COLOR_BLUE);
        Imgproc.rectangle(img22, new Point(r.x, r.y),
                new Point(r.x + r.width - 1, r.y + r.height - 1),
                CvUtils.COLOR_RED);
        System.out.println("Лучшее совпадение: индекс " + index +
                " значение " + min);
        System.out.println("Площадь контура с index: " + Imgproc.contourArea(contours.get(index)));
        System.out.println("Площадь контура сравнения: " + Imgproc.contourArea(contours22.get(0)));
        CvUtils.showImageFX(img, "Что сравниваем");
        CvUtils.showImageFX(img22, "Результат сравнения");
        img.release(); imgGray.release();
        edges.release(); edgesCopy.release(); shape.release();

        img22.release(); imgGray22.release();
        edges22.release(); edgesCopy22.release();

        Imgproc.drawContours(img, contours, (-1), CvUtils.COLOR_RED);
        CvUtils.showImageFX(img, "drawContours");


        img.release(); imgGray.release();
        edges.release(); edgesCopy.release();
        hierarchy.release();
    }

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
            _textResult.setText(_recognizer.recognizeNumber(_currentMat));
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
                0.0
        );

        try {
            DataNetwork.updateDataElement("http://localhost:8080/database/wagons/insert", data);
        } catch (Exception e) {
            MessageShow(Alert.AlertType.ERROR, "Ошибка", e.getMessage());
            return;
        }
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
    }*/
}
