import java.math.BigInteger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Scanner;


public class EllipticCurve {
    public BigInteger a; // Коэффициент a
    public BigInteger b; // Коэффициент b
    public BigInteger p; // Модуль p
    public ECPoint g; // Генерирующая точка g
    
    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p, ECPoint g) {
        this.a = a;
        this.b = b;
        this.p = p;
        this.g = g;
    }

    public EllipticCurve(int a, int b, int p, ECPoint g) {
        this.a = BigInteger.valueOf(a);
        this.b = BigInteger.valueOf(b);
        this.p = BigInteger.valueOf(p);
        this.g = g;
    }

    public ECPoint[] solve() {
        ECPoint[] solutions = new ECPoint[0];
        // y^2 = x^3 + ax + b
        for (BigInteger x = BigInteger.ZERO; x.compareTo(p) < 0; x = x.add(BigInteger.ONE)) {
            for (BigInteger y = BigInteger.ZERO; y.compareTo(p) < 0; y = y.add(BigInteger.ONE)) {
                if (((x.pow(3).add(a.multiply(x)).add(b).subtract(y.pow(2))).mod(p)).equals(BigInteger.ZERO)) {
                    solutions = appendToArray(solutions, new ECPoint(x, y));
                }
            }
        }
        
        return solutions;
    }

    public ECPoint add(ECPoint p1, ECPoint p2) {
        // Если одна из точек является точкой O (находится в бесконечности),
        // то ее суммированием мы получим вторую точку, т.к. точка О является
        // единичным элементом
        if (p1.isPointOfInfinity()) {
            return new ECPoint(p2);
        }
        if (p2.isPointOfInfinity()) {
            return new ECPoint(p1);
        }
        // Если координаты x точек равны, а координаты у симметричны, то
        // результатом будет являться точка в бесконечности (О), т.к.
        // линия, соединяющая p1 и p2, вертикальна и пересекает кривую 
        // в одной точке, которая затем отражается от оси x и дает O. 
        if (p1.x == p2.x && p1.y == p2.y.negate()) {
            return ECPoint.INFINITY;
        }
        
        BigInteger m; // Наклон прямой
        if (p1.x.subtract(p2.x).mod(p).compareTo(BigInteger.ZERO) == 0) {
            if (p1.y.subtract(p2.y).mod(p).compareTo(BigInteger.ZERO) == 0) {
                // Если p1 == p2, то проходящая через них прямая имеет наклон
                // m = (3 * (x_p1)^2 + a) / (2 * y_p1)
                BigInteger nom = p1.x.multiply(p1.x).multiply(BigInteger.valueOf(3)).add(a); // Числитель
                BigInteger den = p1.y.add(p1.y); // Знаменатель
                m = nom.multiply(den.modInverse(p));
            } else {
                return ECPoint.INFINITY;
            }
        } else {
            // Если p1 и q1 не совпадают (x_p1 != x_p2), 
            // то проходящая через них прямая имеет наклон
            // m = (y_p2 - y_p1) / (x_p2 - x_p1)
            BigInteger nom = p2.y.subtract(p1.y); // Числитель
            BigInteger den = p2.x.subtract(p1.x); // Знаменатель
            m = nom.multiply(den.modInverse(p));
        }

        // x_p3 = m^2 - x_p1 - x_p2
        // y_p3 = m * (x_p1 - x_p3) - y_p1
        BigInteger xr = m.multiply(m).subtract(p1.x).subtract(p2.x).mod(p);
        BigInteger yr = m.multiply(p1.x.subtract(xr)).subtract(p1.y).mod(p);
        return new ECPoint(xr, yr);
    }

    public ECPoint multiply(ECPoint p1, BigInteger n) {
        if (p1.isPointOfInfinity()) {
            return ECPoint.INFINITY;
        }
        
        // Алгоритм удвоения сложения
        ECPoint result = ECPoint.INFINITY;
        // Проходимся по двоичному представлению n
        int bitLength = n.bitLength();
        for (int i = bitLength - 1; i >= 0; --i) {
            result = add(result, result); // Удвоение
            if (n.testBit(i)) {
                result = add(result, p1); // Сложение
            }
        }
        
        return result;
    }

    private static ECPoint[] appendToArray(ECPoint[] array, ECPoint element) {
        ECPoint[] newArray = new ECPoint[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }

    public static void main(String[] args) {
        EllipticCurve curve = new EllipticCurve(0, 7, 17, new ECPoint(15, 13));
        ECPoint[] solutions = curve.solve();

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Точки эллиптической кривой");

        for (ECPoint ecPoint : solutions) {
            // System.out.println(ecPoint.toString()); 
            series.add(ecPoint.x.intValue(), ecPoint.y.intValue());
        }

        dataset.addSeries(series);

        // создаем график
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Elliptic Curve", // заголовок графика
                "X", // ось X
                "Y", // ось Y
                dataset, // данные
                PlotOrientation.VERTICAL,
                true, // легенда
                true, // подсказки
                false // URL
        );

        // настройка внешнего вида графика
        XYPlot plot = (XYPlot) chart.getPlot();
        Shape shape = new Ellipse2D.Double(-2.5, -2.5, 5, 5);
        Shape shape1 = new Ellipse2D.Double(-6, -6, 12, 12);
        plot.getRenderer().setSeriesShape(0, shape);
        plot.getRenderer().setSeriesPaint(0, Color.BLUE);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        // создаем панель с графиком и добавляем ее на форму
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("Elliptic Curve");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);


        Scanner in = new Scanner(System.in);

        int choice = -1;
        int isEnd = 1;
        while (isEnd == 1){
            System.out.println("Выберите операцию: 1 - удвоение точки кривой, 2 - сложение двух точек кривой");
            System.out.print("Ваш выбор:  ");
            choice = Integer.valueOf(in.nextLine());

            if (choice == 1){
                // System.out.println("Число точек в эллиптической кривой: " + solutions.length);
                // System.out.println("Выберите точку, введя id точки (от 0 до " + (solutions.length - 1) + ")");
                // int point = Integer.valueOf(in.nextLine());

                System.out.print("Введите значение, на которую умножим точку: ");
                int value = Integer.valueOf(in.nextLine());

                ECPoint g2 = curve.multiply(curve.g, BigInteger.valueOf(value));
                System.out.println("G:" + curve.g.toString());
                System.out.println("G*:" + g2.toString());

                XYSeries multiSeries = new XYSeries("Удвоение точки кривой");
                multiSeries.add(g2.x.intValue(), g2.y.intValue());

                chart.getXYPlot().getDataset();
                dataset.addSeries(multiSeries);
                plot.getRenderer().setSeriesShape(1, shape1);
                plot.getRenderer().setSeriesPaint(1, Color.RED);
                panel.repaint();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                chart.getXYPlot().getDataset();
                dataset.removeSeries(multiSeries);
                panel.repaint();
            }

            if (choice == 2){
                XYSeries sumSeries = new XYSeries("Точки для суммирования");
                XYSeries sumResSeries = new XYSeries("Результат сложения");

                ECPoint p = new ECPoint(2, 10);
                ECPoint q = new ECPoint(8, 14);

                sumSeries.add(p.x.intValue(), p.y.intValue());
                sumSeries.add(q.x.intValue(), q.y.intValue());

                ECPoint r = curve.add(p, q);
                sumResSeries.add(r.x.intValue(), r.y.intValue());

                System.out.println("P:" + p.toString());
                System.out.println("Q:" + q.toString());
                System.out.println("R:" + r.toString());

                chart.getXYPlot().getDataset();
                dataset.addSeries(sumSeries);
                dataset.addSeries(sumResSeries);

                plot.getRenderer().setSeriesShape(1, shape1);
                plot.getRenderer().setSeriesPaint(1, Color.GREEN);

                plot.getRenderer().setSeriesShape(2, shape1);
                plot.getRenderer().setSeriesPaint(2, Color.RED);

                panel.repaint();

                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                chart.getXYPlot().getDataset();
                dataset.removeSeries(sumSeries);
                dataset.removeSeries(sumResSeries);
                panel.repaint();


            }

            System.out.print("Продолжить работу программы? 1 - да, 0 - завершить:  ");
            isEnd = Integer.valueOf(in.nextLine());

        } 

        
        

        

        // try {
        //     Thread.sleep(10000);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        // frame.clearPostOpPoints();

        // ECPoint p = new ECPoint(2, 10);
        // ECPoint q = new ECPoint(8, 14);
        // ECPoint r = curve.add(p, q);
        // System.out.println("P:" + p.toString());
        // System.out.println("Q:" + q.toString());
        // System.out.println("R:" + r.toString());
        // frame.setPostOpPoint(p, "P");
        // frame.setPostOpPoint(q, "Q");
        // frame.setPostOpPoint(r, "R");
    }
}