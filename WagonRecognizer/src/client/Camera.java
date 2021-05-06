package client;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;

public class Camera{
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public int isAreaMin(ArrayList<MatOfPoint> contours, double minArea){
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


    private TextField _textResult;
    private Button _btnLoadImage;
    private Button _btnRecognize;
    private ScrollPane _scPane;
    private ImageView _currentImage;
    private Mat _currentMat;
    private Recognizer _recognizer;
    private Stage _thisStage = null;
    public static Stage mainStage = null;
    private MenuBar _menuBar = null;

    public static void MessageShow(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

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

    public Camera() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/camera_view.fxml"));
        Scene scene = new Scene(root);
        _thisStage = new Stage();
        _thisStage.setScene(scene);
        _thisStage.setTitle("Камера");

        _textResult = (TextField) scene.lookup("#_txtResult");
        _btnLoadImage = (Button) scene.lookup("#_btnLoadImage");
        _btnRecognize = (Button) scene.lookup("#_btnRecognize");
        _scPane = (ScrollPane) scene.lookup("#_imagePlace");

        _menuBar = (MenuBar)scene.lookup("#_menuBar");
        _menuBar.getMenus().clear();

        Menu menu = new Menu("Смена модуля");
        MenuItem c = new MenuItem("Пользователь");

        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                _thisStage.hide();
                mainStage.show();
            }
        });

        menu.getItems().add( c);
        _menuBar.getMenus().add(0, menu);

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

        Stage finalStage = _thisStage;
        _btnLoadImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Чтение данных");
                FileChooser.ExtensionFilter extFilter =
                        new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
                fileChooser.getExtensionFilters().add(extFilter);

                try{
                    File file = fileChooser.showOpenDialog(finalStage);
                    _currentMat = Imgcodecs.imread(file.getAbsolutePath());
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
                    MessageShow(Alert.AlertType.ERROR, "Ошибка", "Не удалось обработать изображение");
                    return;
                }
            }
        });

        _btnRecognize.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if((_currentMat == null) || (_currentMat.empty())){
                    MessageShow(Alert.AlertType.ERROR, "Ошибка", "Не удалось обработать изображение");
                    return;
                }

                _textResult.setText(_recognizer.recognizeNumber(_currentMat));
            }
        });

        /*Scene scene = new Scene(root, 400.0, 150.0);
        stage.setTitle("Recognizer" + Core.VERSION);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
        });*/

        /*Mat img22 = Imgcodecs.imread("C:\\Files\\VALUES\\7\\70_0.jpg");
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
        hierarchy.release();*/
    }

    /*private void onClickButton(ActionEvent e){
        Mat img = Imgcodecs.imread("C:\\Files\\data333.jpg");
        if(img.empty()){
            System.out.println("Не удалось загрузить изображение!");
            return;
        }

        CvUtils.showImageFX(img, "Текст в заголовке окна");
    }*/
}
