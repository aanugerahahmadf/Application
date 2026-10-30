# PRD — Wedding Flowers Decoration Mobile App (Kotlin Multiplatform)

## 1. Product Vision

Membantu calon pengantin menemukan, membandingkan, menyimpan, memesan, membayar, dan mengelola layanan dekorasi bunga/pernikahan dengan pengalaman visual editorial yang hangat, aman, dan personal.

### 1.1 Prinsip produk

1. **Inspiration first:** gambar dan hasil visual search menjadi entry point utama.
2. **Trust first:** harga, status, expiry, KYC, dan order selalu berasal dari Laravel.
3. **Progressive disclosure:** guest dapat menjelajah; login diminta ketika melakukan action personal/transaksi.
4. **Calm wedding editorial:** ivory, blush, champagne, lavender, charcoal; tidak terasa seperti dashboard utilitarian.
5. **Resilient:** loading, empty, offline, retry, upload progress, unauthorized, forbidden, validation, dan server error selalu didesain.
6. **Accessible:** icon + label, kontras AA, font scale, reduced motion, semantics, dan status tidak hanya warna.
7. **One codebase, two platforms:** seluruh DTO, domain model, repository, use case, ViewModel/state, design token, dan Compose UI utama berada di kode shared/common; platform hanya menyediakan actual implementation.

## 2. Target User dan Jobs-to-be-done

| User | Job |
|---|---|
| Guest explorer | “Saya ingin melihat inspirasi tanpa dipaksa login.” |
| Engaged couple | “Saya ingin menemukan dekorasi yang cocok dengan tema dan budget.” |
| Returning customer | “Saya ingin melanjutkan cart/order/payment tanpa kehilangan state.” |
| KYC customer | “Saya ingin menyelesaikan verifikasi dengan instruksi jelas dan aman.” |
| Customer needing help | “Saya ingin bicara dengan CS dan mengirim foto/attachment.” |

## 3. Information Architecture

### 3.1 Main navigation

Bottom capsule pada root shell menampilkan empat tab aktif saat ini:

- Home | Orders | Cart | Profile

Catalog, Visual Search, Wishlist, Chat, Notification, Search, detail, checkout, payment, scanner, dan legal dibuka sebagai full-screen destination dari content/shortcut. Untuk desain generasi berikutnya, **Home | Catalog | Visual Search | Orders | Profile** boleh dijadikan lima canonical tabs, tetapi jangan menghilangkan Cart dan Chat access; keputusan final harus konsisten di route map.

### 3.2 Navigation rules

- Compose Navigation dengan multiple back stack per tab mempertahankan state tiap tab.
- Pindah tab memakai pola `saveState`, `restoreState`, `launchSingleTop` pada NavHost; berpindah tab tidak membuat ViewModel baru.
- Bottom bar hanya pada `/home`, `/orders`, `/cart`, `/profile`; hidden pada detail, search result, chat, checkout, payment, scanner, WebView, dan call bila menghalangi konten.
- Back gesture/button Android dan iOS dari root tab kembali ke Home; back dari detail kembali ke origin.
- Tab Profile pada bottom capsule memiliki gesture khusus: **long-press membuka account sheet modal di bawah**, **double-tap langsung pindah ke akun berikutnya**. Tab lain tidak memiliki gesture tambahan.
- Deep link harus dapat membuka catalog, order, notification, voucher, review, chat, dan payment dengan auth guard yang benar.
- Onboarding hanya sekali setelah `onboarding_seen`; startup mengarahkan ke landing bila belum selesai.
- Route dan tab destination didefinisikan terpusat; feature screen tidak boleh memanggil navigation langsung dari layer data.

## 4. Visual Direction

### 4.1 Look and feel

**Modern glassy wedding editorial:** full-screen gradient ivory/blush/champagne/lavender, frosted surfaces, translucent cards, subtle white border, low shadow, decorative flower silhouettes, charcoal/warm-gray typography, image-led composition.

### 4.2 Design tokens

| Token | Nilai/aturan |
|---|---|
| Background | gradient ivory → blush/champagne/lavender |
| Glass | white 55–72% alpha, border white 1dp, blur sebagai enhancement |
| Solid content | white/ivory surface untuk text/status/payment penting |
| Radius | card 24dp, bottom capsule 32dp, button capsule 999dp |
| Padding | phone 20dp, tablet 32dp, spacing kelipatan 4 |
| Shadow | halus, low elevation, tidak heavy |
| Text | charcoal primary, warm gray secondary, WCAG AA |
| Typography | **Instrument Sans lokal bundled**: 400 body, 600 heading/CTA/status; tidak boleh memakai Roboto, font default sistem, font Google Fonts runtime, atau font remote |
| Motion | fade/slide pendek, spring ringan, reduce-motion fallback |

Jangan memakai font default sistem, Roboto, remote font, `FontWeight.Bold/W700`, atau hard-coded style per feature. Font wajib dibundel sebagai resource lokal aplikasi pada Android dan iOS, lalu didaftarkan sebagai satu-satunya `FontFamily` aplikasi melalui design system. Aplikasi tidak boleh mengunduh/mengambil font dari internet saat runtime.

Mapping wajib:

- Instrument Sans Regular (Weight 400) → body, caption, helper, input, metadata.
- Instrument Sans SemiBold (Weight 600) → heading, title, CTA, tab aktif, status, harga Tier.

### 4.2.1 Language switcher wajib

Language switcher harus menjadi screen/section nyata di Profile → Settings → Language, bukan hanya konfigurasi tersembunyi.

- Tampilkan bahasa aktif dengan nama lokal dan nama English, misalnya `Bahasa Indonesia — Indonesian` dan `English — English`.
- Minimal dukung `id` dan `en`, tetapi render list dari `supported_locales` server agar dapat diperluas tanpa redesign.
- Sediakan radio/list tile dengan selected check, search bila daftar panjang, preview perubahan, dan tombol Apply/Done bila diperlukan.
- Setelah user memilih bahasa: update LocaleStore/StateFlow locale, persist ke preference storage lokal (DataStore/multiplatform settings), kirim `PUT /user/language` untuk user login, lalu tambahkan header `Accept-Language` pada request berikutnya.
- Guest tetap dapat mengganti bahasa secara lokal; setelah login, preferensi server disinkronkan dengan aturan conflict yang terdokumentasi.
- Loading/error state saat sinkronisasi tidak boleh mengunci seluruh aplikasi; tampilkan retry dan pertahankan locale lokal.
- Tidak boleh terjadi mixed language pada satu screen akibat cache lama. Invalidate/reload translatable content setelah perubahan bahasa.
- Format tanggal, angka, nominal, validation, notification, legal, catalog translated content, accessibility label, dan empty/error state mengikuti locale aktif.
- Locale unsupported kembali ke fallback `id`, bukan menampilkan key translation mentah.

Acceptance: perubahan `id ↔ en` terlihat pada Home, Catalog, Search, Cart, Checkout, Order, Profile, Settings, Chat, Notification, Legal, dan validation/error state; restart aplikasi mempertahankan pilihan.

### 4.2.2 Theme mode wajib: Light, Dark, System

Settings → Appearance harus memiliki tiga pilihan eksplisit:

- Light | Dark | System / Follow device

- Theme mode tersimpan sebagai enum `Light`, `Dark`, `System` pada ThemeStore/StateFlow; jangan hanya menyimpan boolean `dark_mode`.
- Persist pilihan dengan key versioned, contoh `theme_mode`, dan migrasikan nilai boolean lama (`dark_mode`) ke `light/dark` saat app upgrade.
- Theme switcher memiliki preview swatch, icon sun/moon/system, selected state, semantics, dan perubahan animated singkat.
- Restart, account switch, dan deep link tidak boleh mengembalikan theme ke default.
- Gunakan brightness perangkat untuk mode System; update ketika OS berubah.
- Semua screen, modal, bottom sheet, WebView shell, image placeholder, shimmer, input, chip, snackbar, payment, KYC, dan bottom capsule harus memiliki light/dark variant.
- Mode theme tidak boleh mengubah harga, status order, payment deadline, KYC result, atau data server.

Acceptance: test light, dark, system, restart persistence, OS brightness change, large text, high contrast, dan reduced motion.

### 4.2.3 Background glassy blur wajib

Glassy blur adalah identitas visual global yang dikelola di design system, bukan modifier acak di setiap feature.

- Root setiap full-screen screen: gradient background + decorative flower shapes + content layer.
- Top bar, card, filter capsule, modal bottom sheet, notification panel, dan bottom capsule memakai `GlassSurface` terpusat.
- Glass surface: translucent white/dark tint, blur radius terkontrol lewat kemampuan blur Compose (RenderEffect/modifier blur platform), border 1dp translucent, radius 24dp/capsule 32dp, low shadow.
- Blur harus berada di belakang content dan tidak mengaburkan text, price, status, form error, countdown, QR code, atau document scan guide.
- Untuk low-end device, reduced transparency OS, atau performance pressure, gunakan opaque fallback dengan warna solid yang tetap memenuhi kontras.
- Dark theme menggunakan tint graphite/plum, bukan white glass yang menjadi silau; status/pembayaran memakai high-contrast solid surface.
- Blur hanya 2–3 layer depth: gradient/decorative background → glass shell → solid/high-contrast content. Hindari nested blur berlebihan.
- Gunakan repaint boundary, clipping radius, dan blur budget agar scrolling catalog/chat tetap lancar.
- Preview Compose wajib menunjukkan glass pada light/dark, fallback tanpa blur, tablet, font scale besar, dan reduced motion.

Canonical composables: `GlassScaffold`, `GlassSurface`, `GlassCard`, `GlassTopBar`, `GlassModalBottomSheet`, `CapsuleBottomBar`, dan `GlassFallbackSurface`.

### 4.3 Component library

