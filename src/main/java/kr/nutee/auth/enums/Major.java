package kr.nutee.auth.enums;

public enum Major {
    SOCIOLOGY("사회융합자율학부"),
    SOCIAL_SCIENCE("사회학"),
    ECONOMY("경제학"),
    POLITICS("정치학"),
    BUSINESS_ADMIN("경영학"),
    WELFARE("사회복지학"),
    IT("IT융합자율학부"),
    INFO_COM("정보통신공학"),
    SOFTWARE("소프트웨어공학"),
    COMPUTER_SCIENCE("컴퓨터공학"),
    GLOCAL_IT("글로컬IT"),
    HUMANITIES("인문융합자율학부"),
    ENGLISH("영어학"),
    JAPANESE("일어일본학"),
    CHINESE("중어중국학"),
    MISSION("기독교문화"),
    MEDIA_CONTENTS("미디어콘텐츠융합자율학부"),
    JOURNALISM("신문방송학"),
    DIGITAL_CONTENTS("디지털콘텐츠");
    public String major;

    Major(String major) {
        this.major = major;
    }

}
