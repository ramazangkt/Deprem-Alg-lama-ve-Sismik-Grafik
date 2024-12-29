

#include <MPU6050.h>
#include <Wire.h>

// MPU6050 sensör nesnesi
MPU6050 MPU;

// Gyro ham değerleri (16-bit)
int16_t GyroX, GyroY, GyroZ;

// Kalibrasyon offset değerleri (32-bit)
int32_t offsetGyroX, offsetGyroY, offsetGyroZ;

// Pin tanımlamaları
const int buzzer = 10;
const int led = 11;

// LED kontrolü için değişkenler
unsigned long sonLEDZamani = 0;
bool ledDurumu = false;
const unsigned long ledYanmaSuresi = 1000; // ms

// Kalibrasyon parametreleri
#define CALIB_ITERATIONS 3     // Kalibrasyon döngü sayısı
#define SAMPLES_PER_ITER 2000  // Her döngüdeki örnek sayısı
#define CALIB_DELAY 2         // Örnekler arası bekleme süresi (ms)

// Hareket algılama eşikleri
#define GYRO_NOISE_THRESHOLD 10  // Düşük gürültü filtresi için eşik
#define GYRO_NOISE_ESIK 3000    // Hareket algılama için eşik

// Hareketli ortalama için parametreler
#define MOVING_AVG_SIZE 32      // Ortalama hesabı için örnek sayısı
int16_t gyroXBuffer[MOVING_AVG_SIZE];
int16_t gyroYBuffer[MOVING_AVG_SIZE];
int16_t gyroZBuffer[MOVING_AVG_SIZE];
int bufferIndex = 0;

void setup() {
  // Haberleşme ayarları
  Serial.begin(115200);  // Yüksek baud rate ile seri haberleşme
  Wire.begin();          // I2C haberleşmeyi başlat
  Wire.setClock(400000); // I2C hızı 400kHz
  
  // MPU6050 başlangıç ayarları
  MPU.initialize();
  
  // Hassasiyet ayarları
  MPU.setFullScaleGyroRange(MPU6050_GYRO_FS_250);  // ±250 derece/saniye
  MPU.setFullScaleAccelRange(MPU6050_ACCEL_FS_2);  // ±2g
  MPU.setDLPFMode(MPU6050_DLPF_BW_5);             // En düşük gürültü filtresi
  
  // Başlangıç offset değerleri
  MPU.setXGyroOffset(0);
  MPU.setYGyroOffset(0);
  MPU.setZGyroOffset(0);
  
  // Hareketli ortalama buffer'ını sıfırla
  for(int i = 0; i < MOVING_AVG_SIZE; i++) {
    gyroXBuffer[i] = gyroYBuffer[i] = gyroZBuffer[i] = 0;
  }
  
  Serial.println(F("Sensör ısınıyor..."));
  delay(3000);  // Sensörün dengelenmesi için bekleme
  
  // Kalibrasyon işlemini başlat
  kalibrasyonYap();
  
  // Çıkış pinlerini ayarla
  pinMode(buzzer, OUTPUT);
  pinMode(led, OUTPUT);
}

// Kalibrasyon fonksiyonu
void kalibrasyonYap() {
  int32_t toplamGyroX, toplamGyroY, toplamGyroZ;
  
  Serial.println(F("Kalibrasyon başlıyor..."));
  Serial.println(F("MPU6050'yi düz bir yüzeye yerleştirin ve hareket ettirmeyin!"));
  delay(2000);

  // Her iterasyon daha hassas kalibrasyon sağlar
  for(int iter = 0; iter < CALIB_ITERATIONS; iter++) {
    toplamGyroX = toplamGyroY = toplamGyroZ = 0;
    
    // İlk kararsız okumaları at
    for(int i = 0; i < 100; i++) {
      MPU.getRotation(&GyroX, &GyroY, &GyroZ);
      delay(2);
    }
    
    // Kalibrasyon için örnek topla
    for(int i = 0; i < SAMPLES_PER_ITER; i++) {
      MPU.getRotation(&GyroX, &GyroY, &GyroZ);
      toplamGyroX += GyroX;
      toplamGyroY += GyroY;
      toplamGyroZ += GyroZ;
      delay(CALIB_DELAY);
    }
    
    // Offset değerlerini güncelle
    if(iter == 0) {
      // İlk iterasyon için direkt ortalama al
      offsetGyroX = toplamGyroX / SAMPLES_PER_ITER;
      offsetGyroY = toplamGyroY / SAMPLES_PER_ITER;
      offsetGyroZ = toplamGyroZ / SAMPLES_PER_ITER;
    } else {
      // Sonraki iterasyonlarda kademeli ortalama al
      offsetGyroX = (offsetGyroX + (toplamGyroX / SAMPLES_PER_ITER)) / 2;
      offsetGyroY = (offsetGyroY + (toplamGyroY / SAMPLES_PER_ITER)) / 2;
      offsetGyroZ = (offsetGyroZ + (toplamGyroZ / SAMPLES_PER_ITER)) / 2;
    }
    delay(1000);
  }
  kontrolKalibrasyon();
}

