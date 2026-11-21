# Jurassic Survival – Birleştirilmiş Proje (Minecraft 1.20.1 / Forge 47.x)

20 + 6 zip'in (v0.2 → v1.16, UNIFIED, source) tek bir projede birleşmiş hâli.

> **Önemli:** Bu ortamda internet ve Forge yoktu, bu yüzden **derleme yapılamadı** ve hazır `.jar` üretilemedi.
> Kod, derleyicinin sözdizimi denetiminden geçti ve tüm import'lar elle/betikle tarandı; ancak Minecraft API
> uyumu gerçek bir `gradlew build` ile doğrulanmadı. İlk derlemede küçük bir hata çıkarsa hata mesajını bana gönder.

## Derleme (kendi bilgisayarında)
1. **JDK 17** kur.
2. Forge 1.20.1 MDK'yı indir (files.minecraftforge.net → 1.20.1 → *Mdk*). İçinden `gradlew`, `gradlew.bat` ve
   `gradle/` klasörünü bu proje klasörüne kopyala (Gradle wrapper jar'ı burada üretilemedi).
3. Proje klasöründe: `gradlew build`  (ilk seferde Forge'u indirir, 5–15 dk sürebilir)
4. Jar: `build/libs/jurassic-survival-1.16.0.jar`
5. Forge 1.20.1 (47.4.0 veya üstü) kurulu Minecraft'ın `mods` klasörüne at.
   Test için: `gradlew runClient`.

## Oyunda
- Yaratıcı modda **Jurassic Survival** sekmesi: tüm eşyalar + tüm dinozor spawn yumurtaları.
- Komutlar (op gerekir): `/js_island` veya `/jurassic island` (ada, birkaç dakikada tick'lere yayılarak kurulur;
  **okyanus ya da düz/boş dünyada** çalıştırın), `/jurassic bunker|wreck|container|vehicle|fence <n>|tree [h]|boat|gyrosphere`.
- Dinozorlar Overworld'de doğal spawn olur (biome_modifier JSON'ları; ağırlıkları `data/.../forge/biome_modifier/`).

Ayrıntılı birleştirme raporu: `BIRLESTIRME_RAPORU.md`
