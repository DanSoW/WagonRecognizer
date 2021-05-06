package client;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.Arrays;

public class CvUtils {

    public static final Scalar COLOR_BLACK = colorRGB(0, 0, 0);
    public static final Scalar COLOR_WHITE = colorRGB(255, 255, 255);
    public static final Scalar COLOR_RED = colorRGB(255, 0, 0);
    public static final Scalar COLOR_BLUE = colorRGB(0, 0, 255);
    public static final Scalar COLOR_GREEN = colorRGB(0, 128, 0);
    public static final Scalar COLOR_YELLOW = colorRGB(255, 255, 0);
    public static final Scalar COLOR_GRAY = colorRGB(128, 128, 128);

    public static Scalar colorRGB(double red, double green, double blue){
        return new Scalar(blue, green, red);
    }

    public static Scalar colorRGB(java.awt.Color c){
        return new Scalar(c.getBlue(), c.getGreen(), c.getRed());
    }

    public static Scalar colorRGBA(double red, double green, double blue,
                                   double alpha) {
        return new Scalar(blue, green, red, alpha);
    }

    public static Scalar colorRGBA(java.awt.Color c) {
        return new Scalar(c.getBlue(), c.getGreen(),
                c.getRed(), c.getAlpha());
    }

    public static Scalar colorRGB(javafx.scene.paint.Color c) {
        return new Scalar((double) Math.round(c.getBlue() * 255),
                (double) Math.round(c.getGreen() * 255),
                (double) Math.round(c.getRed() * 255));
    }

    public static Scalar colorRGBA(javafx.scene.paint.Color c) {
        return new Scalar((double) Math.round(c.getBlue() * 255),
                (double) Math.round(c.getGreen() * 255),
                (double) Math.round(c.getRed() * 255),
                (double) Math.round(c.getOpacity() * 255));
    }