`GlassScaffold`, `GlassSurface`, `GlassCard`, `GlassTopBar`, `CapsuleBottomBar`, `PrimaryCapsuleButton`, `FilterCapsule`, `StatusChip`, `AppAsyncImage`, `LoadingState`, `EmptyState`, `ErrorState`, `GlassModalBottomSheet`, `AppTextField`, `AppSnackbar`, `AppShimmer`, `MediaViewer`, `FaceScoreRing`, `ScanScorePanel`, `PaymentCountdown`.

Semua screen adalah full-screen window Compose yang menghormati `WindowInsets` (status bar, navigation bar, display cutout); jangan membungkus seluruh screen menjadi card kecil dengan area kosong besar.

### 4.4 Typography scale

| Style | Size/line | Weight | Use |
|---|---:|---|---|
| display | 32/40sp | 600 | hero/greeting |
| headline | 26/32sp | 600 | full-screen title |
| titleLarge | 22/28sp | 600 | section |
| titleMedium | 18/24sp | 600 | card/dialog |
| bodyLarge | 16/24sp | 400 | main body |
| bodyMedium | 14/20sp | 400 | metadata |
| labelLarge | 14/20sp | 600 | CTA/tab |
| labelMedium | 12/16sp | 600 | chip/status |
| caption | 12/16sp | 400 | helper |

Seluruh style didefinisikan sekali di design system typography Compose, dengan line height eksplisit; feature tidak boleh menyetel fontSize/fontWeight sendiri.

## 5. Screen Inventory dan Feature Requirements

### 5.1 Splash, onboarding, auth

**Startup/Landing/Onboarding**

- Startup memeriksa config, secure token, locale, cache, Firebase, dan onboarding state.
- Landing menampilkan brand, hero image, value proposition, **Sign In, Sign Up, dan satu-satunya tombol Google/Firebase CTA**, serta guest explore.
- Tombol Google/Firebase **hanya boleh berada di Landing/Auth gateway**, bukan di dalam form Sign In dan bukan di dalam form Sign Up. Sign In dan Sign Up tetap menjadi flow lengkap masing-masing.
- Onboarding menjelaskan visual search, curated catalog, secure checkout, dan chat support; skip/next/done; indikator progress.

**Sign in/sign up — lengkap dan memakai modal**

**Sign In, Sign Up, lupa kata sandi, verifikasi OTP, reset kata sandi, challenge 2FA, dan persetujuan perjanjian semuanya disajikan sebagai modal bottom sheet**, bukan destination terpisah.

**Struktur feature auth KMP**

Feature auth terbagi ke lapisan KMP:

| Lapisan | Isi |
|---|---|
| data | kontrak service API auth, DTO, AuthRepositoryImpl (mapper + implementasi repository), UserModel projection, AppLockService, BiometricAuthService; keystore/keychain bridge |
| domain | AuthRepository contract, model AuthUser/AuthState/OtpChallenge/TwoFactorChallenge, use case login, register, google login, verify OTP, verify 2FA, reset password, logout, logout-for-reauth, switch account |
| presentation | AuthViewModel (StateFlow) sebagai padanan AuthProvider; BiometricViewModel/BiometricSettingsViewModel; modal auth (AuthSheetHost, SignIn sheet, SignUp sheet, ForgotPassword sheet, OtpVerification sheet, ResetPassword sheet, TwoFactorVerify sheet, Agreement modal, PasswordSetupSheet, PinSetupSheet, PinPad); screen AppLock (PIN/Password/Biometric unlock), SecurityQuestion, TwoFactorSetup, CompleteProfile, SwitchAccount; widget AuthHeader, SocialLoginButton |

**Auth gateway / Landing**

- Menampilkan `Welcome Back`, logo/brand, tombol `Sign In`, tombol `Sign Up`, tombol `Continue with Google` melalui Firebase, dan guest/back-to-storefront action.
- Tombol Sign In/Sign Up membuka **sheet auth** modal, bukan destination baru.
- Jika user sudah authenticated, gateway tidak menampilkan form auth dan mengarahkan ke Home/intended destination.

**Auth sheet host**

- Semua sheet auth memakai satu wrapper glass full-height dengan drag handle, safe area, keyboard-aware padding, dan konten scrollable.
- Sheet bersifat non-dismissible (tap luar/drag-down dimatikan); back gesture pada Sign In menutup sheet, back gesture pada Sign Up yang dibuka dari switch mengembalikan ke Sign In.
- Sign In ↔ Sign Up dipindahkan lewat switcher intra-sheet (satu sheet host berganti isi), bukan dua sheet bertumpuk.
- Sheet lain (Forgot Password, OTP, Reset Password, 2FA) membuka di atas sheet sebelumnya dengan urutan pop/push yang rapi; kembali ke Sign In menutup seluruh rantai modal auth.

**Sign In (sheet)**

- Sign In hanya menerima **Email atau Username** melalui satu field `Email / Username`. Nomor HP/WhatsApp, KTP, Passport, SIM, dan NPWP bukan identifier login.
- Password dengan show/hide, validasi, loading, dan pencegahan double-submit.
- Wajib `Ingat Saya` dan persetujuan Terms/Privacy sebelum submit; keduanya menampilkan warning bila belum dicentang; checkbox persetujuan membuka Agreement modal.
- Captcha/security challenge bila diaktifkan server, rate-limit/throttle message, dan pesan login gagal yang tidak membocorkan akun.
- Link `Lupa Kata Sandi?` membuka Forgot Password sheet; link switch ke Sign Up; intended destination; back menutup sheet; offline/error/401/403/429 state.
- Tidak menampilkan Google/Facebook/Apple button di dalam sheet Sign In; social entry hanya dari Landing/Auth gateway.

**Sign Up (sheet)**

- Form lengkap dalam satu sheet scrollable: avatar/foto profil opsional; username; email; password; konfirmasi password; strength indicator; show/hide password.
- Nama: `first_name`, `mid_name`, `last_name`, dan `full_name` otomatis/read-only sesuai aturan backend.
- Tempat dan tanggal lahir (tanggal via date picker).
- Jenis identitas: `KTP`, `Passport`, `SIM`, atau `NPWP`; nomor dokumen divalidasi per tipe (KTP 16 digit, SIM minimal 6, NPWP minimal 15, Passport minimal 6); foto dokumen dan selfie bersama dokumen dengan status terunggah/bisa dihapus; kualitas/retake/upload progress.
- Negara; untuk Indonesia gunakan cascading `province → city/regency → district → village`; untuk negara lain gunakan `province/state → city → district → village`, postal code, address, dan map/location bila tersedia.
- WhatsApp dengan country code dan verifier OTP WhatsApp inline (minimal 10 digit, state verified).
- Gender, agama, status pernikahan, nama ibu, pekerjaan, rentang pendapatan, sumber dana melalui dropdown options server; face scan via kamera dengan state terunggah.
- Wajib `Ingat Saya` dan checkbox persetujuan (Agreement modal wizard) sebelum submit; validasi dekat field; duplicate username/email/document handling; retry, offline, dan server validation state.
- Link switch ke Sign In di bagian bawah.
- Sign Up tidak memiliki Google button. Akun Google dibuat melalui Landing, lalu dirutekan oleh auth continuation (OTP bila `needs_otp`, Complete Profile bila `needs_completion`).

**Alur sheet auth (rantai modal)**

| Alur | Urutan modal |
|---|---|
| Sign In biasa | SignIn sheet → success → Home/intended destination |
| Lupa kata sandi | SignIn sheet → ForgotPassword sheet (email) → OtpVerification sheet (purpose `forgot_password`) → ResetPassword sheet (email + OTP) → SignIn sheet |
| Sign Up | SignUp sheet → OtpVerification sheet (purpose `signup`) → SecurityQuestion → (Ya) TwoFactorSetup → SignIn sheet → 2FA challenge → Home / (Tidak) SignIn sheet → Home |
| Google baru | Landing → OTP sheet (purpose `google_register`) → CompleteProfile → SecurityQuestion → (Ya) TwoFactorSetup → SignIn sheet → 2FA challenge → Home / (Tidak) langsung Home |
| 2FA saat login | SignIn sheet → state `AuthTwoFactorRequired` → TwoFactorVerify sheet menumpuk (SignIn sheet tidak ditutup) → success → sheet 2FA menutup diri → Home |

- ResetPassword sheet menerima email + OTP sebagai bukti; endpoint reset memverifikasi ulang keduanya, jadi sheet tidak dapat dibuka tanpa bukti asli.
- Email pada ForgotPassword/ResetPassword ditampilkan sebagai field disabled (terkunci).
- Setelah Password reset success, tampilkan notifikasi sukses lalu buka kembali SignIn sheet.

**OtpVerification sheet (multi-purpose)**

Satu sheet OTP melayani beberapa purpose dengan judul/isi/tombol yang berbeda:

| Purpose | Judul/subtitle | Field email | Resend | Setelah verifikasi |
|---|---|---|---|---|
| `forgot_password` | `Verifikasi Kode OTP` / masukkan 6 digit kode ke email | disabled field | tanpa tombol resend | ResetPassword sheet |
| `signup` | `Verifikasi Email Anda` | tanpa field (email di subtitle) | tombol resend, cooldown 300 detik | SecurityQuestion |
| `google_register` | `Verifikasi Email Anda` | tanpa field | tombol resend, cooldown 300 detik | CompleteProfile |
| `verify_email` | `Verifikasi Email Anda` | tanpa field | tombol resend, cooldown 300 detik | Home |

- OTP input 6 digit, digits-only, letter-spacing lebar, auto-verify saat mencapai 6 digit.
- Sheet mengirim OTP otomatis saat dibuka (auto-send) pada purpose verifikasi email; auto-send gagal "sudah terkirim" tidak ditampilkan sebagai error.
- Cooldown resend berjalan per detik; tombol resend disabled saat sending/counter aktif.
- Tidak ada dialog success setelah verifikasi: sheet ditutup dulu, baru tujuan berikutnya dibuka agar Navigator/route bersih.
- Error verifikasi memakai pesan server, bukan raw exception.

