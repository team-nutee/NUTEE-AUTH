package kr.nutee.auth.enums;

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
