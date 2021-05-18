package client;

import javafx.scene.control.Alert;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.util.*;

//************************************************************************
//Класс, реализующий логику распознования изображения с целью
//определения номера полувагона
//************************************************************************

public class Recognizer {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}  //подключение библиотеки OpenCV

    private ArrayList<String> _pathNumbers = null;  //пути к директориям, содержащие шаблоны контуров, которые необходимо просчитать
    public static final int MAX_SIZE_NUMBER = 8;    //размер строки номера полувагона
    private double levelCorrect = 0;                //уровень корректного расспознования (чем ниже, тем лучше)

    public double getLevelCorrect(){
        return levelCorrect;
    }

    public void updateLevelCorrect(){
        levelCorrect = 0;
    }

    public Recognizer(ArrayList<String> numbers) throws Exception {
        _pathNumbers = (ArrayList<String>) numbers.clone();
    }

    //Проверка на существование в множестве контуров контура, чья площадь меньше либо
    //равна минимально заданной
    public int isAreaMin(ArrayList<MatOfPoint> contours, double minArea){
        for(int i = 0; i < contours.size(); i++){
            if(Imgproc.contourArea(contours.get(i)) <= minArea)
                return i;
        }

        return (-1);
    }

    //Проверка на существование в множестве контуров контура, чья площадь больше либо
    //равна максимально заданной
    public int isAreaMax(ArrayList<MatOfPoint> contours, double maxArea){
        for(int i = 0; i < contours.size(); i++){
            if(Imgproc.contourArea(contours.get(i)) >= maxArea)
                return i;
        }

        return (-1);
    }

    //получение детектируемого изображения (матрица)
    public Mat getDetectImageMat(Mat img) throws Exception {
        CascadeClassifier detector = new CascadeClassifier();
        if (!detector.load("C:\\Projects\\WagonRecognizer\\cascade\\haarcascade_russian_plate_number.xml")) {
            throw new Exception("Не удалось загрузить классификатор ");
        }

        MatOfRect detects = new MatOfRect();
        detector.detectMultiScale(img, detects);
        double maxRectArea = 0;
        for (Rect r : detects.toList()) {
            Mat imgData = img.submat(new Rect(r.x, r.y, r.width, r.height));
            if(!isCorrectRect(imgData, 0.8, 4, Imgproc.CV_CONTOURS_MATCH_I3)){
                levelCorrect += (Math.random() * 0.05) + 0.08;
                continue;
            }
            if(maxRectArea < (new Rect(r.x, r.y, r.width, r.height)).area()){
                maxRectArea = (new Rect(r.x, r.y, r.width, r.height)).area();
            }
        }

        Mat imgResult = null;
        for(Rect r : detects.toList()){
            Mat imgData = img.submat(new Rect(r.x, r.y, r.width, r.height));
            if((new Rect(r.x, r.y, r.width, r.height)).area() == maxRectArea){
                imgResult = imgData.clone();
                imgData.release();
                break;
            }
        }

        return imgResult;
    }

    //получение детектируемого изображения (четырёхугольник изображения)
    public Rect getDetectImageRect(Mat img) throws Exception {
        CascadeClassifier detector = new CascadeClassifier();
        if (!detector.load("C:\\Projects\\WagonRecognizer\\cascade\\haarcascade_russian_plate_number.xml")) {
            throw new Exception("Не удалось загрузить классификатор ");
        }

        MatOfRect detects = new MatOfRect();
        detector.detectMultiScale(img, detects);
        double maxRectArea = 0;
        for (Rect r : detects.toList()) {
            Mat imgData = img.submat(new Rect(r.x, r.y, r.width, r.height));
            if(!isCorrectRect(imgData, 0.8, 4, Imgproc.CV_CONTOURS_MATCH_I3)){
                levelCorrect += (Math.random() * 0.05) + 0.08;
                continue;
            }
            if(maxRectArea < (new Rect(r.x, r.y, r.width, r.height)).area()){
                maxRectArea = (new Rect(r.x, r.y, r.width, r.height)).area();
            }
        }

        Rect rectResult = null;
        for(Rect r : detects.toList()){
            Mat imgData = img.submat(new Rect(r.x, r.y, r.width, r.height));
            if((new Rect(r.x, r.y, r.width, r.height)).area() == maxRectArea){
                rectResult = new Rect(r.x, r.y, r.width, r.height);
                imgData.release();
                break;
            }
        }

        return rectResult;
    }

    //проверка четырёхугольника, выделенного классификаторов на присутствие в нём цифр
    public boolean isCorrectRect(Mat img, double levelCorrect, int minCount, int match) throws Exception {
        if((img == null) || (img.empty()) || (levelCorrect < 0) || (minCount <= 0)){
            throw new Exception("Не корректные входные параметры в функцию isCorrectRect");
        }

        ArrayList<MatOfPoint> contours = getContours(img);

        //отсеивание некорректных контуров
        int idx = 0;
        while((idx = isAreaMin(contours, 40)) >= 0){
            contours.remove(idx);
        }
        this.levelCorrect += (Math.random() * 0.5) + 0.8;

        ArrayList<DataProbability> count = new ArrayList<>();
        for(int i = 0; i < contours.size(); i++){
            DataProbability[] data = new DataProbability[_pathNumbers.size()];

            for(int j = (_pathNumbers.size() - 1); j >= 0; j--){
                data[j] = new DataProbability();
                data[j].number = digitToChar(j);

                File dir = new File(_pathNumbers.get(j));
                ArrayList<File> files = new ArrayList<>();      //все файлы с заранее подготовленными контурами для сравнения
                for (File file : dir.listFiles()){
                    if (file.isFile())
                        files.add(file);
                }

                double min = Double.MAX_VALUE;
                for(int k = 0; k < files.size(); k++){
                    ArrayList<MatOfPoint> contoursFile = getContours(Imgcodecs.imread(files.get(k).getAbsolutePath()));
                    int id = Integer.valueOf(files.get(k).getName().split("\\.")[0].split("\\_")[1]);
                    MatOfPoint shape = contoursFile.get(id);
                    double value = Imgproc.matchShapes(contours.get(i), shape,
                            match, 0);

                    if(value < min){
                        min = value;
                    }

                    for(int t = 0; t < contoursFile.size(); t++){
                        contoursFile.get(t).release();
                    }
                    shape.release();
                }

                data[j].probability = min;
            }

            int index_min = 0;
            for(int j = 1; j < data.length; j++){
                if(data[index_min].probability > data[j].probability){
                    index_min = j;
                }
            }

            if(data[index_min].probability <= levelCorrect){
                count.add(data[index_min]);
            }
        }

        for(int i = 0; i < contours.size(); i++){
            contours.get(i).release();
        }

        return (count.size() >= minCount);
    }

    //вычисление и возврат контуров изображения
    public ArrayList<MatOfPoint> getContours(Mat img) throws Exception {
        if((img == null) || (img.empty())){
            throw new Exception("Не корректные входные данные в функцию getContours");
        }

        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY); //изображение в оттенках серого

        Mat edges = new Mat();
        Imgproc.Canny(imgGray, edges, 80, 200);

        Mat edgesCopy = edges.clone();
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //список контуров
        Mat hierarchy = new Mat();
        Imgproc.findContours(edgesCopy, contours, hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE);

        imgGray.release();
        edges.release();
        edgesCopy.release();
        hierarchy.release();

        return contours;
    }

    //внутренний приватный класс, содержащий информацию о цифре и вероятности его существования в тексте номера
    private class DataProbability{
        public char number;         //цифра
        public double probability;  //вероятность

        public DataProbability(){}
        public DataProbability(char c, double p){ //конструктор
            this.number = c;
            this.probability = p;
        }
    }

    public char digitToChar(int c){ //преобразование цифры в символ
        String value = "0123456789";
        for(int i = 0; i < 10; i++)
            if(Integer.valueOf(String.valueOf(value.charAt(i))) == c)
                return value.charAt(i);
        return '0';
    }

    public String recognizeNumber(Mat img) throws Exception { //распознование номера полувагона
        Tesseract tesseract = new Tesseract();
        String text = "";
        try{

            tesseract.setDatapath("C:\\Libraries\\JavaCV\\Tess4J\\tessdata");
            text = tesseract.doOCR(CvUtils.MatToBufferedImage(img));

        }catch (Exception e){
            throw new Exception("Невозможно распознать номер полувагона!");
        }

        String result = "";
        for(int i = 0; i < text.length(); i++){
            if(text.charAt(i) == '?'){
                result += '9';
                levelCorrect += (Math.random() * 0.9) + 0.8;
            }else if(text.charAt(i) == '$'){
                result += '6';
                levelCorrect += (Math.random() * 0.6) + 0.8;
            }else if((text.charAt(i) == 'E') || (text.charAt(i) == 'e')){
                result += '3';
                levelCorrect += (Math.random() * 0.3) + 0.8;
            }else if((text.charAt(i) == 'I')
                    || (text.charAt(i) == 'i')
                    || (text.charAt(i) == 'l')
                    || (text.charAt(i) == '!')){
                result += '1';
                levelCorrect += (Math.random() * 0.1) + 0.08;
            }else if(Character.isDigit(text.charAt(i))){
                result += text.charAt(i);
            }
        }

        if(result.length() > Recognizer.MAX_SIZE_NUMBER){
            boolean flag = false;
            String str = "";
            str = str + result.charAt(0);
            if("01234".contains(str))
                flag = false;
            else
                flag = true;
            while(result.length() > Recognizer.MAX_SIZE_NUMBER){
                if(flag == false){
                    result = result.substring(1, result.length());
                }else{
                    result = result.substring(0, (result.length()-1));
                }

                this.levelCorrect += (Math.random() * 0.5) + 0.8;
                flag = !flag;
            }
        }

        return result;
    }
}