**TwoFactorVerify sheet**

- Satu sheet untuk tiga metode — authenticator/TOTP, email OTP, kode pemulihan — dipilih lewat `Opsi Lainnya`; hint dan label input berubah per metode.
- Challenge token hanya dibaca dari state `AuthTwoFactorRequired` (memory); tidak pernah ditulis ke secure store.
- Resend code per metode; membedakan kode salah (retry dengan pesan) dan challenge expired (pesan + reset challenge state + kembali Sign In).
- Tidak ada checkbox `Ingat perangkat ini`: mobile selalu meminta 2FA saat Sign In berikutnya.
- Sheet menumpuk di atas SignIn sheet yang masih terbuka; setelah kodenya benar sheet menutup diri dan listener asli melanjutkan ke Home.

**Agreement modal**

- Mode wizard tiga langkah berurutan: Terms of Service → Privacy Policy → Wedding Decoration Policy, dengan tombol Next pada dua langkah pertama dan tombol persetujuan di langkah terakhir.
- Mode tunggal (terms/privacy/wedding) untuk link teks perjanjian; checkbox baru tercentang setelah wizard selesai (callback onAgreed).
- Konten legal dari endpoint dynamic legal pages; loading shimmer, error + retry, label `Terakhir diperbarui` bila server menyediakannya; parse konten JSON sections (heading/body).
- Saat mode wizard, modal tidak dapat ditutup (non-dismissible) sampai selesai.

**SecurityQuestion (screen, satu kali)**

- Dituju setelah OTP `signup` terverifikasi atau setelah Complete Profile; guard: belum authenticated atau sudah pernah menjawab (flag lokal per email) → langsung Home.
- Dua aksi: `Ya, Perkuat Akun Saya` → TwoFactorSetup screen; `Tidak, Nanti Saja` → menandai sudah dijawab, lalu logout-for-reauth (logout sesi tanpa auto-switch ke akun tersimpan) lalu buka SignIn sheet.
- Setelah setup 2FA maupun skip, user dipaksa Sign In ulang agar challenge 2FA benar-benar diuji sekali; sesi lalu di-logout sebelum Sign In sheet dibuka kembali.
- Back gesture tidak menampilkan halaman auth sebelumnya: diarahkan ke Home; halaman ini bukan destination yang dapat dibuka sendiri (deep link/restore di-guard).

**TwoFactorSetup (screen, tujuan akhir Home)**

- Tujuan akhir alur auth, jadi setelah selesai menuju Home, bukan Settings/Security.
- Tidak punya halaman sebelumnya (back = selesai → Home); 2FA yang sudah aktif langsung diarahkan ke Home.
- Isi flow (pilihan metode, QR/secret, verifikasi TOTP, backup/recovery codes, enable/disable) memakai komponen/flow yang sama dengan Two Factor Setup di Settings — satu implementasi, dua tujuan akhir; jangan menyalin logic.

**CompleteProfile (screen)**

- Dibuka setelah OTP `google_register` atau ketika server mengembalikan `needs_completion`; form profil/KYC yang masih kosong dengan inline validation dan server refresh.
- Setelah selesai → SecurityQuestion.

**SwitchAccount, AppLock, dan security foundation**

- **Account sheet modal (bottom sheet dari tab Profile):**
  - Dibuka dengan **long-press** pada tab Profile di bottom capsule; berupa modal bottom sheet kaca dengan drag handle, sudut membulat atas, safe area bawah.
  - Header sheet: icon swap + label `Ganti Akun`/`Switch Account`.
  - Daftar semua akun tersimpan: avatar (image atau fallback icon person), nama lengkap, email; akun aktif ditandai ring border primer pada avatar + check badge, dan tile akun aktif memakai tint ringan; menekan akun aktif tidak melakukan apa-apa.
  - Menekan akun non-aktif: tutup sheet lalu `switchToAccount` — ganti token/session ke akun tersebut, muat ulang `/user`, refresh seluruh state user-scoped, pindah ke Home.
  - Divider, lalu baris `Tambah Akun`: icon person-add pada lingkaran hijau lembut + label; menekan menutup sheet lalu membuka SignIn sheet auth.
  - Sheet hanya tersedia bila minimal ada akun tersimpan atau user login; guest membuka SignIn sheet langsung.
  - Data akun tersimpan (nama, email, avatar, token ref) berasal dari secure/account store lokal; token nilainya tidak pernah dirender di sheet.
- **Double-tap tab Profile:** langsung memanggil `switchToNextAccount` — pindah ke akun berikutnya dalam daftar akun tersimpan (loop ke awal bila di akun terakhir) tanpa membuka sheet; tampilkan snackbar/notifikasi singkat akun mana yang aktif; bila hanya ada satu akun atau guest, double-tap tidak melakukan apa-apa (atau membuka SignIn sheet bila guest).
- SwitchAccount: daftar akun tersimpan, pilih/masuk, tambah akun (kembali ke sheet auth), hapus akun dari perangkat dengan konfirmasi; token per akun terisolasi; cache user-scoped akun lama tidak boleh tampil di akun baru.
- AppLock: screen lock PIN/Password/Biometric dengan PinPad numerik; setup PIN (PinSetupSheet) dan setup password (PasswordSetupSheet) berupa sheet; app lock service + biometric service menyimpan state terenkripsi per platform; lock muncul saat app resume sesuai timeout; bypass saat logout/switch account.
- Setup 2FA dari Settings (security) memakai flow yang sama dengan TwoFactorSetup tetapi tujuan akhir kembali ke Settings/Security.
- Switch account, logout semua device, session expired, trusted devices, login activity, security emails, dan app lock mengikuti state server/security feature yang tersedia.
- Guest action yang membutuhkan auth menampilkan `GuestAuthPrompt`, menyimpan intended destination, lalu kembali ke action setelah auth selesai.

**Auth continuation router (satu logika, dua pintu)**

Semua hasil auth dari Landing dan dari dalam sheet auth dirutekan oleh satu keputusan continuation yang sama:

| Hasil state | Lanjutan |
|---|---|
| `AuthTwoFactorRequired` | buka TwoFactorVerify sheet menumpuk; SignIn sheet tidak ditutup |
| authenticated + `needsOtp` | tutup sheet, buka OtpVerification sheet purpose `google_register`; setelah verifikasi → CompleteProfile |
| authenticated + `needsCompletion` | tutup sheet, tampilkan info ringkas, buka CompleteProfile |
| authenticated (lainnya) | tutup sheet, notifikasi sukses, ke Home/intended destination |

Jangan mengimplementasikan logika continuation dua kali untuk pintu Landing dan pintu sheet; keduanya wajib memakai keputusan yang sama agar perilaku identik.

#### Google Sign-In wajib melalui Firebase Authentication

Google authentication pada mobile harus mengikuti urutan berikut:

1. Inisialisasi Firebase saat bootstrap (guarded, per platform).
2. Ambil Google OAuth credential melalui adapter platform: Android Credential Manager / Google Sign-In SDK, iOS GoogleSignIn/AuthenticationServices.
3. Tukar ke Firebase credential dan lakukan `signInWithCredential` pada Firebase Auth.
4. Ambil Firebase ID token dari Firebase user.
5. Kirim POST `/auth/google` dengan field `firebase_id_token`.
6. Laravel memverifikasi token via Firebase Admin/Kreait.
7. Laravel mengembalikan Sanctum token dan canonical user.

Aturan:

- Google credential harus ditukar menjadi Firebase credential lalu `signInWithCredential`; jangan pernah memakai token Google langsung sebagai token aplikasi.
- Hanya Firebase **ID token** yang dikirim ke Laravel `/auth/google` dengan field `firebase_id_token`; jangan mengirim Google access token sebagai token aplikasi dan jangan memakai Firebase refresh token di API.
- Firebase Auth diakses melalui interface di kode common dengan actual implementation Firebase di platform Android/iOS; konfigurasi platform harus cocok dengan Firebase project, package/bundle ID, SHA certificate, OAuth client ID, dan environment.
- Setelah Laravel sukses, simpan hanya Sanctum token secara encrypted di secure storage (Keystore Android / Keychain iOS); Firebase user dipakai untuk state Google session, tetapi authorization catalog/order/profile selalu memakai Sanctum.
- Jika Firebase sukses tetapi Laravel gagal, tampilkan error/retry dan sign out Firebase bila session belum dapat dipetakan; jangan menganggap user sudah login.
- Jika user membatalkan account picker, kembali ke auth idle tanpa error merah.
- Existing Google user langsung masuk bila profile/policy lengkap; new Google user dapat menerima `needs_otp` untuk verifikasi email dan `needs_completion` untuk profile/KYC.
- Google account dengan email yang sudah terdaftar password harus menampilkan account conflict/linking flow yang aman; jangan menimpa password account atau membuat duplicate user.
- Google user dengan `two_factor_required` harus masuk ke `AuthTwoFactorRequired`; challenge token hanya hidup di memory selama sheet terbuka dan belum boleh ditulis ke secure token store.
- Sign out harus memanggil Laravel logout/revoke lalu sign out Firebase dan Google; switch account harus memuat ulang `/user` untuk token yang dipilih.
- Analytics hanya mencatat event non-PII seperti `google_sign_in_started`, `google_sign_in_success`, `google_sign_in_cancelled`, `google_sign_in_failed`; jangan mengirim email, UID, ID token, access token, atau exception body.

#### Authentication state machine mobile

State auth yang wajib ada:

- `AuthInitial` → `AuthLoading` → `AuthAuthenticated`, `AuthTwoFactorRequired` (temporary challenge, tanpa Sanctum token), `AuthOtpRequired` (verifikasi registrasi email/Google), `AuthNeedsCompletion` (profile/KYC incomplete), `AuthError`, atau `AuthOffline`.
- `AuthAuthenticated` dapat berpindah ke `AuthRefreshingProfile`, `AuthLoggedOut`, dan `AuthSessionExpired` (401).

State requirements:

- `AuthLoading` menonaktifkan submit/social button dan mencegah double request.
- `AuthTwoFactorRequired` membedakan kode salah (retry) dan challenge expired (kembali login).
- `AuthOtpRequired` memiliki purpose, resend cooldown, attempt/expiry message, dan deep-link-safe return.
- `AuthNeedsCompletion` mempertahankan intended destination setelah profile selesai.
- `AuthError` memetakan 401/403/422/429/5xx/network tanpa menampilkan raw exception.
- Startup tidak boleh menghapus session valid hanya karena Firebase tidak tersedia; Firebase diperlukan untuk Google login dan FCM, sedangkan Sanctum tetap dapat memulihkan session API.

**Implementation guard:** direct Google ID/access token tidak pernah boleh menjadi token aplikasi. State holder auth selalu melewati `signInWithCredential` Firebase, mengambil Firebase ID token, dan mengirimkannya sebagai `firebase_id_token` setelah backend Firebase verifier aktif.

### 5.2 Home

Hero greeting personal, search bar, visual search shortcut, category chips, featured catalog carousel/grid, on-sale/promotions, voucher card, latest order/payment reminder, review/social proof, chat shortcut, notification badge.

States: initial loading skeleton, cached content + refresh, empty section, partial failure per section, offline cached mode, retry. Hero image harus memiliki placeholder dan fixed aspect ratio.

### 5.3 Catalog dan catalog detail

**Catalog grid screen**

- Search/filter category, price range, capacity/theme/sale, sort, pagination/infinite scroll.
- Card menampilkan image, title, category, rating, jumlah/availability Tier, sale badge bila ada, dan wishlist toggle. Card Catalog **tidak menampilkan harga langsung**.
- Filter memakai modal bottom sheet; selected filters berupa chips; clear all; loading tidak menghilangkan layout.

**Catalog detail screen**

- Full-screen image gallery/media viewer, title, description, category, rating summary, review preview, related items.
- Tier selector menampilkan nama, description, price, sale price bila ada, capacity/metadata, features, stock, dan availability. **Harga hanya berasal dari Tier/Package server.**
- Catalog Detail tidak boleh memiliki CTA “buy catalog at default price”; user wajib memilih Package/Tier terlebih dahulu. Package/Tier adalah selector data di halaman Catalog Detail, bukan halaman navigasi tersendiri.
- CTA wishlist, add to cart, buy/book now, chat CS, share.
- Guest boleh lihat detail; action mutasi meminta login.
- Loading/error/404/out-of-stock/price changed state dengan CTA retry/back.

### 5.4 Text search dan Visual Search/CBIR

**Search screen / results screen**

- Search field dengan debounce, recent query, suggestions, popular search, filters, result count.
- Empty search dan no result menyarankan category/visual search.
- Pagination, pull-to-refresh, image cache, error retry.

**CBIR flow**

Urutan: Visual Search → penjelasan permission → camera/gallery → crop/scan preview → client quality hint → upload progress → result similarity list → detail/wishlist/cart.

- Support camera and gallery, MIME/size validation, JPEG compression, cancel/retry.
- Client boleh memberi arahan blur/brightness/aspect; hasil similarity dan ranking dari Laravel/AI Core.
- Result menampilkan similarity percentage sebagai text + visual indicator, bukan warna saja.
- Simpan/delete/clear history hanya bila endpoint tersedia; jangan membuat row lokal seolah-olah canonical.
- Video tidak diperlakukan sebagai image tanpa aturan server.
- Camera permission diminta melalui expect/actual permission bridge hanya saat scanner dibuka.

### 5.5 Cart, wishlist, voucher

**Cart screen**

- Item list dengan image, catalog, selected Package/Tier, quantity/selection, Tier price, remove, summary, voucher, checkout CTA. Cart item yang dapat dibeli wajib memiliki `tierId`.
- Server refresh total setelah quantity/voucher change; optimistic UI hanya untuk interaction non-financial dan harus rollback.
- Empty cart illustration + browse CTA; unauthorized cart prompt; offline retry.
- Checkout disabled selama mutation/request in-flight.

**Wishlist screen**

- Grid/list, filter, bulk select/add to cart, remove confirmation, empty state.
- Toggle harus idempotent dan sinkron ketika kembali dari detail.

**Voucher list / detail**

- Available/claimed/expired states, code, discount, minimum spend, expiry, usage limit, claim, copy code, apply to cart.
- Validation error dekat field; jangan menampilkan voucher valid bila server menolak cart context.

### 5.6 Checkout, orders, payment

**Checkout screen**

- Review item/tier, quantity, event date, address/location picker, notes, voucher, fee, total, terms confirmation.
- Address supports country/region hierarchy and map picker/geolocation bila diaktifkan.
- Submit one time; button disabled + progress; rotation/rebuild tidak mengulang POST.
- Success menuju order detail/payment instruction berdasarkan payment state.

**Order history screen**

- Tabs/filter: pending payment, paid, processing, completed, cancelled/refund; search/order number; pull refresh.
- Status memakai icon + text + solid surface; badge tidak hanya warna.
- Empty first order CTA ke Catalog.

**Order detail screen**

- Order number, timeline/status, item/tier, address/event details, payment summary, transaction, proof, invoice, cancel, contact CS, write review/reorder.
- Track by order number; refresh status canonical server.

**Payment instruction / method detail / WebView**

- Payment method cards: VA, QRIS, gateway/WebView, upload proof bila diizinkan.
- Display amount = latest transaction total; deadline = transaction VA expiry; countdown jelas dan timezone-aware.
- Copy VA, show QR image, open WebView securely, upload proof with preview/progress/validation.
- Poll with backoff; state “payment pending”, “success”, “expired”, “failed”, “manual review”.
- Never show settlement account as customer VA; never create duplicate VA/order on retry.

### 5.7 Profile, KYC, identity scanner

**Profile**

- Avatar, name/email/phone, completion progress, KYC status, order/wishlist/review shortcuts, edit profile, language, settings.
- Complete profile and field editor have inline validation and server refresh.

**Document scanner flow**

Urutan: pilih dokumen (KTP/SIM/NPWP/Passport) → penjelasan permission kamera → camera frame/auto capture → review/crop/retake → upload progress → processing → result/issues → retry/complete.

- Camera permission diminta saat scanner dibuka, bukan startup.
- Selfie with document dan face scanner memiliki liveness/quality instruction, face guide, score, retake; kamera dan face detection memakai CameraX/ML Kit di Android dan AVFoundation/Vision di iOS melalui interface platform.
- `reason` ditampilkan sebagai tipe dokumen; issue berasal dari `reason_code`/`blocking_issue` dan diterjemahkan ke bahasa user.
- Temporary photos dibersihkan setelah upload; jangan cache KTP/selfie dalam gallery/log.
- Status: not started, uploading, processing, approved, rejected, needs retake, pending review.

### 5.8 Settings dan security

Settings terdiri dari language switcher, appearance/theme mode, notification settings, password & security, storage & data, app lock, legal.

**Appearance screen** wajib berisi:

- Theme mode selector: Light, Dark, System.
- Preview mini card yang menunjukkan gradient, glass card, text, status chip, dan capsule navigation.
- Fallback tanpa blur ketika device tidak mampu menjalankan blur; fallback tidak boleh menghilangkan hierarchy.
- Informasi bahwa perubahan theme hanya memengaruhi tampilan lokal, bukan status transaksi.

**Language screen** wajib berisi:

- Locale list dari API/local fallback, selected state, flag/icon opsional, language name, Apply/Done.
- Indikator sync dengan server dan retry ketika `PUT /user/language` gagal.
- Preview contoh heading/body/status agar user memahami perubahan.

Selain itu:

- Change password: current/new/confirmation, strength, failure/success.
- 2FA settings: status, setup OTP, backup codes, enable/disable confirmation.
- Trusted devices: list, verify “Ya, Ini Saya”, reject “Bukan Saya”, remove one/all.
- Login activity: device/location/time, revoke session.
- Recent emails dan security checkup.
- Saved login toggle dan switch account.
- App lock: PIN/password/biometric unlock, face enroll/verify, timeout, locked state; biometric melalui BiometricPrompt/Keystore di Android dan LocalAuthentication/Keychain di iOS.
- Storage/data: auto-download photo/video/document/audio, upload/download quality, network usage, proxy, storage usage/clear cache.

### 5.9 Reviews dan reports

- Catalog review list/all reviews, rating summary, photos grid, helpful vote, reply, report.
- My reviews: pending/completed/edit/delete; write review hanya untuk order/item eligible.
- User reviews profile/public list.
- Report submit dengan category, reason, optional attachment; report history/status/PDF bila tersedia.

### 5.10 Chat, calls, notifications

**Chat list / detail**

- Conversation list, unread count, search, staff category, online/typing hint bila tersedia.
- Message bubble: text, image, document, audio/video metadata, sticker/poll schema, timestamp, delivered/read, reply/forward/star/reaction/delete.
- Attachment picker dengan permission, preview, upload progress, retry, size/type error.
- Guest chat membuat guest token/inbox; authenticated chat memakai Sanctum.
- Call UI: incoming/outgoing/connecting/connected/ended/failed, camera/mic permission, mute/speaker/camera toggle, WebRTC reconnect; WebRTC dan signaling dijalankan melalui interface platform (Android/iOS WebRTC SDK).
- Realtime event hanya trigger refresh; jangan menganggap FCM/push payload sebagai state final.

**Notifications**