// Hareketli ortalama hesaplama
int16_t getMovingAverage(int16_t newValue, int16_t* buffer) {
  buffer[bufferIndex] = newValue;
  int32_t sum = 0;
  for(int i = 0; i < MOVING_AVG_SIZE; i++) {
    sum += buffer[i];
  }
  return sum / MOVING_AVG_SIZE;
}

// Düşük gürültü filtresi
int filtreleGyro(int deger) {
    return abs(deger) < GYRO_NOISE_THRESHOLD ? 0 : deger;
}

// Kalibrasyon doğruluk kontrolü
void kontrolKalibrasyon() {
  long gxSum = 0, gySum = 0, gzSum = 0;
  const int KONTROL_ORNEKLERI = 200;
  
  Serial.println(F("\nKalibrasyon doğruluğu kontrol ediliyor..."));
  
  for(int i = 0; i < KONTROL_ORNEKLERI; i++) {
    MPU.getRotation(&GyroX, &GyroY, &GyroZ);
    gxSum += (GyroX - offsetGyroX);
    gySum += (GyroY - offsetGyroY);
    gzSum += (GyroZ - offsetGyroZ);
    delay(2);
  }
  
  // Ortalama sapmaları hesapla
  float gxMean = gxSum / (float)KONTROL_ORNEKLERI;
  float gyMean = gySum / (float)KONTROL_ORNEKLERI;
  float gzMean = gzSum / (float)KONTROL_ORNEKLERI;
  
  // Kalibrasyon kalitesini kontrol et
  bool kalibrasyonIyi = true;
  if(abs(gxMean) > 0.5 || abs(gyMean) > 0.5 || abs(gzMean) > 0.5) {
    kalibrasyonIyi = false;
    Serial.println(F("! Gyro kalibrasyonu optimal değil !"));
  }
  
  if(kalibrasyonIyi) {
    Serial.println(F("\n✓ Kalibrasyon başarılı!"));
  } else {
    Serial.println(F("\n! Kalibrasyon uyarısı !"));
    Serial.println(F("Öneriler:"));
    Serial.println(F("1. Sensörün düz bir yüzeyde olduğundan emin olun"));
    Serial.println(F("2. Kalibrasyon sırasında sensörü hareket ettirmeyin"));
    Serial.println(F("3. Sensörün ısınmasını bekleyin"));
  }
}

void loop() {
  static unsigned long sonVeriZamani = 0;
  unsigned long simdikiZaman = millis();

  // Gyro değerlerini oku
  MPU.getRotation(&GyroX, &GyroY, &GyroZ);

  // Kalibre edilmiş ve filtrelenmiş değerleri hesapla
  int calGyroX = filtreleGyro(GyroX - offsetGyroX);
  int calGyroY = filtreleGyro(GyroY - offsetGyroY);
  int calGyroZ = filtreleGyro(GyroZ - offsetGyroZ);

  // Hareketli ortalama uygula
  int16_t avgGyroX = getMovingAverage(calGyroX, gyroXBuffer);
  int16_t avgGyroY = getMovingAverage(calGyroY, gyroYBuffer);
  int16_t avgGyroZ = getMovingAverage(calGyroZ, gyroZBuffer);

  bufferIndex = (bufferIndex + 1) % MOVING_AVG_SIZE;

  // Hareket algılama
  bool hareketVar = (abs(avgGyroX) > GYRO_NOISE_ESIK || 
                    abs(avgGyroY) > GYRO_NOISE_ESIK || 
                    abs(avgGyroZ) > GYRO_NOISE_ESIK);

  // LED ve buzzer kontrolü
  if(hareketVar) {
    if(!ledDurumu) {
      digitalWrite(led, HIGH);
      ledDurumu = true;
      sonLEDZamani = simdikiZaman;
    }
    tone(buzzer, 1000);  // 1kHz ses
  } else {
    noTone(buzzer);
  }

  // LED zamanlayıcı kontrolü
  if(ledDurumu && (simdikiZaman - sonLEDZamani >= ledYanmaSuresi)) {
    digitalWrite(led, LOW);
    ledDurumu = false;
  }

  // Her 50ms'de bir veri gönder
  if(simdikiZaman - sonVeriZamani >= 50) {
    // Format: GyroX,GyroY,GyroZ,LED_DURUMU
    Serial.print(avgGyroX);
    Serial.print(",");
    Serial.print(avgGyroY);
    Serial.print(",");
    Serial.print(avgGyroZ);
    Serial.print(",");
    Serial.println(digitalRead(led) == HIGH ? "HIGH" : "LOW");
    
    sonVeriZamani = simdikiZaman;
  }
}