
public class Clock {

        public Clock() {
        }

        // return the current time
        public synchronized String tick() {
                int hour = java.time.LocalTime.now().getHour();
                int mins = java.time.LocalTime.now().getMinute();
                int seconds = java.time.LocalTime.now().getSecond();
                return hour + ":" + mins + ":" + seconds;
        }
}
