package com.hairtrack.transformation.service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public class MilestoneDefinitions {

    @Data
    @AllArgsConstructor
    public static class Milestone {
        private int dayMark;
        private String titleTr;
        private String titleEn;
        private String messageTr;
        private String messageEn;
    }

    public static final List<Milestone> MILESTONES = List.of(
            new Milestone(
                    3,
                    "3. gün - İlk yıkama",
                    "Day 3 - First wash",
                    "İlk yıkama dönemine giriyorsun. Kliniğinin verdiği talimatları takip et.",
                    "You're entering the first wash period. Follow your clinic's instructions."
            ),
            new Milestone(
                    7,
                    "1. hafta tamamlandı",
                    "Week 1 completed",
                    "İlk hafta geçti. Kabuklar oluştu, sakın kaşıma. Şişlik azalmış olmalı.",
                    "First week is done. Scabs have formed, don't scratch. Swelling should be reducing."
            ),
            new Milestone(
                    14,
                    "2. hafta - Kabuklar dökülüyor",
                    "Week 2 - Scabs falling",
                    "Kabukların büyük kısmı bu hafta düşer. Nazikçe yıkama yap, zorla koparma.",
                    "Most scabs fall off this week. Wash gently, don't force them off."
            ),
            new Milestone(
                    30,
                    "1. ay - Shock loss başlıyor",
                    "Month 1 - Shock loss starts",
                    "Ektirilen saçların dökülmesi normal. Panikleme, bu beklenen bir aşama. Minoxidil'e devam et.",
                    "Shedding of transplanted hair is normal. Don't panic, this is expected. Continue minoxidil."
            ),
            new Milestone(
                    60,
                    "2. ay - Shock loss devam ediyor",
                    "Month 2 - Shock loss continues",
                    "En zor görüntü dönemi. Aynaya günde 5 kere bakmayı bırak. Süreç işliyor.",
                    "Worst visual phase. Stop checking the mirror 5 times a day. The process is working."
            ),
            new Milestone(
                    90,
                    "3. ay - Dönüm noktası",
                    "Month 3 - Turning point",
                    "Shock loss zirveye ulaştı. Bundan sonra büyüme görmeye başlayacaksın.",
                    "Shock loss peaked. From now on you'll start seeing growth."
            ),
            new Milestone(
                    120,
                    "4. ay - İlk büyüme",
                    "Month 4 - First growth",
                    "Yeni saçlar çıkmaya başladı. İnce ve renksiz olabilir, normal. Gerçek büyüme şimdi başlıyor.",
                    "New hair starting to emerge. May be thin and colorless, that's normal. Real growth begins now."
            ),
            new Milestone(
                    180,
                    "6. ay - Yarıya geldin",
                    "Month 6 - Halfway there",
                    "Final görüntünün %40-50'sine ulaştın. Hairline netleşmeye başladı, crown hala doluyor.",
                    "Reached 40-50% of final appearance. Hairline becoming clearer, crown still filling."
            ),
            new Milestone(
                    270,
                    "9. ay - Önemli gelişme",
                    "Month 9 - Major progress",
                    "Saçlar belirgin şekilde kalınlaştı. Final sonucun %70-80'i görünür halde.",
                    "Hair has noticeably thickened. 70-80% of final result is visible."
            ),
            new Milestone(
                    365,
                    "1. yıl tamamlandı",
                    "Year 1 completed",
                    "Tebrikler! Final sonucun büyük kısmı belli oldu. Crown bölgesi 12-18. ay arası dolmaya devam edecek.",
                    "Congratulations! Most of final result is visible. Crown area will continue filling between months 12-18."
            ),
            new Milestone(
                    540,
                    "18. ay - Final sonuç",
                    "Month 18 - Final result",
                    "Final sonuç bu. Mevcut saçlarını koruman için finasteride değerlendir.",
                    "This is the final result. Consider finasteride to preserve existing hair."
            )
    );
}
