import utils.TimeWheel;

import javax.swing.*;
import java.time.Duration;
import java.time.Instant;

/**
 * ClassName: ${NAME}
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/25 22:41
 * @Version: 1.0
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");

        TimeWheel timeWheel = new TimeWheel(10, Duration.ofSeconds(1));

        // 添加两个任务
        timeWheel.addTask("task1", () -> System.out.println("Task 1 executed"), Instant.now().plusSeconds(5));
        timeWheel.addTask("task2", () -> System.out.println("Task 2 executed"), Instant.now().plusSeconds(10));

        Thread.sleep(20000L);
        timeWheel.stop();
    }
}