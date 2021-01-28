package kr.nutee.auth.Enum;

import java.util.List;

public enum Interest {
    FREE("FREE"),
    DORMITORY("DORMITORY"),
    FOOD("FOOD"),
    LOVE("LOVE"),
    TRIP("TRIP"),
    JOB("JOB"),
    MARKET("MARKET"),
    STUDY("STUDY"),
    PROMOTION("PROMOTION"),
    ANIMAL("ANIMAL"),
    CERTIFICATE("CERTIFICATE(");

    Interest(String interest) {
    }

    public static List<Interest> getAllInterests() {
        return List.of(FREE,DORMITORY,FOOD,LOVE,TRIP,JOB,MARKET,STUDY,PROMOTION,ANIMAL,CERTIFICATE);
    }
}
