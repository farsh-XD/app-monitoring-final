# HG6245N Monitor (Android 7+ / API 24+)

Aplikasi dashboard monitoring Android 7.0 (API 24) ke atas untuk memantau:
- **Memory Usage** (persentase & progress bar)
- **CPU Usage** (persentase & progress bar)
- **Suhu CPU** (indikator suhu °C + status termal)
- **Pengguna yang Tersambung** (daftar perangkat aktif lengkap dengan nama/hostname, IP, MAC, dan jalur koneksi)

Target default router/ONT: `http://192.168.1.100` (HG6245N) dengan dukungan HTTP lokal (`cleartextTrafficPermitted="true"`) dan *fallback* data awal jika endpoint belum merespons langsung.
