package kr.nutee.auth.enums;

import lombok.Getter;

@Getter
public enum Interest {
    FREE("자유"),
    DORMITORY("기숙사"),
    FOOD("음식"),
    LOVE("연애"),
    TRIP("여행"),
    JOB("취업"),
    MARKET("장터"),
    STUDY("스터디"),
    PROMOTION("홍보"),
    ANIMAL("반려동물"),
    CERTIFICATE("자격증");
    public String interest;

    Interest(String interest) {
        this.interest = interest;
    }

    public boolean contains(String interest) {
        for (Interest value : Interest.values()) {
            if (value.interest.equals(interest)) {
                return true;
            }
        }
        return false;
    }
}
