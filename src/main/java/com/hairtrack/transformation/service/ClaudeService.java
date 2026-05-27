package com.hairtrack.transformation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hairtrack.transformation.entity.Analysis;
import com.hairtrack.transformation.entity.Photo;
import com.hairtrack.transformation.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClaudeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StorageService storageService;
    @Value("${claude.api.key}")
    private String apiKey;
    @Value("${claude.api.url}")
    private String apiUrl;
    @Value("${claude.api.model}")
    private String model;
    @Value("${claude.api.version}")
    private String apiVersion;
    @Value("${claude.mock.enabled:false}")
    private boolean mockEnabled;

    public AnalysisResult analyzePhoto(Photo photo, User user, String photoBase64, String language) {
        log.info("Analyzing photo {} for user {} in {}", photo.getId(), user.getId(), language);

        if (mockEnabled) {
            log.info("Mock mode enabled - returning fake analysis");
            return buildMockAnalysis(photo, language);
        }

        return callClaudeApi(photo, user, photoBase64, language);
    }

    private AnalysisResult callClaudeApi(Photo photo, User user, String photoBase64,String language) {
        String prompt = buildPrompt(photo, user,language);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("max_tokens", 1500);

        Map<String, Object> imageSource = new HashMap<>();
        imageSource.put("type", "base64");
        imageSource.put("media_type", "image/jpeg");
        imageSource.put("data", photoBase64);

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", apiVersion);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map response = restTemplate.postForObject(apiUrl, entity, Map.class);
            String analysisText = extractText(response);

            // JSON parse et
            return parseClaudeResponse(analysisText);

        } catch (Exception e) {
            log.error("Claude API error", e);
            throw new RuntimeException("Analysis failed: " + e.getMessage(), e);
        }
    }

    private AnalysisResult parseClaudeResponse(String jsonText) {
        try {
            // Claude bazen markdown code block içinde döner (```json ... ```), temizle
            String cleaned = jsonText.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            JsonNode root = objectMapper.readTree(cleaned);

            return AnalysisResult.builder()
                    .densityScore(root.get("densityScore").asDouble())
                    .hairlineScore(root.has("hairlineScore") ? root.get("hairlineScore").asDouble() : null)
                    .crownScore(root.has("crownScore") ? root.get("crownScore").asDouble() : null)
                    .templeScore(root.has("templeScore") ? root.get("templeScore").asDouble() : null)
                    .shockLossStatus(Analysis.ShockLossStatus.valueOf(root.get("shockLossStatus").asText()))
                    .stageAssessment(root.get("stageAssessment").asText())
                    .recommendation(root.get("recommendation").asText())
                    .rawAnalysis(root.get("rawAnalysis").asText())
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse Claude response: {}", jsonText, e);
            // Fallback: parse edemezsek raw text'i recommendation olarak ver
            return AnalysisResult.builder()
                    .densityScore(0.0)
                    .stageAssessment("Parse error - check raw analysis")
                    .shockLossStatus(Analysis.ShockLossStatus.NONE)
                    .recommendation("Analiz parse edilemedi, ham metin: " + jsonText.substring(0, Math.min(200, jsonText.length())))
                    .rawAnalysis(jsonText)
                    .build();
        }
    }

    private AnalysisResult buildMockAnalysis(Photo photo, String language) {
        Integer months = photo.getMonthsSinceTransplant() != null ? photo.getMonthsSinceTransplant() : 0;
        Integer days = photo.getDaysSinceTransplant() != null ? photo.getDaysSinceTransplant() : 0;

        boolean isTurkish = "tr".equalsIgnoreCase(language);

        // 0-14 gün: healing
        if (days <= 14) {
            return AnalysisResult.builder()
                    .densityScore(8.5)
                    .hairlineScore(9.0)
                    .crownScore(8.0)
                    .templeScore(8.5)
                    .stageAssessment(isTurkish
                            ? "İyileşme dönemi - " + days + ". gün"
                            : "Healing phase - day " + days)
                    .shockLossStatus(Analysis.ShockLossStatus.NONE)
                    .recommendation(isTurkish
                            ? (days <= 7
                            ? "Kabuklar oluşuyor, sakın kaşıma. İlk hafta kritik."
                            : "Kabuklar dökülmeye başladı, bu normal.")
                            : (days <= 7
                            ? "Scabs are forming, do not scratch. First week is critical."
                            : "Scabs are starting to fall off, this is normal."))
                    .rawAnalysis(isTurkish
                            ? "İlk 14 gün healing dönemi. Tüm greftler yerleşmiş, kabuklar normal süreçte."
                            : "First 14 days are the healing phase. All grafts placed, scabs in normal process.")
                    .build();
        }

        // 1. ay: shock loss başlangıcı
        if (months <= 1) {
            return AnalysisResult.builder()
                    .densityScore(4.5)
                    .hairlineScore(5.0)
                    .crownScore(4.0)
                    .templeScore(4.5)
                    .stageAssessment(isTurkish
                            ? "Normal - " + months + ". ay, shock loss aktif"
                            : "Normal for " + months + " months - shock loss active")
                    .shockLossStatus(Analysis.ShockLossStatus.ACTIVE)
                    .recommendation(isTurkish
                            ? "Shock loss başladı, panikleme. Minoxidil'e devam et."
                            : "Shock loss started, do not panic. Continue minoxidil.")
                    .rawAnalysis(isTurkish
                            ? "Shock loss aktif. Ektirilen saçlar dökülüyor, bu beklenen bir durum. 3. aydan sonra büyüme başlayacak."
                            : "Shock loss is active. Transplanted hair is shedding, this is expected. Growth will start after month 3.")
                    .build();
        }

        // 2-3. ay: peak
        if (months <= 3) {
            return AnalysisResult.builder()
                    .densityScore(5.0)
                    .hairlineScore(5.5)
                    .crownScore(4.5)
                    .templeScore(5.0)
                    .stageAssessment(isTurkish
                            ? "Normal - " + months + ". ay, shock loss zirvede"
                            : "Normal for " + months + " months - shock loss peak")
                    .shockLossStatus(Analysis.ShockLossStatus.ACTIVE)
                    .recommendation(isTurkish
                            ? "En zor dönem. 4. aydan itibaren büyüme görülecek."
                            : "Hardest phase. Growth will be visible from month 4.")
                    .rawAnalysis(isTurkish
                            ? "Shock loss devam ediyor. En kötü görüntü dönemi."
                            : "Shock loss continues. Worst appearance phase.")
                    .build();
        }

        // 4-6. ay
        if (months <= 6) {
            return AnalysisResult.builder()
                    .densityScore(6.5)
                    .hairlineScore(7.5)
                    .crownScore(5.5)
                    .templeScore(7.0)
                    .stageAssessment(isTurkish
                            ? "Normal - " + months + ". ay, büyüme başladı"
                            : "Normal for " + months + " months - growth started")
                    .shockLossStatus(Analysis.ShockLossStatus.RESOLVING)
                    .recommendation(isTurkish
                            ? "İyi gidiyorsun, hairline kapanmaya başladı."
                            : "Going well, hairline starting to fill in.")
                    .rawAnalysis(isTurkish
                            ? "Ektirilen greftlerin %40-50'si çıkmış durumda. Hairline netleşiyor, crown hâlâ seyrek."
                            : "40-50% of transplanted grafts have emerged. Hairline becoming clearer, crown still sparse.")
                    .build();
        }

        // 7-12. ay
        if (months <= 12) {
            return AnalysisResult.builder()
                    .densityScore(7.8)
                    .hairlineScore(8.5)
                    .crownScore(7.0)
                    .templeScore(8.0)
                    .stageAssessment(isTurkish
                            ? "Normal - " + months + ". ay"
                            : "Normal for " + months + " months")
                    .shockLossStatus(Analysis.ShockLossStatus.COMPLETED)
                    .recommendation(isTurkish
                            ? "Mükemmel ilerleme. 12. ayda final değerlendirme yapalım."
                            : "Excellent progress. Let's do final evaluation at month 12.")
                    .rawAnalysis(isTurkish
                            ? "Final görüntüye %70-80 ulaşılmış. Crown'da hâlâ doluş bekleniyor."
                            : "70-80% of final appearance reached. Still expecting fill-in at crown.")
                    .build();
        }

        // 12+ ay: final
        return AnalysisResult.builder()
                .densityScore(8.5)
                .hairlineScore(9.0)
                .crownScore(7.5)
                .templeScore(8.5)
                .stageAssessment(isTurkish
                        ? "Final sonuç - " + months + ". ay"
                        : "Final result - " + months + " months")
                .shockLossStatus(Analysis.ShockLossStatus.COMPLETED)
                .recommendation(isTurkish
                        ? "Final sonuç. Mevcut saçlarını korumak için finasteride değerlendir."
                        : "Final result. Consider finasteride to preserve existing hair.")
                .rawAnalysis(isTurkish
                        ? "Final sonuç. Greft tutunum oranı yüksek görünüyor."
                        : "Final result. Graft retention rate appears high.")
                .build();
    }

    private String buildPrompt(Photo photo, User user, String language) {
        String languageInstruction = "tr".equalsIgnoreCase(language)
                ? "TÜRKÇE cevap ver."
                : "Respond in ENGLISH.";

        return String.format("""
                        You are a hair transplant recovery analyst. Analyze this photo.
                        
                        USER CONTEXT:
                        - Transplant date: %s
                        - Days post-op: %d
                        - Months post-op: %d
                        - Total grafts: %d
                        - Method: %s
                        - Zones: %s
                        - Medications: %s
                        - Previous session: %s
                        
                        PHOTO ANGLE: %s
                        
                        %s
                        
                        Respond ONLY in the following JSON format, nothing else:
                        
                        {
                          "densityScore": 0-10 decimal,
                          "hairlineScore": 0-10 decimal,
                          "crownScore": 0-10 decimal,
                          "templeScore": 0-10 decimal,
                          "shockLossStatus": "NONE" or "ACTIVE" or "RESOLVING" or "COMPLETED",
                          "stageAssessment": "short status for this stage",
                          "recommendation": "one actionable recommendation",
                          "rawAnalysis": "detailed explanation (2-3 sentences)"
                        }
                        """,
                user.getTransplantDate(),
                photo.getDaysSinceTransplant(),
                photo.getMonthsSinceTransplant(),
                user.getTotalGrafts(),
                user.getMethod(),
                user.getZones(),
                user.getMedications(),
                user.getPreviousSession() != null && user.getPreviousSession()
                        ? "Yes, " + user.getPreviousGrafts() + " grafts"
                        : "No",
                photo.getAngle(),
                languageInstruction
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

    @Data
    @Builder
    public static class AnalysisResult {
        private Double densityScore;
        private Double hairlineScore;
        private Double crownScore;
        private Double templeScore;
        private Analysis.ShockLossStatus shockLossStatus;
        private String stageAssessment;
        private String recommendation;
        private String rawAnalysis;
    }
}