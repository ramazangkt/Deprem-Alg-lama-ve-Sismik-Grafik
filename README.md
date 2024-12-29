# Deprem Algılama ve Sismik Grafik Uygulaması

Bu proje, MPU6050 sensörü kullanan bir Arduino tabanlı deprem algılama ve gerçek zamanlı görselleştirme sistemidir.

## Özellikler

- MPU6050 jiroskop kullanarak gerçek zamanlı sismik aktivite algılama
- Java tabanlı grafiksel kullanıcı arayüzü
- Gerçek zamanlı veri görselleştirme
- Hareket algılama ve uyarı sistemi
- Seri port üzerinden Arduino-Java haberleşmesi

## Gereksinimler

### Donanım
- Arduino (Uno, Nano, vb.)
- MPU6050 Sensör
- LED (Pin 11)
- Buzzer (Pin 10)
- Bağlantı kabloları

### Yazılım
- Arduino IDE
- Java JDK 8 veya üstü
- Virtual Serial COM Port Emulator VSPE

### Kütüphaneler
- jSerialComm-2.10.4.jar
- jfreechart-1.5.4.jar
- MPU6050 Arduino Kütüphanesi

## Kurulum

1. Arduino Kurulumu
   - MPU6050 kütüphanesini Arduino IDE'ye ekleyin
   - `arduino/arduino_deprem/arduino_deprem.ino` dosyasını Arduino'ya yükleyin

2. Java Uygulaması
   - Gerekli JAR dosyalarını projeye ekleyin
   - Java kodunu derleyin ve çalıştırın

## Kullanım

1. Arduino'yu USB port üzerinden bilgisayara bağlayın
2. Java uygulamasını başlatın
3. Doğru COM portunu seçin ve "Bağlan" butonuna tıklayın
4. Gerçek zamanlı sismik verileri grafikte görüntüleyin

## Proje Yapısı

- `arduino/`: Arduino kaynak kodları
- `java/`: Java uygulama kodları
- `lib/`: Gerekli JAR dosyaları
- `docs/`: Dokümantasyon ve görseller

## Katkıda Bulunma

1. Bu repository'yi fork edin
2. Feature branch oluşturun (`git checkout -b feature/yeniOzellik`)
3. Değişikliklerinizi commit edin (`git commit -am 'Yeni özellik eklendi'`)
4. Branch'inizi push edin (`git push origin feature/yeniOzellik`)
5. Pull Request oluşturun

## Lisans

MIT License

## Yazar

[Ramazan Göktürk](https://github.com/ramazangkt)
