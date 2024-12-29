// Grafik kullanıcı arayüzü sınıfı - Ana pencere ve bileşenleri yönetir
import javax.swing.*;
import java.awt.*;

public class DepremGUI {
    // Sabit değerler - Pencere ve panel boyutları
    private static final int PENCERE_GENISLIK = 1200;    // Ana pencere genişliği
    private static final int PENCERE_YUKSEKLIK = 800;    // Ana pencere yüksekliği
    private static final int DEPREM_PANEL_GENISLIK = 0;  // Deprem durum paneli genişliği
    private static final int DEPREM_PANEL_YUKSEKLIK = 80; // Deprem durum paneli yüksekliği
    private static final int DEPREM_YAZI_FONT = 80;      // Deprem durum yazısı font büyüklüğü

    // GUI bileşenleri
    private final JFrame anapencere;         // Ana pencere
    private final JLabel depremEtiketi;      // Deprem durumunu gösteren etiket
    private final JComboBox<String> portSecimKutusu; // Seri port seçim kutusu
    private final GrafikPaneli grafikPaneli; // Grafik gösterim paneli
    private final SeriPortYoneticisi portYoneticisi; // Seri port iletişim yöneticisi

    // Yapıcı metod - GUI bileşenlerini oluşturur ve başlatır
    public DepremGUI() {
        this.anapencere = new JFrame("Anlık Sismik Grafik");
        this.depremEtiketi = olusturDepremEtiketi();
        this.portSecimKutusu = new JComboBox<>();
        this.grafikPaneli = new GrafikPaneli();
        this.portYoneticisi = new SeriPortYoneticisi(this);

        baslatGrafikArayuzu();
    }

    // GUI'yi başlatan metod
    private void baslatGrafikArayuzu() {
        anapencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        anapencere.setSize(PENCERE_GENISLIK, PENCERE_YUKSEKLIK);
        anapencere.setLayout(new BorderLayout());

        JPanel ustPanel = olusturUstPanel();
        anapencere.add(ustPanel, BorderLayout.NORTH);
        anapencere.add(grafikPaneli.getChartPanel(), BorderLayout.CENTER);
        anapencere.setVisible(true);
    }

    // Üst panel oluşturma - Port seçimi ve deprem durum panelini içerir
    private JPanel olusturUstPanel() {
        JPanel ustPanel = new JPanel(new BorderLayout());

        // Port kontrol paneli oluşturma
        JPanel portPanel = new JPanel(new FlowLayout());
        JLabel portEtiketi = new JLabel("Port Seç:");
        JButton baglanButonu = new JButton("Bağlan");

        portYoneticisi.doldurPortSecimKutusu(portSecimKutusu);
        baglanButonu.addActionListener(e -> portYoneticisi.portaBaglan());

        portPanel.add(portEtiketi);
        portPanel.add(portSecimKutusu);
        portPanel.add(baglanButonu);

        // Deprem durum paneli oluşturma
        JPanel depremPanel = new JPanel(new BorderLayout());
        depremPanel.add(depremEtiketi, BorderLayout.CENTER);
        depremPanel.setPreferredSize(new Dimension(DEPREM_PANEL_GENISLIK, DEPREM_PANEL_YUKSEKLIK));

        ustPanel.add(portPanel, BorderLayout.NORTH);
        ustPanel.add(depremPanel, BorderLayout.CENTER);

        return ustPanel;
    }

    // Deprem durum etiketi oluşturma
    private JLabel olusturDepremEtiketi() {
        JLabel etiket = new JLabel("DEPREM OLMUYOR", SwingConstants.CENTER);
        etiket.setFont(new Font("Arial", Font.BOLD, DEPREM_YAZI_FONT));
        etiket.setOpaque(true);
        etiket.setBackground(Color.GREEN);
        etiket.setForeground(Color.WHITE);
        return etiket;
    }

    // Deprem durumunu güncelleme metodu
    public void depremDurumuGuncelle(boolean depremVar) {
        depremEtiketi.setText(depremVar ? "DEPREM OLUYOR!" : "DEPREM OLMUYOR");
        depremEtiketi.setBackground(depremVar ? Color.RED : Color.GREEN);
        depremEtiketi.setForeground(Color.WHITE);
    }

    // Getter metodları
    public GrafikPaneli getGrafikPaneli() {
        return grafikPaneli;
    }

    public JComboBox<String> getPortSecimKutusu() {
        return portSecimKutusu;
    }
}