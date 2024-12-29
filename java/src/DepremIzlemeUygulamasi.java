// Ana uygulama sınıfı - Deprem izleme uygulamasını başlatır
import javax.swing.SwingUtilities;

public class DepremIzlemeUygulamasi {
    public static void main(String[] args) {
        // GUI'yi Event Dispatch Thread üzerinde başlat
        SwingUtilities.invokeLater(() -> new DepremGUI());
    }
}