package utils;

/**
 * ClassName: TaskElement
 * Description: 代表一个任务，包含任务函数、所在位置、循环次数和键
 *
 * @Author agility6
 * @Create 2024/5/25 22:43
 * @Version: 1.0
 */
public class TaskElement {

    /**
     * 键
     */
    private String key;

    /**
     * 任务
     */
    private Runnable task;

    /**
     * 所在位置
     */
    private int pos;

    /**
     * 循环次数
     */
    int cycle;

    public TaskElement(String key, Runnable task, int pos, int cycle) {
        this.key = key;
        this.task = task;
        this.pos = pos;
        this.cycle = cycle;
    }

    /* -------------- Get/Set --------------*/
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
