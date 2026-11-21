# Birleştirme Raporu

## Hangi sürümden ne alındı
| Parça | Kaynak |
|---|---|
| Struthiomimus (varlık, hedefler) | v0.8 (oynanış); modeli/renderer'ı v0.7 |
| Ankylosaurus / Pachycephalosaurus / Triceratops / Compsognathus / Stegosaurus / Parasaur / Velociraptor | v0.9 / v1.0 / v1.1 / v1.2 / v1.3 / v1.4 / v1.5 |
| T-Rex / Spinosaurus (+model, doku, spawn) | v1.6 / v1.7 |
| Dimorphodon, Brachiosaurus, Baryonyx | v1.10, v1.11, v1.12 |
| Mosasaurus, Kaçış Teknesi | v1.13 |
| Benzin | v1.14 · Fişek/Jirosfer v1.15 · Keycard/InGen zırhı/Pusu/Sığınak v1.16 |
| Atmosfer (sis, ağaç, çit, konteyner, araç) | v1.9 · Ada: v0.3 |
`UNIFIED` paketi *sadeleştirilmiş* kopyalar içerdiği için (ör. Baryonyx'te ses/balık avı/sürü çağrısı yok, Mosasaurus'ta koku kaybetme yok) esas alınmadı; her sınıfın en eksiksiz sürümü kullanıldı. v0.2 (v0.3'ün içinde), v0.5, v0.6 ve iki "source" zip'i sonraki sürümlerce kapsandığı için ayrıca eklenmedi.

## Derlemeyi bozacak hatalar (düzeltildi)
- `SpecialSpawn` olayı 1.20.1'de yok (8 dosya) → `ModSpawns` + biome modifier JSON'ları. `SpawnPlacements.SpawnType`, `MoveToBlockGoal.isValidTarget(pos)` de geçersizdi.
- `TemptGoal(..., Items.X, ...)` → `Ingredient.of(Items.X)` gerekiyor (8 dosya).
- Eksik import'lar: `Entity` (Pachy, Struthio sürü), `Component` (T-Rex yuvası), `CompoundTag` (100. gün), `Mob`.
- Var olmayan referanslar: `JurassicSurvival.GASOLINE_ITEM/FLARE_ITEM/KEYCARD_ITEM`, `ScorpiosRexEntity`, `EscapeBoatEntity.canRecallWithFlare()`, `PARAUR` yazım hatası.
- `getStepHeight()` 1.20.1'de ezilemez → `setMaxUpStep(1.5F)`.
- `new MosasaurusEntity(null, level)` → `ModEntities.MOSASAURUS.get().create(level)` (null tür çökertirdi).

## Oyunda çökme / bozuk çalışma sebepleri (düzeltildi)
- 13 varlığın **renderer'ı yoktu** (istemci çöker). Hepsine model+doku+renderer eklendi (11 yeni model, `DinoSpecs`; Jirosfer; tekne).
- `Animal.createLivingAttributes()` saldırı-geri-tepme özniteliği içermez → saldıran Struthiomimus/Ankylosaurus çökerdi. `Mob.createMobAttributes()` kullanıldı.
- Mosasaurus yüzemiyordu (yüzme kontrolü/navigasyonu yoktu) ve tekneye hiç vuramıyordu; ikisi de eklendi.
- Parasaur kaçış hedefi yol üretmediği için oyuncu yakınında donuyordu.
- Su içme hedefi sürekli tetikleniyor ve hiç bitmeyebiliyordu (her 3 tickte 2000 blok tarama): rastgele aralık + zaman aşımı eklendi.
- Benzin süresi her sorguda azalıyordu (15 dk saniyeler içinde biterdi): oyun saatine bağlandı.
- Loot filtresi `"lab"` içerdiği için her **slab** kırıldığında benzin düşerdi → yalnız sandık/kasa tablolarına uygulanır.
- Jirosfer seçilemiyor/sürülemiyordu (`isPickable`, kontrol yok) → tamir edilince WASD ile sürülür.
- Keycard kapısı yalnız bir yarıyı açıyordu, olay iki el için tetiklenip "kilitli" mesajı da veriyordu → düzeltildi.
- Fişek tabancası her atışta kendini harcıyordu; artık Fişek Mermisi harcar.
- 100. gün durumu `static boolean` idi (kaydedilmez, dünyalar arası sızar) → dünyaya kayıtlı `SavedData`.
- Ada oluşturucu ~1 milyon sütunu tek tick'te yapıyordu (oyun dakikalarca donar) → tick'lere yayıldı. Yapraklar `persistent` (çürümez), sarmaşıklar yüz atanarak (kaybolmaz), yatak iki parça, sığınak sandığı loot tablosuna bağlı.
- Gece sisi su altı/lav sisini de eziyordu.

## Henüz OLMAYAN özellikler (gönderdiğin dosyalarda kodu yoktu)
- **v1.8 (muhtemelen Scorpios Rex), v0.1, v0.4 gelmedi.** Benzin kuralı `#jurassicsurvival:ignores_gasoline` etiketine bağlandı; Scorpios Rex eklenince otomatik çalışır.
- 80. gün butonuna basınca ne olacağı (yardım çağrısı → gemi gelişi) kodlanmamış; buton sadece ortaya çıkıyor.
- Gece görüş gözlüğü, telsiz, kamp ceketi şimdilik işlevsiz eşyalar.
- Spawn kuralları yalnız Overworld, tüm biyomlarda düşük ağırlıkla (adayla sınırlı değil).
- Yeni modellerin ve eşya simgelerinin görselleri otomatik üretilmiş **yer tutucu**dur; `tools/` içindeki betiklerle veya kendi dokularınla değiştirebilirsin (T-Rex ve Spinosaurus dokuları/modelleri aynen korundu).