- List grouped by date/type, unread badge, read one/all, delete, deep link ke order/payment/chat/promo.
- FCM permission explanation, token register/refresh, local notification fallback.

### 5.11 Legal dan help

Terms of Service, Privacy Policy, Wedding Decoration Policy, Privacy & Terms combined, Help Center FAQ/contact, About. Content dapat berasal dari endpoint dynamic legal pages; loading/error/locale fallback wajib ada.

## 6. State Model Universal

Setiap screen harus memiliki state:

- initial loading
- content
- empty
- refreshing tanpa menghilangkan content
- pagination loading / end
- uploading dengan progress
- validation error
- unauthorized
- forbidden
- offline cached
- server error / retry
- not found

Action yang mengubah data/uang/file menjadi in-flight state di ViewModel (StateFlow/immutable UiState) agar double-submit aman. Snackbar hanya untuk feedback singkat; error penting dekat field/section. Destructive action selalu confirmation. Screen Compose bersifat presentational: menerima UiState dan callback, tanpa akses network, token, atau repository langsung.

Theme dan locale juga memiliki state tersendiri: `loadingPreference`, `localOnly`, `syncing`, `synced`, `syncError`, dan `fallback`. Perubahan lokal harus segera terasa, sedangkan sinkronisasi server boleh berjalan asynchronous.

## 7. API dan Data Contract Mobile

### 7.1 Transport

- Base URL berakhir `/api/` dan berasal dari environment dev/staging/prod melalui konfigurasi build per environment, bukan hard-code.
- HTTP client multiplatform (Ktor Client) di kode common adalah jalur utama dengan kontrak service interface dan serializable DTO; GraphQL/gRPC tersedia untuk kontrak/operasi tertentu, bukan alasan membuat business logic berbeda.
- Interceptor/plugin: locale (`Accept-Language`), bearer token, request ID, redacted debug logger, retry policy GET yang aman.
- AuthInterceptor membaca token dari store atomic/suspend, bukan dari mutable singleton; 401 ditangani satu session coordinator: clear secure store, cancel sensitive work, redirect login. 403 tidak otomatis logout.
- Secure token menggunakan platform secure storage (Android Keystore-backed, iOS Keychain) melalui interface; data nonsecret/cache memakai SQLDelight dan preference storage.
- Semua DTO menerima nullable/extra field dan mapper menormalisasi nominal, tanggal, status, relative media URL.
- LocaleStore memasang `Accept-Language` sesuai locale aktif; theme tidak dikirim sebagai business header kecuali endpoint preferensi theme resmi tersedia.
- Setelah login, bootstrap memanggil `GET /user/language`/`GET /settings` bila tersedia, lalu melakukan merge dengan preferensi lokal tanpa menghapus pilihan user secara diam-diam.

### 7.2 Endpoint mapping utama

- Auth: `/register` `/login` `/logout` `/forgot-password` `/reset-password` `/auth/send-otp` `/auth/verify-otp` `/auth/two-factor/*` `/auth/google` (dan social lain bila tersedia).
- Home: `/home` `/settings` `/categories` `/categories/{id}`.
- Catalog: `/catalog` `/catalog/public` `/catalog/featured` `/catalog/on-sale` `/catalog/{id}` `/catalog/{id}/tiers`.
- Search: `/search` `/search/image` `/cbir/search` `/cbir/stats` `/cbir/health`.
- Commerce: `/cart` `/cart/add` `/cart/{id}` `/wishlist*` `/vouchers*`.
- Orders: `/orders*` dan alias `/bookings*` `/transactions*`.
- Payment: `payment-info` `/pay` `/virtual-account` `/qris` `/upload-proof` `/confirm-payment` `/invoice` `/invoice/email`.
- Profile/KYC: `/user` `/profile*` `/profile/{document}` `/profile/selfie` `/profile/face-scan`.
- Security: `/security/*` `/profile/app-lock/*`.
- Chat: `/messages/*` `/messages/guest/*` `/messages/calls/*`.
- Notification: `/notifications/*` `/notifications/fcm-token`.
- Legal/geo: `/legal/*` `/legal-pages/*` `/regions/*` `/world-regions/*` `/geo/*`.

Harga, payment status, expiry, availability, KYC decision, unread badge, order timeline, dan URL media yang diterima dari endpoint adalah authoritative.

## 8. Domain Models, Database Contract, dan KMP Data Layer

Bagian ini adalah kontrak data utama untuk implementasi Kotlin Multiplatform. Mobile **tidak mengakses database Laravel secara langsung** dan tidak membuat keputusan bisnis sendiri. Laravel API adalah source of truth; database lokal mobile hanya cache, draft non-finansial, secure-session reference, dan queue operasi yang aman.

### 8.1 Aturan canonical model

- Setiap DTO API mengikuti JSON Laravel secara nullable/forward-compatible.
- Setiap DTO dipetakan ke domain model immutable; UI tidak menerima raw DTO.
- `id` server bertipe numerik panjang, bukan array index sebagai identity.
- Nominal rupiah disimpan sebagai bilangan bulat, bukan desimal/floating point; format currency hanya di presentation.
- Timestamp API dipetakan ke type datetime multiplatform (`Instant`); tanggal lokal/event memakai date type lokal, waktu appointment memakai datetime + timezone.
- URL media disimpan sebagai URL absolut hasil resolver; URL relative tidak bocor ke UI.
- Field server yang tidak dikenal disimpan/diabaikan tanpa crash; enum tidak dikenal dipetakan ke `Unknown`.
- Semua model memiliki `createdAt`, `updatedAt` bila server menyediakannya; cache juga memiliki `cachedAt`, `expiresAt`, `syncState`.
- DTO request dan response dipisahkan. Jangan mengirim kembali response object sebagai request karena dapat membocorkan field internal.
- Server-generated status, total, payment deadline, KYC decision, unread count, stock, dan permission tidak boleh dioverride oleh cache lokal.

### 8.2 Tipe bersama dan enum

Tipe bersama: UserId, CatalogId, TierId, OrderId, TransactionId, ConversationId, dan MessageId semuanya bertipe numerik panjang. Money terdiri dari amount (numerik panjang) dan currency (teks, default IDR). PageInfo terdiri dari currentPage, lastPage, perPage, total, dan hasNext.

Enum domain minimum:

| Enum | Nilai minimum |
|---|---|
| `AuthState` | Initial, Loading, Authenticated, OtpRequired, TwoFactorRequired, NeedsCompletion, Error, Offline, LoggedOut |
| `ThemeMode` | Light, Dark, System |
| `LocaleCode` | Id, En, Unknown/server-extensible |
| `KycStatus` | NotStarted, Uploading, Processing, Pending, Approved, Rejected, NeedsRetake, Unknown |
| `DocumentType` | Ktp, Sim, Npwp, Passport, Unknown |
| `OrderStatus` | Pending, Confirmed, Processing, InProgress, Completed, Cancelled, Refunded, Unknown |
| `OrderPaymentStatus` | Pending, Paid, Failed, Expired, Cancelled, ManualReview, Unknown |
| `TransactionStatus` | Pending, Success, Failed, Expired, Cancelled, Unknown |
| `PaymentMethodType` | VirtualAccount, Qris, Gateway, ManualProof, Unknown |
| `ReviewStatus` | Pending, Published, Rejected, Reported, Unknown |
| `MessageType` | Text, Image, Document, Audio, Video, Sticker, Poll, Call, System, Unknown |
| `MessageDelivery` | Sending, Sent, Delivered, Read, Failed, Unknown |
| `SyncState` | Fresh, Stale, PendingUpload, Syncing, Conflict, Failed |

Unknown enum values must remain renderable as a safe fallback; deserialization must never fail the entire response.

### 8.3 Auth/session models

**AuthSession** berisi: `sessionId` (identifier lokal), `userId`, `sanctumTokenRef` (reference saja; nilai token tidak pernah masuk database), `firebaseUserId` (reference non-secret), `provider` (password, google_firebase, facebook, apple), `isActive`, `lastValidatedAt`, `expiresAt`, `createdAt`.

Sensitive values: Sanctum token, Firebase ID token, refresh token, password, OTP, 2FA challenge token hanya di platform secure storage/memory. Android memakai Keystore-backed storage; iOS memakai Keychain.

**User** berisi: `id`, `fullName`, `firstName`, `middleName`, `lastName`, `username`, `email`, `phone/whatsapp`, `avatarUrl`, `provider`, `emailVerifiedAt`, `whatsappVerifiedAt`, `needsCompletion`, `identityType`, `kycStatus`, `profileCompletionPercent`, `language`, `themeMode`, `notificationPreferences`, `createdAt`, `updatedAt`.

Jangan menyimpan password, raw identity number, KTP image bytes, selfie bytes, Firebase ID token, Sanctum token, atau backup code di model cache biasa.

**Auth request/response models:**

- Login: identifier (email/username) + password; response berisi token, user, needsCompletion.
- Google login: request berisi Firebase ID token; response berisi token, user, needsOtp, needsCompletion.
- Register: name parts, username, email, whatsapp, identity type/number, birth data, region, address, password confirmation, optional multipart references.
- OtpChallenge: purpose, masked destination, expiresAt, resendAfter, attemptsRemaining.
- TwoFactorChallenge: challenge token reference, method, maskedEmail, expiresAt; challenge token hanya memory.
- Password reset: request email, lalu verifikasi email + OTP + password baru + konfirmasi.
- SessionInfo: deviceName, platform, ipMasked, lastActiveAt, isCurrent.

### 8.4 Catalog/content models

**Category:** `id`, `name` (LocalizedText), `slug`, `description`, `type`, `parentId`, `children`, `catalogCount`, `tierCount`, `imageUrl`, `isActive`.

