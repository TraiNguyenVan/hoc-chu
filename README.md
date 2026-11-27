# Bé học chữ (prototype)

Native Android Java prototype. See [SPEC.md](SPEC.md) for requirements, unfinished work and acceptance gates; see [AUDIO.md](AUDIO.md) for audio status. A successful build does not establish chart correctness, reviewed audio or device usability.

## Build with Gradle

Requirements: JDK 17 or 21, Android SDK platform 35, Build Tools 35.0.0 and Platform-Tools; accept the SDK licences. Set `ANDROID_HOME` to the SDK directory (or set `sdk.dir` in an untracked `local.properties`). The first build needs network access to download Gradle 8.9, Android Gradle Plugin 8.7.3 and their dependencies from the configured official repositories. The wrapper verifies the Gradle distribution SHA-256. From the project root:

```sh
./gradlew clean assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`. This is a debug-signed APK, not a release build. The build keeps MP3 assets uncompressed for `AssetManager.openFd`.

### Tested Docker build on this host

The following command was tested with `eclipse-temurin:21-jdk` and the existing SDK at `/home/yes/.hermes/cache/scratch/android-build-probe/sdk` on the Docker host. Adjust the host mount paths if your SDK or project is elsewhere. Scratch contents can be pruned, so install the SDK again if it is gone. A persistent debug keystore is mounted to avoid generating a new signing identity for every container run:

```sh
mkdir -p /opt/data/cache/scratch/khoaikhoai-gradle/android-home
docker run --rm \
  -v /home/yes/.hermes:/data \
  -v /home/yes/.hermes/cache/scratch/khoaikhoai-gradle/android-home:/root/.android \
  -w /data/projects/khoaikhoai-01 \
  -e ANDROID_HOME=/data/cache/scratch/android-build-probe/sdk \
  -e GRADLE_USER_HOME=/data/cache/scratch/khoaikhoai-gradle/gradle-home \
  -e JAVA_TOOL_OPTIONS=-XX:TieredStopAtLevel=1 \
  eclipse-temurin:21-jdk ./gradlew clean assembleDebug --no-daemon --max-workers=2
```

On this host the first build crashed in the Temurin 21.0.12 C2 compiler; `JAVA_TOOL_OPTIONS` above disables C2 as a local workaround, not a general requirement. SDK XML version and Java 8 source/target deprecation warnings were also observed. Debug keystores generated on different machines or by the manual build are distinct; Android will not update an existing install signed with a different key. Back up the keystore if retaining the same debug signing identity matters. Do not use it for release signing.

## Existing manual build

The existing `build-debug.sh` remains available and was independently built and signature-verified using JDK 21 and the same SDK. From the project root on a host with JDK tools in `PATH` and `ANDROID_HOME` set:

```sh
sh build-debug.sh
```

Default output: `build/manual/app-debug.apk`. Set `BUILD_OUT` to use a different output directory; the script removes existing APK and ZIP files within that directory and generates a local debug keystore there. It uses a different debug signing key from Gradle by default.
