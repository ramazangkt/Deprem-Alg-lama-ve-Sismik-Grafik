# 🌍 Deprem Algılama ve Sismik Grafik Uygulaması


## 📑 İçindekiler
- [Proje Hakkında](#-proje-hakkında)
- [Donanım Bağlantıları](#-donanım-bağlantıları)
- [I2C Haberleşme](#-i2c-haberleşme)
- [Yazılım Bileşenleri](#-yazılım-bileşenleri)
- [Kurulum](#-kurulum)
- [VSPE Yapılandırması](#-vspe-yapılandırması)
- [Kalibrasyon](#-kalibrasyon)
- [Kullanım](#-kullanım)

## 🎯 Proje Hakkında
Bu proje, MPU6050 sensörü kullanan bir Arduino tabanlı sismik aktivite algılama ve gerçek zamanlı görselleştirme sistemidir. Sistem, sismik hareketleri algılayarak Java tabanlı bir kullanıcı arayüzünde görselleştirir.

## 🔌 Donanım Bağlantıları

### MPU6050 - Arduino Bağlantısı
VCC -> 3.3V
GND -> GND
SCL -> (SCL)
SDA -> (SDA)

### Diğer Bileşenler
LED -> Pin 11
Buzzer -> Pin 10

## 🔄 I2C Haberleşme
MPU6050 sensörü, I2C protokolü üzerinden Arduino ile haberleşir:
- I2C Adresi: 0x68 (varsayılan)
- I2C Hızı: 400kHz (Yüksek Hız Modu)
- Wire kütüphanesi kullanılarak haberleşme sağlanır

## 💻 Yazılım Bileşenleri

### Arduino Yazılımı
- **Özellikler:**
  - Gelişmiş kalibrasyon sistemi
  - Hareketli ortalama filtresi (32 örnek)
  - Gürültü filtreleme (±10 threshold)
  - LED ve Buzzer ile uyarı sistemi
  - 115200 baud rate seri haberleşme
  - Veri formatı: `GyroX,GyroY,GyroZ,LED_DURUMU`

### Java Uygulaması
- **Bileşenler:**
  - DepremIzlemeUygulamasi.java (Ana sınıf)
  - DepremGUI.java (Kullanıcı arayüzü)
  - GrafikPaneli.java (JFreeChart grafiği)
  - SeriPortYoneticisi.java (Port iletişimi)

- **Özellikler:**
  - Gerçek zamanlı grafik gösterimi
  - Port seçimi ve bağlantı kontrolü
  - Görsel deprem uyarı sistemi
  

## 🛠 Kurulum

### Arduino Kurulumu
1. Arduino IDE'yi yükleyin
2. MPU6050 kütüphanesini ekleyin
3. Wire kütüphanesini ekleyin (genelde dahilidir)
4. Kodu Arduino'ya yükleyin

### Java Kurulumu
1. JDK 11 veya üstünü yükleyin
2. Gerekli JAR dosyalarını ekleyin:
   - jSerialComm-2.11.0.jar
   - jfreechart-1.0.19.jar

## 🔗 VSPE Yapılandırması
Virtual Serial Port Emulator (VSPE) kullanarak sanal seri port oluşturma:

1. VSPE'yi başlatın
2. "Pair" seçeneğini seçin
3. İlk port: Arduino'nun bağlı olduğu COM port
4. İkinci port: Java uygulamasının kullanacağı sanal COM port
5. "Create" ile bağlantıyı oluşturun

## 📊 Kalibrasyon
Arduino kodu içinde otomatik kalibrasyon sistemi bulunmaktadır:
- 3 iterasyon x 2000 örnek
- Her iterasyon arası 2ms bekleme
- Offset değerleri hesaplanır ve uygulanır
- Kalibrasyon doğruluk kontrolü yapılır

## 🚀 Kullanım
1. Arduino'yu USB ile bilgisayara bağlayın
2. VSPE'de port eşleştirmesini yapın
3. Java uygulamasını başlatın
4. Port seçimi yapın ve "Bağlan" butonuna tıklayın
5. Gerçek zamanlı sismik verileri izleyin

## 📝 Önemli Notlar
- MPU6050 sensörünü sabit ve düz bir yüzeye monte edin
- Kalibrasyon sırasında sensörü hareket ettirmeyin
- VSPE bağlantısını kontrol edin
- Java uygulamasında doğru portu seçtiğinizden emin olun