**Catalog:** `id`, `title` (LocalizedText), `slug`, `description`, `category` (summary), `theme`, `tags`, `media` (list MediaAsset), `tiers` (optional di list response), `ratingAverage`, `ratingCount`, `isFeatured`, `isOnSale`, `isAvailable`, `stock`, `metadata`, `createdAt`, `updatedAt`. **Catalog tidak mempunyai field harga.**

**CatalogTier:** `id`, `catalogId`, `name` (LocalizedText), `description`, `price` (Money), `salePrice` (Money, optional), `capacity`, `theme`, `features`, `stock`, `isActive`, `isAvailable`.

`Package` adalah istilah backend untuk **Tier**. Mobile canonical memakai `CatalogTier` sebagai model data Tier, bukan screen/page. Price display wajib membaca harga Tier dari server dan tidak boleh menghitung harga Catalog sendiri.

**Supporting content:**

- `MediaAsset`: id, url, thumbnailUrl, type, mimeType, width, height, altText, sortOrder, blurHash optional.
- `LocalizedText`: defaultText plus translations per locale; server-selected text tetap diprioritaskan.
- `HomeData`: heroBanners, promotions, featuredCatalogs, onSaleCatalogs, categories, featuredReviews, stats, reminders.
- `HeroBanner`: id, title, subtitle, imageUrl, actionType, actionValue, startsAt, endsAt, isActive.
- `Promotion`: id, title, description, imageUrl, discount, startsAt, endsAt, actionUrl.

### 8.5 Search dan CBIR models

- `SearchQuery`: text, categoryId, minPrice, maxPrice, theme, capacity, sort, page, locale; filter harga diterapkan server terhadap harga Tier yang tersedia, bukan harga Catalog.
- `SearchSuggestion`: id, text, type, imageUrl optional, count optional.
- `SearchResult`: catalog, matchedTier optional, highlight optional, score optional, similarityPercent optional, source.
- `CbirUploadRequest`: fileRef, mimeType, byteSize, width, height, crop optional.
- `CbirResult`: id, catalogId, catalog, tier optional, similarityPercent, distance optional, matchedMediaUrl, rank.
- `CbirSearchSession`: id, queryMediaUrl optional, createdAt, resultCount, status.
- `CbirFeedback`: searchId, resultId, relevant, comment optional.
- `SearchHistoryItem`: id, type, queryText, imageThumbnailUrl, resultCount, createdAt.

Search result ranking, similarity, deduplication, history ownership, and index status berasal dari server. Local cache boleh menyimpan thumbnail result tetapi tidak boleh menandai similarity 100% atau membuat histori server.

### 8.6 Commerce models

**Cart:** id, userId, items, subtotal, discount, voucherDiscount, fees, total, currency, appliedVoucher, updatedAt.

**CartItem:** id, cartId, catalogId, tierId, catalog, tier, quantity, unitPrice, lineTotal, selected, availability, validationMessage optional.

`unitPrice`, `lineTotal`, `subtotal`, discount, fee, dan total adalah server values. Client mengirim quantity/selection/voucher request, lalu mengganti cart dengan response terbaru.

**WishlistItem:** id, userId, catalogId optional, tierId optional, catalog optional, tier optional, createdAt.

**Voucher:** id, code, name, localizedName, description, discountType, discountValue, maxDiscount optional, minSpend optional, quota optional, claimedCount optional, startsAt optional, expiresAt optional, isActive, isClaimed, terms.

**UserVoucher:** id, userId, voucherId, claimedAt, usedAt optional, status.

Discount type harus enum server (percentage, fixed, atau future unknown). Mobile hanya menampilkan validation response; tidak menghitung eligibility final.

### 8.7 Address, checkout, order, payment models

**UserAddress:** id, userId, label, recipientName, phone, countryCode, provinceCode, cityCode, districtCode, villageCode, postalCode, addressLine, latitude optional, longitude optional, isDefault.

**CheckoutDraft:** items, addressId optional, eventDate optional, eventTime optional, notes optional, voucherCode optional, acceptedPolicy, clientRequestId; draft lokal tidak authoritative.

**CreateOrderRequest:** items (catalogId, tierId, quantity), address, eventDate, eventTime, notes, voucherCode, paymentMethodId, idempotencyKey; client tidak mengirim price sebagai authority.

**Order:** id, orderNumber, userId, status, paymentStatus, items, address snapshot optional, eventDate optional, eventTime optional, notes, subtotal, discount, voucherDiscount, serviceFee, shippingFee, grandTotal, latestTransaction optional, timeline, paymentDeadline optional, cancelledAt optional, createdAt, updatedAt.

**OrderItem:** id, orderId, catalogId, tierId, catalogTitle, tierName optional, mediaUrl optional, quantity, unitPrice, discount, lineTotal, reviewable. Order item title/price/media adalah snapshot response; jangan menggantinya dengan current catalog saat melihat histori.

**Payment models:**

- `Transaction`: id, orderId, userId, reference, type, status, amount, currency, paymentMethod, virtualAccount, virtualAccountExpiry, qrisImageUrl, gatewayUrl, proofUrl optional, paidAt optional, failureReason optional, createdAt, updatedAt.
- `PaymentMethod`: id, name, type, provider, logoUrl, deeplink optional, instructions, isActive, supportsProofUpload.
- `PaymentInfo`: order, amount, deadline, paymentMethods, latestTransaction, qrisImageUrl, instructions.
- `Invoice`: id, orderId, invoiceNumber, downloadUrl, emailedAt optional, createdAt.
- `PaymentProof`: id, orderId, transactionId optional, fileUrl, status, submittedAt, reviewedAt optional, rejectionReason optional.

Display amount wajib mengikuti total transaksi terbaru; deadline mengikuti expiry VA transaksi; fallback 24 jam hanya jika API menyatakan fallback. QRIS hanya dari URL gambar QRIS server.

### 8.8 KYC/profile/security models

- `ProfileCompletion`: requiredFields, completedFields, percentage, blockingFields.
- `IdentityDocument`: id, userId, type, documentNumberMasked, status, frontImageUrl optional, backImageUrl optional, extractedName optional, extractedBirthDate optional, reasonCode optional, blockingIssues, submittedAt optional, reviewedAt optional, expiresAt optional.
- `SelfieVerification`: status, imageUrl optional, score optional, livenessScore optional, reasonCode optional, blockingIssues, submittedAt optional.
- `FaceScanResult`: status, score optional, liveness optional, match optional, reasonCode optional, blockingIssues.
- `KycIssue`: code, message, severity, field, canRetake.
- `SecurityCheckup`: score, checks, recommendations.
- `TrustedDevice`: id, name, platform, fingerprintMasked, isCurrent, isTrusted, createdAt, lastSeenAt.
- `LoginActivity`: id, deviceName, platform, locationMasked, ipMasked, createdAt, lastActiveAt, isCurrent.
- `SecurityEmail`: id, subject, type, sentAt, openedAt optional.
- `AppLockSettings`: enabled, method, timeoutSeconds, biometricAvailable, faceEnrolled.
- `NotificationPreferences`: push, order, payment, promotion, chat, security.

Never persist raw document number, raw OCR payload, face embeddings, KTP/selfie bytes, or security backup codes in ordinary local database. Masked document number and status are safe display projections only.

### 8.9 Review/report/legal models

- `Review`: id, user, catalogId, tierId optional, orderId optional, rating, title optional, body, photos, status, helpfulCount, viewerMarkedHelpful, replies, createdAt, updatedAt.
- `ReviewPhoto`: id, url, thumbnailUrl, width optional, height optional, sortOrder.
- `ReviewReply`: id, reviewId, author, body, createdAt, updatedAt.
- `ReviewSummary`: average, total, distribution, verifiedCount.
- `Report`: id, reporterId, targetType, targetId, category, reason, attachmentUrls, status, createdAt, updatedAt.
- `LegalDocument`: id, slug, type, title, body, locale, version, publishedAt, updatedAt.
- `HelpArticle`: id, category, title, body, localizedContent, sortOrder, isPublished.

Review/report mutations are server-authoritative; local drafts may be stored only until submitted and must be deletable by user.

### 8.10 Chat/call/notification models

**Conversation:** id, type, title, avatarUrl optional, participants, lastMessage optional, unreadCount, isGuest, guestTokenRef optional, isMuted, updatedAt.

**Message:** id, conversationId, senderId optional, senderName optional, type, body optional, attachments, replyToId optional, metadata, delivery, sentAt, editedAt optional, deletedAt optional.

**Attachment:** id, messageId, kind, url optional, localFileRef optional, mimeType, byteSize, name, width optional, height optional, durationMs optional, uploadState.

Metadata memakai typed models: `StickerMeta`, `PollMeta`, `CallMeta`; jangan menjadikan arbitrary meta type sebagai discriminator universal. Guest token hanya secure storage.

- `ChatCall`: id/token, conversationId, guest, state, direction, participant, startedAt, connectedAt optional, endedAt optional, endReason optional.
- `CallSignal`: token, type, payload, sequence, createdAt; payload hanya diproses WebRTC layer dan tidak dilog.
- `NotificationItem`: id, type, title, body, imageUrl optional, deepLink optional, relatedOrderId optional, relatedConversationId optional, isRead, createdAt.
- `UnreadCounts`: notifications, messages, orders optional, dari server; badge lokal hanya cache sementara.

### 8.11 Local database KMP

Gunakan **SQLDelight** sebagai shared KMP database agar schema/query dapat dipakai Android dan iOS. Room hanya boleh dipakai di sisi Android, Core Data hanya di sisi iOS; jangan membuat business persistence berbeda per platform.

Tabel cache lokal yang disarankan:

