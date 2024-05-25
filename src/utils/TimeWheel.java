package utils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: TimeWheel
 * Description: 时间轮
 *
 * @Author agility6
 * @Create 2024/5/25 22:46
 * @Version: 1.0
 */
public class TimeWheel {

    /**
     * 时间轮的槽数量
     */
    private final int slotNum;

    /**
     * 每个槽的时间间隔
     */
    private final Duration interval;

    /**
     * 存放任务的槽列表，每个槽是一个双向链表
     */
    private final List<LinkedList<TaskElement>> slots;

    /**
     * 快速定位任务的键到任务元素的映射
     */
    private final Map<String, TaskElement> keyToTask;

    /**
     * 调度器
     */
    private final ScheduledExecutorService scheduler;

    /**
     * 当前时间轮指向的位置
     */
    private int curSlot;

    /**
     * 控制时间轮的计时器
     */
    private ScheduledFuture<?> ticker;

    public TimeWheel(int slotNum, Duration interval) {
        if (slotNum <= 0) {
            slotNum = 10;
        }
        if (interval.isNegative() || interval.isZero()) {
            interval = Duration.ofSeconds(1);
        }
        this.slotNum = slotNum;
        this.interval = interval;
        this.slots = new LinkedList<>();
        // 初始化将每一个位置都设置为双向链表
        for (int i = 0; i < slotNum; i++) {
            this.slots.add(new LinkedList<>());
        }
        this.keyToTask = new HashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.curSlot = 0;

        // 开启时间轮
        start();
    }

    /**
     * 新增任务
     *
     * @param key
     * @param task
     * @param executeAt
     */
    public void addTask(String key, Runnable task, Instant executeAt) {
        int[] posAndCycle = getPosAndCycle(executeAt);
        int pos = posAndCycle[0];
        int cycle = posAndCycle[1];
        TaskElement taskElement = new TaskElement(key, task, pos, cycle);

        synchronized (this) {
            LinkedList<TaskElement> slot = slots.get(pos);
            slot.add(taskElement);
            keyToTask.put(key, taskElement);
        }
    }

    /**
     * 删除任务
     *
     * @param key
     */
    public void removeTask(String key) {
        synchronized (this) {
            TaskElement taskElement = keyToTask.get(key);
            if (taskElement != null) {
                slots.get(taskElement.getPos()).remove(taskElement);
            }
        }
    }

    /**
     * 停止时间轮
     */
    public void stop() {
        if (ticker != null) {
            ticker.cancel(true);
            scheduler.shutdown();
        }
    }

    /* ------------- private ------------- */

    private void start() {
        ticker = scheduler.scheduleAtFixedRate(this::tick, interval.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS);
    }


    /**
     * 根据executeAt求所在时间轮的位置和循环次数
     * <p>
     * 例如：
     * slotNum = 10, interval = 1 second, 当前slot = 0, 任务在25s执行
     * delay = 25000ms
     * cycle = 25000 / (10 * 1000ms) = 2
     * pst = (0 + 25000 / 10000) % 10 = 5
     *
     * @param executeAt
     * @return
     */
    private int[] getPosAndCycle(Instant executeAt) {
        long delay = Duration.between(Instant.now(), executeAt).toMillis();
        int cycle = (int) (delay / (slotNum * interval.toMillis()));
        int pos = (curSlot + (int) (delay / interval.toMillis()) % slotNum);
        return new int[]{pos, cycle};
    }

    /**
     * 执行当前指向时间的任务
     */
    private void tick() {
        LinkedList<TaskElement> slot;
        synchronized (this) {
            // 获取当前位置上的任务队列
            slot = slots.get(curSlot);
            // 后移
            curSlot = (curSlot + 1) % slotNum;
        }
        // 执行任务队列
        execute(slot);
    }

    /**
     * 执行任务队列
     *
     * @param slot
     */
    private void execute(List<TaskElement> slot) {
        for (TaskElement taskElement : new LinkedList<>(slot)) {
            if (taskElement.cycle > 0) { // 为达到执行的条件
                taskElement.cycle--;
            } else {
                slot.remove(taskElement);
                keyToTask.remove(taskElement.getKey());
                scheduler.submit(taskElement.getTask());
            }
        }
    }

}
