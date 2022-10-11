import lombok.Builder;

import java.time.LocalDate;

public class DayInfo {
    
    String dateName;
    LocalDate date;
    int remainingDays;
    
    @Builder
    public DayInfo(String dateName, int year, int month, int day, int remainingDays) {
        this.dateName = dateName;
        this.date = LocalDate.of(year, month, day);
        this.remainingDays = remainingDays;
    }
    
    @Override
    public String toString() {
        return "공휴일 이름: " + dateName + "\n일정: "
            + date.getYear() + "년 " + date.getMonthValue() + "월 " + date.getDayOfMonth() + "일"
            + "\n지금으로부터 " + remainingDays + "일 남았습니다.";
    }
}