| Tabel | Kolom penting |
|---|---|
| `cached_users` | id, email, display_name, username, avatar_url, locale, theme_mode, kyc_status, completion_percent, json_extra, cached_at, updated_at |
| `cached_categories` | id, parent_id, name_default, name_json, slug, image_url, is_active, sort_order, cached_at, expires_at |
| `cached_catalogs` | id, slug, title_default, title_json, description_json, category_id, tier_count, has_available_tiers, rating_average, rating_count, is_featured, is_on_sale, is_available, json_extra, cached_at, updated_at, expires_at |
| `cached_catalog_media` | id, catalog_id FK, url, thumbnail_url, mime_type, width, height, alt_text, sort_order, cached_at |
| `cached_catalog_tiers` | id, catalog_id FK, name_default, name_json, description_json, amount, sale_amount, capacity, stock, is_active, is_available, json_extra, cached_at, updated_at |
| `cached_cart` | id, user_id, subtotal_amount, discount_amount, fee_amount, total_amount, currency, voucher_json, server_updated_at, cached_at |
| `cached_cart_items` | id, cart_id FK, catalog_id, tier_id, quantity, selected, unit_amount, line_total_amount, availability, json_extra, cached_at |
| `cached_wishlist` | id, user_id, catalog_id, tier_id, created_at, cached_at |
| `cached_vouchers` | id, code UNIQUE, name_json, discount_type, discount_amount, min_spend_amount, max_discount_amount, starts_at, expires_at, status, cached_at, cache_expires_at |
| `cached_orders` | id, user_id, order_number UNIQUE, status, payment_status, grand_total_amount, currency, event_date, event_time, deadline, json_snapshot, created_at, updated_at, cached_at |
| `cached_order_items` | id, order_id FK, catalog_id, tier_id, title_snapshot, tier_snapshot, quantity, unit_amount, line_total_amount, media_url, reviewable |
| `cached_order_timeline` | id, order_id FK, status, label, description, created_at, sort_order |
| `cached_transactions` | id, order_id FK, reference, status, amount, currency, method_type, va_masked, va_expiry, qris_url, gateway_url, paid_at, updated_at |
| `cached_reviews` | id, catalog_id, tier_id, order_id, user_id, rating, body, status, helpful_count, json_snapshot, created_at, updated_at |
| `cached_conversations` | id, type, title, avatar_url, unread_count, last_message_json, is_guest, updated_at, cached_at |
| `cached_messages` | id, conversation_id FK, client_message_id UNIQUE, sender_id, type, body, metadata_json, delivery, sent_at, edited_at, deleted_at, local_attachment_ref, cached_at |
| `cached_notifications` | id, type, title, body, deep_link, is_read, created_at, cached_at |
| `cached_search_history` | id, type, query_text, thumbnail_url, result_count, created_at |
| `sync_queue` | id, operation_id UNIQUE, aggregate_type, aggregate_id, operation, request_json, idempotency_key, attempts, next_retry_at, state, last_error_code, created_at, updated_at |
| `app_preferences` | key, value, version, updated_at |

Rules local database:

- Never store raw Sanctum/Firebase token, password, OTP, 2FA challenge, KTP/selfie/payment-proof bytes, or WebRTC SDP/candidate payload in SQLDelight.
- `cached_*` tables are replaceable projections. A logout clears user-scoped rows, except anonymous catalog cache explicitly marked public.
- Every user-scoped table includes `user_id` or is reachable through a user-scoped parent; account switching must never show account A data to account B.
- Use foreign keys/cascade for child cache (catalog media, catalog tiers, order items, messages) and unique constraints for server IDs/client operation IDs.
- Store money as integer columns (`*_amount`), boolean as integer, dates as ISO strings/epoch UTC, and JSON only for extensible metadata—not fields required for filtering/sorting.
- Add indexes on `updated_at`, `expires_at`, `status`, `user_id`, `order_number`, `conversation_id`, `catalog_id`, `is_read`, and `next_retry_at`.
- Cache TTL: public settings/legal/catalog can be stale-read; payment/order/KYC/security must refresh before display of critical action.

### 8.11.1 Server database reference dan relationship map

Daftar berikut adalah peta database Laravel untuk mapper/API client. Nama tabel server tidak boleh disalin mentah sebagai nama domain mobile bila sudah ada alias/renaming; mobile mengikuti response API canonical.

| Server table/model | Primary key dan relasi penting | Isi yang wajib dipahami client |
|---|---|---|
| `users` / User | id; addresses, documents, carts, wishlist, orders, transactions, reviews, sessions, devices, notifications | identity, profile, locale, KYC summary, provider metadata; field legacy KTP tetap compatibility-only |
| `admins` / Admin | id; guard admin | tidak pernah menjadi mobile customer model |
| `personal_access_tokens` | token id; morph tokenable | Sanctum backend store; token value tidak pernah masuk local DB |
| `user_languages` | user relation, locale | preference language server; fallback `id` |
| `user_addresses` | id, user_id | address checkout/profile; region codes dan default flag |
| `identity_documents` | id, user_id, unique (user_id, document_type) | canonical KTP/SIM/NPWP/Passport; AI fields dan blocking issues |
| `categories` | id, nullable parent_id | category tree, translations, type, active state |
| `catalogs` | id, category_id, slug unique | visual/parent entity; tidak memiliki harga beli langsung |
| `packages` | id, catalog_id, category relation | **Tier entity**; nama, harga, promo, kapasitas, features, stock, availability; bukan halaman |
| `media` / Spatie Media | morph owner (model_type, model_id) | image/document asset metadata; resolve URL via API origin |
| `carts` | id, user_id, catalog/tier references | server cart aggregate; purchasable item wajib selected Tier |
| `wishlists` | id, user_id, catalog/package/tier compatibility | personal saved items; toggle idempotent |
| `vouchers` | id, discount relation | code, translation, validity, quota, discount rules |
| `user_vouchers` | user + voucher unique/usage relation | claimed/used/expired projection |
| `discounts` | morph target and discount type | internal discount rule; never calculate final eligibility locally |
| `orders` | id, user_id, catalog + package/tier refs | order aggregate, selected Tier price snapshot, status/payment status, booking date, total, notes |
| `order_timelines` | order_id | chronological server status events |
| `order_payments` | order/payment relation | payment snapshots/attempts where returned |
| `refund_requests` | order/transaction relation | refund status and reason |
| `transactions` | id, user_id, nullable order_id, unique reference | amount, admin fee, total amount, gateway/method, VA/QRIS metadata, status |
| `payment_methods` | id, gateway/bank relation | active method, type, deeplink, instruction, proof support |
| `payment_gateways` | id | provider configuration projection; secrets never returned |
| `banks` | id | bank display data for VA/instruction |
| `reviews` | id, user, order, catalog/tier relation | rating, title/body, moderation, helpful count, photos |
| `review_votes` | user + review unique | current viewer helpful state |
| `review_replies` | review_id, author | nested reply list |
| `review_reports` | review/reporter | moderation/report status |
| `reports` | id, reporter/target | customer content/order report, attachments, PDF/status |
| `fm_inboxes` | id, JSON user_ids, soft delete | conversation participant set and title |
| `fm_messages` | id, inbox/user, soft delete | text, read arrays, timestamps; attachment metadata may be in related media/meta |
| `chat_calls` | call token/conversation relation | signaling lifecycle; payload stays transient |
| `notifications` | polymorphic notifiable | title/body/type/read/deep link projection |
| device token/FCM tables | user/device relation | FCM registration; token refresh/revoke |
| `cbir_searches` | user/session/search data | server-owned image search history |
| `cbir_search_results` | search + catalog/media result | ranked similarity results |
| `cbir_feedback` | user/search/result | relevance feedback |
| `search_logs` | user/query/request metadata | text/search analytics; sensitive query redaction |
| `hero_banners`, `promotions` | content records | Home editorial content and date/active state |
| `legal_pages`, `terms_of_service`, `privacy_policies`, `wedding_decoration_policies`, `helps` | content + translations | dynamic legal/help content by locale |
| `provinces`, `cities`, `districts`, `villages` | hierarchical geographic keys | checkout/profile selectors; use code/id from API |
| `trusted_devices`, `user_sessions`, `security_emails`, `security_events`, `backup_codes` | user/security relations | security projections only; secrets never cached |

Relationship rules:

- User 1──* IdentityDocument, UserAddress, CartItem/WishlistItem/Order/Transaction/Review/Notification.
- Category 1──* Catalog; Catalog 1──* CatalogTier/Package (harga hanya di Tier); Catalog 1──* MediaAsset.
- Order 1──* OrderItem/OrderTimeline/Transaction/Review eligibility.
- Transaction *──1 PaymentMethod/PaymentGateway (bila dikembalikan).
- Review 1──* ReviewPhoto/ReviewReply/ReviewVote/ReviewReport.
- Conversation 1──* Message 1──* Attachment; CbirSearch 1──* CbirSearchResult.

Compatibility rules:

1. Physical migrations may retain `products`, `packages`, `product_id`, `package_id`, and legacy user document columns; mobile canonical terms remain Catalog, CatalogTier, and IdentityDocument.
2. New KYC writes/reads use `identity_documents`; do not create a second local model for legacy `ktp_*` fields.
3. A server response may flatten or nest relations depending endpoint. Mapper must support both documented shapes and normalize into one domain graph.
4. Do not expose Eloquent internals, admin-only columns, AI raw payload, payment gateway secrets, or polymorphic class names to Compose UI.
5. Deletion/soft deletion from server must be represented as tombstone where needed; a missing list item is not permission to recreate it locally.

### 8.12 Repository and use-case contract KMP

Repository dan operasi minimum:

