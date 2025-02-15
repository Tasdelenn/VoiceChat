# Voice Chat Android App

Bu Android uygulaması, ses kaydı ve paylaşımı yapabilen basit bir mesajlaşma uygulamasıdır.

## Geliştirme Aşamaları

### v0.1 - Temel Yapı
- [x] Android proje yapısı oluşturuldu
- [x] Gradle yapılandırması tamamlandı
- [x] Temel Activity ve layout dosyaları eklendi
- [x] İlk başarılı build alındı

### v0.2 - Ses Kaydı 
- [x] Mikrofon izinleri eklendi
- [x] Kayıt başlat/durdur butonları eklendi
- [x] MediaRecorder implementasyonu yapıldı
- [x] Ses dosyası kaydetme özelliği eklendi
- [x] Kayıt süresi göstergesi (Chronometer) eklendi
- [x] Kayıt durumu bildirimleri eklendi
- [x] Kayıt dosya adı ve konumu gösterimi eklendi
- [x] Kayıt dosyası boyutu gösterimi eklendi

### v0.2.2 - Ses Oynatma Özelliği
- [x] Ses oynatma butonu eklendi
- [x] MediaPlayer implementasyonu yapıldı
- [x] Oynatma durumu göstergesi eklendi
- [x] Kayıt/oynatma geçişleri iyileştirildi

### v0.3.0 - Gelişmiş Ses Özellikleri
- [x] Ses kalitesi ayarları eklendi
  - Örnekleme hızı seçimi
  - Bit derinliği seçimi
- [x] Ses iyileştirme özellikleri eklendi
  - Akustik Eko Engelleme (AEC)
  - Gürültü Bastırma
  - Otomatik Kazanç Kontrolü
  - Gürültü Bastırma Seviyesi ayarı

### v0.3.1 - Ses Görselleştirme
- [x] Gerçek zamanlı ses dalga formu görselleştirmesi eklendi
- [x] 20fps yenileme hızı ile düşük gecikme
- [x] Normalize edilmiş amplitüd gösterimi

### v0.4 - WebRTC Entegrasyonu
- [x] WebRTC kütüphanesi projeye eklendi

### Planlanan Özellikler
- [ ] Ses dosyalarını oynatma
- [ ] Ses dosyalarını paylaşma
- [ ] Kullanıcı arayüzü geliştirmeleri
- [ ] Gerçek zamanlı ses iletimi
- [ ] Sohbet odaları

## Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/Tasdelenn/VoiceChat.git
```

2. Android Studio'da açın

3. Gradle sync yapın

4. Uygulamayı derleyin:
```bash
./gradlew assembleDebug
```

5. Aşağıdaki yoldan oluşturduğunuz apk 'yı android cihazınıza yükleyebilirsiniz.
```bash
src : ...VoiceChat\app\build\outputs\apk\debug\app-debug.apk
```

## Gereksinimler

- Android Studio Arctic Fox veya üzeri
- Android SDK 24+
- Gradle 8.12
- JDK 17

## Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## Lisans

[MIT License](LICENSE)
