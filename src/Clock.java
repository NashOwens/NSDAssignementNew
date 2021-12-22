import java.time.LocalDate;

public class Clock {

        public Clock() {
        }

        // return the current time
        public synchronized String tick() {
                int hour = java.time.LocalTime.now().getHour();
                int mins = java.time.LocalTime.now().getMinute();
                int seconds = java.time.LocalTime.now().getSecond();
                int day = LocalDate.now().getDayOfYear();
                return hour + ":" + mins + ":" + seconds + "// " +day;
        }
}
