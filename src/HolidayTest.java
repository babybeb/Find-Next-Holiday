import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class HolidayTest {
    
    @org.junit.jupiter.api.Test
    void calculateDate() {
        String date = "20221011";
        Holiday holiday = new Holiday();
        LocalDate localDate = holiday.calculateDate(date);
        assertEquals(2022, localDate.getYear());
        assertEquals(10, localDate.getMonthValue());
        assertEquals(11, localDate.getDayOfMonth());
    }
}