- **AuthRepository:** login, register, signInWithFirebaseGoogle, verifyOtp, verifyTwoFactor, forgotPassword, resetPassword, currentUser, logout, logoutAll, savedAccounts, activeAccountIndex, switchToAccount, switchToNextAccount, logoutForReauth.
- **CatalogRepository:** home, categories, listCatalog, catalogDetail, tiers, refresh, observeCached.
- **CommerceRepository:** cart, add/update/removeCart, wishlist, vouchers, validateVoucher.
- **OrderRepository:** createOrder, listOrders, orderDetail, paymentInfo, cancelOrder, invoice.
- **PaymentRepository:** createVa, createQris, initiateGateway, uploadProof, confirmPayment, pollStatus.
- **ProfileRepository:** profile, updateProfile, completion, identityDocument, selfie, faceScan.
- **ChatRepository:** conversations, messages, sendMessage, uploadAttachment, read, reaction, calls.

Use cases wajib memisahkan query dan command:

- Query boleh membaca cache lalu refresh; command POST/PUT/DELETE harus mengikuti response server dan idempotency.
- CreateOrder, CreateVirtualAccount, CreateQris, UploadPaymentProof, SendMessage, dan KYC upload tidak boleh di-retry otomatis tanpa idempotency/request state.
- Repository mengembalikan Result-style data (success/ApiError) atau Flow state, bukan exception mentah ke Compose UI.
- Mapper harus memiliki test untuk null, field tambahan, enum unknown, nominal zero/large, timezone, relative URL, dan malformed optional object.

### 8.13 Platform boundary KMP

Kode common dilarang mengimpor `android.*`, UIKit, Activity, Firebase Android SDK, atau API platform lain. Firebase Auth, secure storage, camera, biometric, file picker, connectivity, notification, dan WebRTC di-inject melalui interface dengan implementasi aktual di platform Android/iOS.

### 8.14 Database migration dan seed contract

- SQLDelight schema migration harus versioned, forward-only, dan diuji dari database kosong serta setiap versi sebelumnya.
- Migration tidak boleh mengubah server ID atau menghapus pending order/payment data secara otomatis.
- Saat logout/account switch: hapus secure token sesuai session; purge user-scoped cache; public catalog cache boleh dipertahankan.
- Saat app upgrade, migrate legacy boolean dark mode ke theme mode; migrate legacy package/product keys ke catalog/tier names.
- Seed hanya fake data untuk Compose Preview/UI test; tidak boleh memakai KTP, token, payment proof, Firebase credential, atau data production.
- Preview fake models wajib tersedia untuk loading, success, empty, error, expired payment, rejected KYC, unread chat, long text, dark theme, large font, dan offline cache.

### 8.15 Data privacy dan retention mobile

| Data | Storage | Retention |
|---|---|---|
| Sanctum token | Keystore/Keychain | sampai logout/revoke/expiry |
| Firebase session | Firebase SDK + platform secure state | sampai sign out/revoke |
| User profile | encrypted/normal local cache | sampai logout atau TTL policy |
| Catalog/public content | SQLDelight cache | TTL dan invalidation server |
| Order/payment | cache terbatas | refresh sebelum action; purge sesuai policy |
| KTP/selfie/face scan | temporary encrypted file only | hapus setelah upload/failed retry selesai |
| Payment proof | temporary encrypted file only | hapus setelah upload result |
| Chat attachment draft | encrypted temp file | hapus setelah send/cancel/expiry |
| Analytics/crash | redacted only | provider retention policy |

## 9. Kotlin Multiplatform Architecture

Aplikasi dibangun dengan Kotlin Multiplatform dan Compose Multiplatform. Logika bisnis, DTO, mapper, repository, use case, ViewModel/state holder, design system, dan seluruh screen utama berada di kode common dan berjalan sama di Android dan iOS.

Setiap feature memakai pembagian lapisan:

- **data:** kontrak service API, DTO serializable, mapper DTO↔domain, repository implementation, local database DAO.
- **domain:** model immutable, interface repository, use case yang memisahkan query dan command.
- **presentation:** Route (menghubungkan ViewModel dan navigation), Screen (Composable presentational: menerima UiState dan callback), ViewModel (StateFlow immutable), UiState, UiEvent, dan komponen screen khusus.

Feature utama: auth, catalog, search/cbir, cart/wishlist, order/payment, profile/kyc, review, chat, notification, settings.

Dependencies utama: Compose Multiplatform + Material 3, Compose Navigation, Kotlinx Serialization, Ktor Client, SQLDelight, DataStore/preference storage, DI multiplatform (misal Koin), Coil untuk image loading, library Firebase multiplatform atau Firebase actual per platform (Auth, Messaging, Analytics, Crashlytics, Remote Config), Google Sign-In/Credential Manager adapter, WebRTC, camera dan face/text ML per platform, secure storage Keystore/Keychain, WebView per platform, geolocation/map, QR, dan animasi Compose.

ViewModel tidak boleh menyimpan secret di log/state dan tidak mengakses endpoint yang bukan feature-nya.

## 10. Firebase dan Analytics

- FCM untuk push notification; token dikirim ke Laravel `/notifications/fcm-token`.
- Firebase Auth/social provider hanya adapter; Sanctum tetap authoritative untuk session API.
- Analytics event UX tanpa PII, token, KTP, selfie, bukti bayar, nomor VA, isi chat, atau query sensitif.
- Crash/non-fatal reporting harus redact HTTP headers/body dan file metadata sensitif.
- Remote Config dapat mengatur feature flag/maintenance banner; tidak menggantikan business rule server.
- Performance monitoring untuk cold start, image/search/upload/payment timing bila diperlukan.

## 11. Permission, Privacy, Accessibility

- Kamera, gallery, microphone, notification, location, storage, dan biometric diminta just-in-time dengan rationale melalui bridge permission per platform.
- Denied/permanently denied memiliki fallback gallery/manual input/settings CTA.
- KTP/selfie/payment proof tidak dipreview dalam analytics/log/screenshot test.
- Semua image punya semantic label; button punya content description; tab punya selected state dan role.
- Minimum contrast WCAG AA; status memakai icon + label; font scale 1.0–1.3 diuji tanpa clipping.
- Reduced motion mematikan blur/animation nonessential; low-end device tetap readable dengan glass fallback solid.

## 12. Acceptance Criteria per Journey

### Browse to purchase

1. Guest dapat startup → landing → home/catalog → detail.
2. Filter/search/pagination tidak kehilangan query atau scroll state.
3. Login prompt muncul saat wishlist/cart/checkout, lalu kembali ke intended action.
4. Checkout menampilkan total dari server dan hanya membuat satu order saat double tap/rotation.
5. Payment instruction menampilkan nominal dan expiry transaksi terbaru.
6. Webhook/poll refresh mengubah order state tanpa client menebak status.

### Visual search

1. Permission dijelaskan sebelum kamera.
2. User dapat kamera/gallery, preview, retake, upload progress, cancel, retry.
3. Result menampilkan ranking/similarity server dan dapat membuka detail.
4. Failure AI/time-out memiliki retry dan tidak membuat histori palsu.

### KYC

1. User memilih salah satu KTP/SIM/NPWP/Passport.
2. Scanner memandu kualitas foto dan tidak menyimpan foto sensitif secara permanen di cache publik.
3. Processing/pending/rejected/needs-retake/approved dapat dibedakan.
4. Issue tampil sebagai bahasa manusia; `reason` tidak disalahartikan sebagai kode error.

### Chat/notification

1. Guest dan authenticated chat memiliki route/token yang benar.
2. Attachment menampilkan progress/error; message state diambil dari server.
3. Push membuka deep link target dan unread count sinkron setelah read.
4. Call dua device nyata dapat connect/end/reconnect dengan permission state jelas.

## 13. Testing Matrix

- Unit: DTO null/extra field, mapper nominal/date/media, validators, formatter, KYC utility, API error parser.
- ViewModel/state: loading/content/empty/error/offline, retry, pagination, rotation/process death, double-submit, session 401/403.
- Compose UI test/golden: light/dark/system, phone/tablet, large font, reduced motion, glass blur, no-blur/opaque fallback, full-screen/bottom capsule.
- Preference tests: language switch `id ↔ en`, unsupported locale fallback, `Accept-Language`, server sync failure/retry, theme persistence, legacy boolean migration, OS brightness change in System mode.
- Auth tests: Firebase credential exchange, invalid/expired ID token, cancelled Google picker, Firebase-success/Laravel-failure rollback, existing/new Google account, account conflict, OTP/2FA challenge, Sanctum persistence, logout both providers, 401 session recovery, switch account (long-press sheet, double-tap next account, isolation antar akun).
- Integration/MockWebServer-style: auth, catalog, CBIR multipart, KYC upload, cart/order/payment, notification deep link, chat attachment.
- Device: camera/gallery permission, biometric, FCM, WebView payment, WebRTC dua perangkat nyata.
- Build/test memakai **Kotlin Toolchain** CLI: `kotlin build` untuk build APK, `kotlin test` untuk unit/integration test, `kotlin check` untuk lint/checks (daftar via `kotlin show checks`), `kotlin run -m <module>` untuk menjalankan di device/emulator, dan `kotlin package -m <module>` untuk AAB release. Konfigurasi dev/staging/prod (base URL dan feature flag) berasal dari konfigurasi build per environment, bukan hard-code.

## 14. Deliverables

Buat visual flow dan screen set berikut:

1. Brand/landing/onboarding/auth/OTP/2FA.
2. Home editorial dan 4/5-tab shell dengan bottom capsule.
3. Catalog grid, filter sheet, detail, tier picker, image viewer.
4. Search, visual search camera/gallery, scan preview, result grid.
5. Wishlist, cart, voucher, checkout/address/date.
6. Order list/detail/timeline, payment method, VA, QRIS, upload proof, WebView.
7. Profile/completion, document scanner, selfie/face scan, KYC status.
8. Settings/security/2FA/trusted devices/login activity/app lock/storage.
9. Reviews/report/legal/help.
10. Chat list/detail, attachment menu, call states, notification list/detail.
11. Untuk setiap screen: loading, success, empty, validation, unauthorized, forbidden, offline, server error, upload progress, dark theme, large font, tablet.