    //преобразование матрицы Mat в BufferedImage
    public static BufferedImage MatToBufferedImage(Mat m){
        if((m == null) || (m.empty()))
            return null;
        if(m.depth() == CvType.CV_8U){}
        else if(m.depth() == CvType.CV_16U){
            Mat m_16 = new Mat();
            m.convertTo(m_16, CvType.CV_8U, 255.0 / 65535);
            m = m_16;
        }else if((m.depth() == CvType.CV_32F)){
            Mat m_32 = new Mat();
            m.convertTo(m_32, CvType.CV_8U, 255);
            m = m_32;
        }else
            return null;

        int type = 0;
        if(m.channels() == 1)
            type = BufferedImage.TYPE_BYTE_GRAY;
        else if(m.channels() == 3)
            type = BufferedImage.TYPE_3BYTE_BGR;
        else if(m.channels() == 4)
            type = BufferedImage.TYPE_4BYTE_ABGR;
        else
            return null;

        byte[] buf = new byte[m.channels() * m.cols() * m.rows()];
        m.get(0, 0, buf);
        byte tmp = 0;
        if(m.channels() == 4){
            for(int i = 0; i < buf.length; i += 4){
                tmp = buf[i + 3];
                buf[i + 3] = buf[i + 2];
                buf[i + 2] = buf[i + 1];
                buf[i + 1] = buf[i];
                buf[i] = tmp;
            }
        }

        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        byte[] data =
                ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buf, 0, data, 0, buf.length);
        return image;
    }

    //преобразование BufferedImaage в Mat
    public static Mat BufferedImageToMat(BufferedImage img){
        if(img == null)
            return new Mat();
        int type = 0;
        if(img.getType() == BufferedImage.TYPE_BYTE_GRAY){
            type = CvType.CV_8UC1;
        }else if(img.getType() == BufferedImage.TYPE_3BYTE_BGR){
            type = CvType.CV_8UC3;
        }else if(img.getType() == BufferedImage.TYPE_4BYTE_ABGR){
            type = CvType.CV_8UC4;
        }else
            return new Mat();

        Mat m = new Mat(img.getHeight(), img.getWidth(), type);
        byte[] data =
                ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        if((type == CvType.CV_8UC1) || (type == CvType.CV_8UC3)){
            m.put(0, 0, data);
            return m;
        }

        byte[] buf = Arrays.copyOf(data, data.length);
        byte tmp = 0;
        for(int i = 0; i < buf.length; i += 4){
            tmp = buf[i];
            buf[i + 1] = buf[i + 2];
            buf[i + 2] = buf[i + 3];
            buf[i + 3] = tmp;
        }
        m.put(0, 0, buf);
        return m;
    }

    //преобразование Mat в WritableImage
    public static WritableImage MatToWritableImage(Mat m){
        BufferedImage bim = MatToBufferedImage(m);
        if(bim == null)
            return null;
        else
            return SwingFXUtils.toFXImage(bim, null);
    }

    //Mat в ImageFX
    public static WritableImage MatToImageFX(Mat m){
        if((m == null) || (m.empty()))
            return null;
        if(m.depth() == CvType.CV_8U){}
        else if(m.depth() == CvType.CV_16U){
            Mat m_16 = new Mat();
            m.convertTo(m_16, CvType.CV_8U, 255.0 / 65535);
            m = m_16;
        }else if(m.depth() == CvType.CV_32F){
            Mat m_32 = new Mat();
            m.convertTo(m_32, CvType.CV_8U, 255);
            m = m_32;
        }else
            return null;

        if(m.channels() == 1){
            Mat m_bgra = new Mat();
            Imgproc.cvtColor(m, m_bgra, Imgproc.COLOR_GRAY2BGRA);
            m = m_bgra;
        }else if(m.channels() == 3){
            Mat m_bgra = new Mat();
            Imgproc.cvtColor(m, m_bgra, Imgproc.COLOR_BGR2BGRA);
            m = m_bgra;
        }else if(m.channels() == 4){}
        else
            return null;

        byte[] buf = new byte[m.channels() * m.cols() * m.rows()];
        m.get(0, 0, buf);

        WritableImage wim = new WritableImage(m.cols(), m.rows());
        PixelWriter pw = wim.getPixelWriter();
        pw.setPixels(0, 0, m.cols(), m.rows(),
                WritablePixelFormat.getByteBgraInstance(),
                buf, 0, m.cols() * 4);
        return wim;
    }

    public static Mat ImageFXToMat(javafx.scene.image.Image img){
        if(img == null)
            return new Mat();
        PixelReader pr = img.getPixelReader();
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        byte[] buf = new byte[4 * w * h];
        pr.getPixels(0, 0, w, h, WritablePixelFormat.getByteBgraInstance(),
                buf, 0, w * 4);
        Mat m = new Mat(h, w, CvType.CV_8UC4);
        m.put(0, 0, buf);

        return m;
    }

    //сохранение Mat в бинарный файл
    public static boolean saveMat(Mat m, String path){
        if((m == null) || (m.empty()))
            return false;
        if((path == null) || (path.length() < 5)
        || (!path.endsWith(".mat")))
            return false;

        if(m.depth() == CvType.CV_8U){}
        else if(m.depth() == CvType.CV_16U){
            Mat m_16 = new Mat();
            m.convertTo(m_16, CvType.CV_8U, 255.0 / 65535);
            m = m_16;
        }else if(m.depth() == CvType.CV_32F){
            Mat m_32 = new Mat();
            m.convertTo(m_32, CvType.CV_8U, 255);
            m = m_32;
        }else
            return false;

        if((m.channels() == 2) || (m.channels() > 4))
            return false;

        byte[] buf = new byte[m.channels() * m.cols() * m.rows()];
        m.get(0, 0, buf);

        try{
            OutputStream out = new FileOutputStream(path);
            BufferedOutputStream bout = new BufferedOutputStream(out);
            DataOutputStream dout = new DataOutputStream(bout);

            dout.writeInt(m.rows());
            dout.writeInt(m.cols());

            dout.writeInt(m.channels());
            dout.write(buf);
            dout.flush();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    //загрузка Mat из бинарного файла
    public static Mat loadMat(String path){
        if ((path == null) || (path.length() < 5) || (!path.endsWith(".mat")))
            return new Mat();
        File f = new File(path);
        if (!f.exists() || !f.isFile()) return new Mat();

        try{
            InputStream in = new FileInputStream(path);
            BufferedInputStream bin = new BufferedInputStream(in);
            DataInputStream din = new DataInputStream(bin);

            int rows = din.readInt();
            if(rows < 1)
                return new Mat();
            int cols = din.readInt();
            if(cols < 1)
                return new Mat();
            int ch = din.readInt();
            int type = 0;
            if(ch == 1)
                type = CvType.CV_8UC1;
            else if(ch == 3)
                type = CvType.CV_8UC3;
            else if(ch == 4)
                type = CvType.CV_8UC4;
            else
                return new Mat();

            int size = ch * cols * rows;
            byte[] buf = new byte[size];
            int rsize = din.read(buf);
            if(size != rsize)
                return new Mat();
            Mat m = new Mat(rows, cols, type);
            m.put(0, 0, buf);

            return m;
        }catch (Exception e){
            return new Mat();
        }
    }

    public static void showImageSwing(Mat img, String title) {
        BufferedImage im = MatToBufferedImage(img);
        if (im == null) return;
        int w = 1000, h = 600;
        JFrame window = new JFrame(title);
        window.setSize(w, h);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon imageIcon = new ImageIcon(im);
        JLabel label = new JLabel(imageIcon);
        JScrollPane pane = new JScrollPane(label);
        window.setContentPane(pane);
        if (im.getWidth() < w && im.getHeight() < h) {
            window.pack();
        }
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void showImageFX(Mat img, String title) {
        Image im = MatToImageFX(img);
        Stage window = new Stage();
        ScrollPane sp = new ScrollPane();
        ImageView iv = new ImageView();
        if (im != null) {
            iv.setImage(im);
            if (im.getWidth() < 1000) {
                sp.setPrefWidth(im.getWidth() + 5);
            }
            else sp.setPrefWidth(1000.0);
            if (im.getHeight() < 700) {
                sp.setPrefHeight(im.getHeight() + 5);
            }
            else sp.setPrefHeight(700.0);
        }
        sp.setContent(iv);
        sp.setPannable(true);
        BorderPane box = new BorderPane();
        box.setCenter(sp);
        Scene scene = new Scene(box);
        window.setScene(scene);
        window.setTitle(title);
        window.show();
    }
}
