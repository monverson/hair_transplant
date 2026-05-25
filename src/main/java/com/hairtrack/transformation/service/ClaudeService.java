package com.hairtrack.transformation.service;

import com.hairtrack.transformation.dto.AnalysisRequest;
import com.hairtrack.transformation.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaudeService {

    @Value("${claude.mock.enabled:false}")
    private boolean mockEnabled;

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.url}")
    private String apiUrl;

    @Value("${claude.api.model}")
    private String model;

    @Value("${claude.api.version}")
    private String apiVersion;

    private final RestTemplate restTemplate = new RestTemplate();

    public AnalysisResponse analyzePhoto(AnalysisRequest request) {
        log.info("Analyzing photo for user: {}", request.getUserId());

        // MOCK MODE - API çağırmadan sahte cevap döndür
        if (mockEnabled) {
            log.info("Mock mode enabled - returning fake response");
            return buildMockResponse(request);
        }

        String prompt = buildPrompt(request);

        // Claude API request body
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("max_tokens", 1500);

        Map<String, Object> imageSource = new HashMap<>();
        imageSource.put("type", "base64");
        imageSource.put("media_type", "image/jpeg");
        imageSource.put("data", request.getPhotoBase64());

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image");
        imageContent.put("source", imageSource);

        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", List.of(imageContent, textContent));

        body.put("messages", List.of(message));

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", apiVersion);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map response = restTemplate.postForObject(apiUrl, entity, Map.class);
            String analysisText = extractText(response);

            return new AnalysisResponse(
                    7.5, // şimdilik hardcoded, sonra parse edeceğiz
                    "Normal for " + request.getMonthsPostOp() + " months",
                    "active",
                    "Minoxidil kullanmaya devam et",
                    analysisText
            );
        } catch (Exception e) {
            log.error("Claude API error", e);
            throw new RuntimeException("Analiz yapılamadı: " + e.getMessage());
        }
    }

    private String buildPrompt(AnalysisRequest request) {
        return String.format("""
            Sen bir saç ekimi recovery analistisin. Bu fotoğrafı analiz et.

            KULLANICI BİLGİSİ:
            - Ekim sonrası ay: %d
            - Toplam graft: %d
            - Yöntem: %s
            - Bölgeler: %s
            - İlaçlar: %s

            Analiz et ve şu bilgileri ver:
            1. Density score (0-10)
            2. Bu aşama için durum (ahead/normal/behind)
            3. Shock loss durumu (active/resolving/completed/none)
            4. Bir tane actionable öneri
            5. Kullanıcıya psikolojik destek mesajı

            Kısa ve net cevap ver. Türkçe yaz.
            """,
                request.getMonthsPostOp(),
                request.getTotalGrafts(),
                request.getMethod(),
                request.getZones(),
                request.getMedications()
        );
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map response) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (content != null && !content.isEmpty()) {
            return (String) content.get(0).get("text");
        }
        return "No response";
    }


    private AnalysisResponse buildMockResponse(AnalysisRequest request) {
        int months = request.getMonthsPostOp();

        // Aşamaya göre farklı mock data
        if (months <= 1) {
            return new AnalysisResponse(
                    4.5,
                    "Normal for " + months + " months - shock loss başlıyor",
                    "active",
                    "Bu en zor dönem, panikleme. Minoxidil'e devam et.",
                    "1. ay analizi: Shock loss aktif. Saçların dökülüyor, bu beklenen bir durum. " +
                            "3. aydan sonra büyüme başlayacak. Şu an aynaya günde 5 kere bakmayı bırak."
            );
        } else if (months <= 3) {
            return new AnalysisResponse(
                    5.0,
                    "Normal for " + months + " months - shock loss peak",
                    "active",
                    "Bu aşamayı sabırla geç, sonuçlar 6. aydan sonra netleşecek.",
                    "3. ay analizi: Shock loss devam ediyor. En kötü görüntü dönemi. Saçların seyrek görünüyor."
            );
        } else if (months <= 6) {
            return new AnalysisResponse(
                    6.5,
                    "Normal for " + months + " months - büyüme başladı",
                    "resolving",
                    "İyi gidiyorsun, hairline kapanmaya başladı.",
                    "6. ay analizi: Ektirilen greftlerin %40-50'si çıkmış durumda. Hairline netleşiyor, crown hâlâ seyrek."
            );
        } else if (months <= 12) {
            return new AnalysisResponse(
                    7.8,
                    "Normal for " + months + " months",
                    "completed",
                    "Mükemmel ilerleme. 12. ayda final değerlendirme yapalım.",
                    "9. ay analizi: %70-80 final görüntüye ulaşılmış. Crown'da hâlâ doluş bekleniyor."
            );
        } else {
            return new AnalysisResponse(
                    8.5,
                    "Final result - " + months + " months",
                    "completed",
                    "Final sonuç görünüyor. Mevcut saçlarını koruman için finasteride değerlendir.",
                    "12+ ay analizi: Final sonuç. Greft tutunum oranı yüksek görünüyor."
            );
        }
    }
}
