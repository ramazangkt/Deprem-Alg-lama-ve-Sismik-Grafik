// Grafik panel sınıfı - Sismik verileri görselleştiren grafik panelini yönetir
import org.jfree.chart.*;
import org.jfree.data.xy.*;
import java.awt.*;

public class GrafikPaneli {
    // Grafik serileri ve panel
    private final XYSeries gyroXSerisi;      // X ekseni gyro verileri
    private final XYSeries gyroYSerisi;      // Y ekseni gyro verileri
    private final XYSeries gyroZSerisi;      // Z ekseni gyro verileri
    private final ChartPanel grafikPanel;    // Grafik gösterim paneli
    private int zaman;                       // Zaman ekseni değeri

    // Sabit değerler
    private static final int MAKSIMUM_VERI = 400;  // Grafikte gösterilecek maksimum veri noktası
    private static final int GRAFIK_PENCERE_YUKSEKLIK = 1150;
    private static final int GRAFIK_PENCERE_GENISLIK = 600;

    // Yapıcı metod
    public GrafikPaneli() {
        this.gyroXSerisi = new XYSeries("GyroX");
        this.gyroYSerisi = new XYSeries("GyroY");
        this.gyroZSerisi = new XYSeries("GyroZ");
        this.zaman = 0;
        this.grafikPanel = olusturGrafikPanel();
    }

    // Grafik paneli oluşturma
    private ChartPanel olusturGrafikPanel() {
        XYSeriesCollection veriSeti = new XYSeriesCollection();
        veriSeti.addSeries(gyroXSerisi);
        veriSeti.addSeries(gyroYSerisi);
        veriSeti.addSeries(gyroZSerisi);

        JFreeChart grafik = ChartFactory.createXYLineChart(
                "Sismik Veriler",
                "Zaman",
                "Hareket",
                veriSeti
        );

        ChartPanel panel = new ChartPanel(grafik);
        panel.setPreferredSize(new Dimension(GRAFIK_PENCERE_YUKSEKLIK, GRAFIK_PENCERE_GENISLIK));
        return panel;
    }

    // Yeni veri ekleme metodu
    public void veriEkle(int gyroX, int gyroY, int gyroZ) {
        gyroXSerisi.add(zaman, gyroX);
        gyroYSerisi.add(zaman, gyroY);
        gyroZSerisi.add(zaman, gyroZ);

        // Maksimum veri sayısı aşıldığında eski verileri sil
        if (gyroXSerisi.getItemCount() > MAKSIMUM_VERI) {
            gyroXSerisi.remove(0);
            gyroYSerisi.remove(0);
            gyroZSerisi.remove(0);
        }

        zaman++;
    }

    public ChartPanel getChartPanel() {
        return grafikPanel;
    }
}