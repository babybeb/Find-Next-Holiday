import lombok.Builder;

import java.time.LocalDate;

public class DayInfo {
    String dateName;
    LocalDate date;

    @Builder
    public DayInfo(String dateName, int year, int month, int day) {
        this.dateName = dateName;
        this.date = LocalDate.of(year, month, day);
    }

    @Override
    public String toString() {
        return "공휴일 이름: " + dateName + "\n일정: "
                + date.getYear() + "년 " + date.getMonth() + "월 " + date.getDayOfMonth() + "일";
    }
}
