// Seri port yönetici sınıfı - Arduino ile seri port iletişimini yönetir
import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;
import java.io.InputStream;
import java.util.concurrent.*;

public class SeriPortYoneticisi {
    // Sabit değerler
    private static final int VERI_OKUMA_GECIKMESI = 50;  // Veri okuma aralığı (ms)

    private final DepremGUI gui;                    // GUI referansı
    private SerialPort aktifPort;                   // Aktif seri port
    private ScheduledExecutorService zamanlayiciServis; // Zamanlayıcı servisi

    // Yapıcı metod
    public SeriPortYoneticisi(DepremGUI gui) {
        this.gui = gui;
    }

    // Port seçim kutusunu doldurma
    public void doldurPortSecimKutusu(JComboBox<String> portSecimKutusu) {
        SerialPort[] portlar = SerialPort.getCommPorts();
        for (SerialPort port : portlar) {
            String portBilgisi = String.format("%s (%s)",
                    port.getSystemPortName(),
                    port.getDescriptivePortName());
            portSecimKutusu.addItem(portBilgisi);
        }
    }

    // Seçilen porta bağlanma
    public void portaBaglan() {
        if (aktifPort != null && aktifPort.isOpen()) {
            aktifPort.closePort();
        }

        String secilenPortBilgisi = (String) gui.getPortSecimKutusu().getSelectedItem();
        if (secilenPortBilgisi == null) return;

        String portAdi = secilenPortBilgisi.split(" ")[0];
        aktifPort = SerialPort.getCommPort(portAdi);

        if (aktifPort.openPort()) {
            System.out.println("Port başarıyla açıldı: " + portAdi);
            baslatVeriOkuma();
        } else {
            System.out.println("Port açılamadı: " + portAdi);
        }
    }

    // Veri okuma işlemini başlatma
    private void baslatVeriOkuma() {
        aktifPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100000, 100000);
        InputStream girisAkisi = aktifPort.getInputStream();

        if (zamanlayiciServis != null && !zamanlayiciServis.isShutdown()) {
            zamanlayiciServis.shutdown();
        }

        zamanlayiciServis = Executors.newSingleThreadScheduledExecutor();
        zamanlayiciServis.scheduleAtFixedRate(
                () -> okuVeIsleVeri(girisAkisi),
                0, VERI_OKUMA_GECIKMESI, TimeUnit.MILLISECONDS
        );
    }

    // Veri okuma ve işleme
    private void okuVeIsleVeri(InputStream girisAkisi) {
        try {
            byte[] tampon = new byte[1024];  // Veri okuma tamponu
            int okunanBayt = girisAkisi.read(tampon);

            // Okunan veri varsa işle
            if (okunanBayt > 0) {
                String veri = new String(tampon, 0, okunanBayt).trim();
                if (!veri.isEmpty()) {
                    isleVeriSatirlari(veri);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Gelen veriyi satır satır işleme
    private void isleVeriSatirlari(String veri) {
        for (String satir : veri.split("\\n")) {
            String[] degerler = satir.trim().split(",");
            // Veri formatı: GyroX, GyroY, GyroZ, LED_DURUMU
            if (degerler.length == 4) {
                try {
                    // String değerleri sayısal değerlere dönüştür
                    int gyroX = Integer.parseInt(degerler[0].trim());
                    int gyroY = Integer.parseInt(degerler[1].trim());
                    int gyroZ = Integer.parseInt(degerler[2].trim());
                    String ledDurumu = degerler[3].trim(); // LED durumu

                    // GUI güncellemelerini EDT üzerinde yap
                    SwingUtilities.invokeLater(() -> {
                        // Grafik panelini güncelle
                        gui.getGrafikPaneli().veriEkle(gyroX, gyroY, gyroZ);
                        // Deprem durumunu kontrol et ve göster
                        kontrolDepremDurumu(ledDurumu);
                    });
                } catch (NumberFormatException ex) {
                    System.err.println("Hatalı veri formatı: " + satir);
                }
            }
        }
    }

    // Deprem durumunu kontrol etme ve GUI'yi güncelleme
    private void kontrolDepremDurumu(String ledDurumu) {
        // Arduino'dan gelen LED durumuna göre deprem durumunu güncelle
        boolean depremVar = ledDurumu.equals("HIGH");
        gui.depremDurumuGuncelle(depremVar);
    }
}
