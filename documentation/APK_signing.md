# Firmar el APK release — nota interna

Pasos para generar el APK RC firmado. Correr desde la raíz del proyecto.

## 1. Crear el keystore (una sola vez)

```bash
keytool -genkeypair -v \
  -keystore qless-release.jks \
  -alias qless \
  -keyalg RSA -keysize 2048 -validity 10000
```

Guardar el `.jks` y las contraseñas fuera del repo. Si se pierden, no se pueden
firmar updates de la app.

## 2. Crear `keystore.properties` en la raíz (NO se commitea)

```properties
storeFile=qless-release.jks
storePassword=...
keyAlias=qless
keyPassword=...
```

## 3. Generar el APK firmado

```bash
./gradlew clean assembleRelease
```

Salida: `app/build/outputs/apk/release/app-release.apk`

## 4. Verificar la firma

```bash
~/Library/Android/sdk/build-tools/36.0.0/apksigner verify --print-certs \
  app/build/outputs/apk/release/app-release.apk
```

El `certificate DN` debe ser el propio (`CN=Qless`), no `CN=Android Debug`.

## Notas

- La firma se cablea en `app/build.gradle.kts` (signingConfig "release" que lee
  `keystore.properties`). Sin ese archivo, el release cae a la clave de debug.
- Requiere el SDK accesible (`sdk.dir` correcto en `local.properties` o `ANDROID_HOME`).
- Usar la versión exacta de `build-tools` (no un comodín `*`, que matchea varias).
