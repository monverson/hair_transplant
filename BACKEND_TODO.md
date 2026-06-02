# Backend Roadmap - HairTrack

## ✅ Tamamlanan

### Faz 1: Database Layer
- [x] PostgreSQL + Docker setup
- [x] User, Photo, Analysis entities
- [x] Repositories

### Faz 2: Authentication
- [x] JWT token sistemi
- [x] Register endpoint
- [x] Login endpoint
- [x] Spring Security + JWT filter

### Faz 3: Photo Storage
- [x] MinIO (local) + Cloudflare R2 ready (production)
- [x] Photo upload endpoint
- [x] Signed URL generation
- [x] Photo list / delete

### Faz 4: Business Logic
- [x] 4.1 User Profile endpoints (GET/PATCH /me)
- [x] 4.2 Analysis endpoint (multi-language tr/en)
- [x] 4.3 Stage-based Notifications (milestone sistemi)
- [x] 4.4 Timeline + Comparison + Progress Summary
- [x] Photo entity refactor (dynamic date calculation via DateUtil)

### Faz 5: Production Hardening
- [x] 5.1 Error handling (@ControllerAdvice + custom exceptions)
- [x] 5.2 Input validation (@Valid + jakarta.validation)
- [~] 5.3 CORS configuration (SKIPPED - iOS only, no web)
- [x] 5.4 API documentation (Swagger - SpringDoc 2.8.13)
- [~] 5.5 Logging improvements (DEFERRED to Faz 10)

---

## ⏸️ Ertelenen (Frontend/iOS sonrası)

### Faz 6: Auth Improvements
- [ ] 6.1 Email verification (post-launch)
- [ ] 6.2 Password reset (post-launch)
- [ ] 6.3 Refresh token (post-launch)
- [ ] 6.4 Account deletion (App Store ZORUNLU - launch öncesi ekle)

### Faz 7: AI Integration
- [ ] 7.1 Real Claude API (model: claude-sonnet-4-6, mock kapatılacak)
- [ ] 7.2 Rate limiting (kullanıcı bazlı)
- [ ] 7.3 API cost tracking

### Faz 8: Subscription
- [ ] 8.1 RevenueCat webhook entegrasyonu
- [ ] 8.2 Subscription status middleware
- [ ] 8.3 Free tier vs Premium kısıtları

### Faz 9: Push Notifications
- [ ] 9.1 Firebase Cloud Messaging setup
- [ ] 9.2 Device token kaydetme
- [ ] 9.3 Stage-based push gönderme

### Faz 10: Operations
- [ ] 10.1 Database migrations (Flyway)
- [ ] 10.2 Image processing (resize, EXIF temizleme, thumbnail)
- [ ] 10.3 Health check + metrics endpoint
- [ ] 10.4 Logging improvements (structured JSON, Sentry)

### Faz 11: Deploy
- [ ] 11.1 Production Dockerfile
- [ ] 11.2 Hetzner sunucu kurulum
- [ ] 11.3 Cloudflare R2 entegrasyonu (MinIO'dan geçiş)
- [ ] 11.4 SSL + domain
- [ ] 11.5 CI/CD pipeline (GitHub Actions)

---

## 📝 Önemli Notlar

- **Model adı:** `claude-sonnet-4-6` (eski `claude-sonnet-4-5` retired oldu - Nisan 2026)
- **Account deletion:** App Store launch için ZORUNLU, Faz 6.4'ü launch öncesi yap
- **Mock mode:** Şu an `claude.mock.enabled=true` - gerçek API Faz 7'de
- **Storage:** Dev'de MinIO, production'da Cloudflare R2
- **Profiles:** dev (local) ve prod (environment variables)

---

## 🎯 Mevcut Endpoint'ler

**Auth:** POST /api/auth/register, /api/auth/login
**User:** GET/PATCH /api/users/me
**Photos:** POST /api/photos/upload, GET /api/photos, GET /api/photos/{id}/url, DELETE /api/photos/{id}
**Analyses:** POST /api/analyses, GET /api/analyses, GET /api/analyses/latest
**Notifications:** GET /api/notifications, /unread, PATCH /{id}/read, /read-all
**Timeline:** GET /api/timeline
**Comparison:** POST /api/comparisons
**Progress:** GET /api/progress/summary
**Docs:** /swagger-ui.html