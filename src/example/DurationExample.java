package example;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * ClassName: DurationExample
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/26 0:33
 * @Version: 1.0
 */
public class DurationExample {
    public static void main(String[] args) {

        // 创建一个持续时间为5秒的Duration实例
        Duration duration = Duration.ofSeconds(5);
        System.out.println("Duration of 5 seconds: " + duration);

        // 通过两个Instant对象指向的差异创建Duration
        Instant start = Instant.now();
        Instant end = start.plus(10, ChronoUnit.SECONDS);
        Duration between = Duration.between(start, end);
        System.out.println("Duration between start and end: " + between);

        // 增加或减少时间
        Duration tenMinutes = Duration.ofMinutes(10);
        Duration fiveMinutesLater = tenMinutes.plusMinutes(5);
        System.out.println("Duration of 15 minutes: " + fiveMinutesLater);

        // 获取Duration中的时间单位
        long seconds = duration.getSeconds();
        long millis = duration.toMillis();
        System.out.println("Seconds: " + seconds);
        System.out.println("Milliseconds: " + millis);

        // 解析字符串形式的Duration
        Duration parsedDuration = Duration.parse("PT20.345S"); // 20.345 seconds
        System.out.println("Parsed duration: " + parsedDuration);
    }
}
