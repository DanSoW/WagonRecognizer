package client;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//класс, реализующий логику распознования
public class Recognizer {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    private ArrayList<String> _pathNumbers = null;  //пути к директориям, содержащие шаблоны контуров, которые необходимо просчитать
    public static final int MAX_VALUES = 10;        //число цифр, которые могут быть встречены на номере полувагона
    public static final int MAX_SIZE_NUMBER = 8;    //размер строки номера полувагона

    private boolean checkExistDirectory(){ //проверка на корректность директорий
        if((_pathNumbers == null) || (_pathNumbers.size() != MAX_VALUES))
            return false;
        /*for(String i : _pathNumbers)
            if(!((new File(i)).exists()))
                return false;*/
        return true;
    }

    public Recognizer(ArrayList<String> numbers) throws Exception {
        _pathNumbers = (ArrayList<String>) numbers.clone();
        if(!checkExistDirectory())
            throw new Exception("Ошибка: директории шаблонов цифр для распознавания неисправны!");
    }

    //Логика методов фильтрации контуров:
    public int isAreaMin(ArrayList<MatOfPoint> contours, double minArea){
        for(int i = 0; i < contours.size(); i++){
            if(Imgproc.contourArea(contours.get(i)) <= minArea)
                return i;
        }

        return (-1);
    }

    public int isAreaMax(ArrayList<MatOfPoint> contours, double maxArea){
        for(int i = 0; i < contours.size(); i++){
            if(Imgproc.contourArea(contours.get(i)) >= maxArea)
                return i;
        }

        return (-1);
    }
    //.............................

    private ArrayList<MatOfPoint> getContours(Mat img){ //вычисление и возврат контуров изоюражения
        if(img.empty())
            return null;

        Mat imgGray = new Mat(); //изображение в оттенках серого
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        Mat edges = new Mat();
        Imgproc.Canny(img, edges, 80, 200);

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

    public String recognizeNumber(Mat img){ //распознование номера полувагона
        if(!checkExistDirectory())
            return null;
        ArrayList<MatOfPoint> contoursImg = getContours(img); //контуры исходного изображения
        //они будут подвержены анализу
        String result = "";

        int ind;
        while((ind = isAreaMin(contoursImg, 10)) >= 0)
            contoursImg.remove(ind);

        double area = 0;
        for(int i = 0; i < contoursImg.size(); i++)
            area += Imgproc.contourArea(contoursImg.get(i));
        area /= contoursImg.size();
        System.out.println("Средняя площадь: " + String.valueOf(area));
        /*if(contoursImg.size() != MAX_SIZE_NUMBER){ //первоначальная фильтрация (для ограничения числа контуров)
            int sumIndex = 0;
            for(int i = 0; i < contoursImg.size(); i++)
                sumIndex += i;
            sumIndex /= contoursImg.size();
            double valueMin= Imgproc.contourArea(contoursImg.get(sumIndex));
            double valueMax = Imgproc.contourArea(contoursImg.get(sumIndex)) + 100;
            while(contoursImg.size() != MAX_SIZE_NUMBER){
                if((contoursImg.size() - 1) < MAX_SIZE_NUMBER)
                    break;
                int index = isAreaMin(contoursImg, valueMin);
                if(index >= 0)
                    contoursImg.remove(index);

                if((contoursImg.size() - 1) < MAX_SIZE_NUMBER)
                    break;
                index = isAreaMax(contoursImg, valueMin);
                if(index >= 0)
                    contoursImg.remove(index);
            }
        }*/


        System.out.println("Contours = " + String.valueOf(contoursImg.size()));
        Imgproc.drawContours(img, contoursImg, 3, CvUtils.COLOR_BLUE);
        CvUtils.showImageFX(img, "С чем сравниваем");
        for(int i = 0; i < contoursImg.size(); i++){
            System.out.println("Index: " + String.valueOf(i) + " - " +
                    "Area: " + String.valueOf(Imgproc.contourArea(contoursImg.get(i)))
            + " - Bounding Rect Area: " + String.valueOf(
                    Imgproc.boundingRect(contoursImg.get(i)).area()
            ));
            Rect r = Imgproc.boundingRect(contoursImg.get(i));
            Imgproc.rectangle(img, r, CvUtils.COLOR_RED);
            DataProbability[] data = new DataProbability[_pathNumbers.size()];
            for(int j = 0; j < _pathNumbers.size(); j++){
                data[j] = new DataProbability();
                data[j].number = digitToChar(j);

                File dir = new File(_pathNumbers.get(j));
                ArrayList<File> files = new ArrayList<>();
                for (File file : dir.listFiles() ){
                    if (file.isFile() )
                        files.add(file);
                }

                ArrayList<Double> minValues = new ArrayList<>();
                for(int k = 0; k < files.size(); k++){
                    ArrayList<MatOfPoint> contours = getContours(Imgcodecs.imread(files.get(k).getAbsolutePath()));
                    int id = Integer.valueOf(files.get(k).getName().split("\\.")[0].split("\\_")[1]);
                    MatOfPoint shape = contours.get(id);
                    double[] values = new double[]{
                            Imgproc.matchShapes(contoursImg.get(i), shape,
                                    Imgproc.CV_CONTOURS_MATCH_I1, 0),
                            Imgproc.matchShapes(contoursImg.get(i), shape,
                                    Imgproc.CV_CONTOURS_MATCH_I2, 0),
                            Imgproc.matchShapes(contoursImg.get(i), shape,
                                    Imgproc.CV_CONTOURS_MATCH_I3, 0)
                    };

                    minValues.add(Arrays.stream(values).min().getAsDouble());
                    shape.release();
                }

                data[j].probability = Collections.min(minValues);
            }

            int index_min = 0;
            for(int j = 0; j < data.length; j++){
                if(data[index_min].probability > data[j].probability){
                    index_min = j;
                }
                //System.out.println("Number: " + String.valueOf(data[j].number) + " - Probability: " + String.valueOf(data[j].probability));
            }

            if(data[index_min].probability != Double.MAX_VALUE) {
                result += data[index_min].number;
            }
        }

        CvUtils.showImageFX(img, "Rect");
        return (new StringBuilder(result).reverse()).toString();
        //double idArea = Imgproc.contourArea(contours.get(id));

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
}
