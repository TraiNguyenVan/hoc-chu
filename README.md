# Bé học chữ

Ứng dụng Android giúp bé học chữ cái, chữ ghép và các vần tiếng Việt. Chạm vào ô để nghe âm thanh; ứng dụng có thêm phần luyện tập.

## Tải ứng dụng

Tải APK từ mục [Actions](../../actions/workflows/android-apk.yml): chọn lần chạy thành công trên nhánh `main`, rồi tải artifact `hoc-chu-signed-apk`. Artifact hết hạn sau 30 ngày; cần đăng nhập GitHub để tải. Cũng có thể chạy lại workflow bằng nút **Run workflow**. Đây là bản thử nghiệm được ký bằng khóa debug, không phải bản phát hành chính thức.

## Tạo APK

Yêu cầu: JDK 17 hoặc 21, Android SDK Platform 35, Build Tools 35.0.0 và Platform-Tools. Chấp nhận giấy phép SDK và đặt biến `ANDROID_HOME` trỏ đến thư mục SDK, hoặc đặt `sdk.dir` trong `local.properties`.

```sh
./gradlew assembleDebug
```

Lần build đầu cần kết nối mạng để tải Gradle và các thư viện. APK được tạo tại `app/build/outputs/apk/debug/app-debug.apk`.

Cũng có thể dùng `sh build-debug.sh` khi đã cài JDK và Android SDK. APK tạo tại `build/manual/app-debug.apk`; biến `BUILD_OUT` cho phép chọn thư mục khác. Script xóa các file APK và ZIP trong thư mục đầu ra trước khi build.

Các bản build dùng khóa ký khác nhau không thể cài đè lên nhau. Giữ lại khóa debug nếu cần cập nhật bản đã cài; không dùng khóa debug để phát hành chính thức.

## Tình trạng

Đây là bản thử nghiệm. Nội dung bảng và cách phát âm vẫn cần được kiểm tra. Xem [SPEC.md](SPEC.md) và [AUDIO.md](AUDIO.md) để biết chi tiết.
