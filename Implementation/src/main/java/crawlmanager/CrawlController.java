package crawlmanager;

import crawlmanager.service.DynamicScheduler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@RestController
public class CrawlController {

    DynamicScheduler TaskScheduler;

    public CrawlController (DynamicScheduler TaskScheduler){
        this.TaskScheduler = TaskScheduler;
    }

    @RequestMapping("/")
    public String index() {
        return "Hello welcome the the Crawl-Manager!";
    }

    @RequestMapping("/tasks")
    public String getTaskList() {
        Map<ScheduledFuture, Boolean> futureMap = TaskScheduler.getScheduledTasksList();
        if (futureMap == null) {
            return "Task list is currently empty!";
        }
        else {
            String ResultText = "id: \t \t status: \t \t task \n";
            int counter = 0;
            for (Map.Entry<ScheduledFuture, Boolean> entry : futureMap.entrySet()) {
                ResultText = ResultText + counter + " \t \t " + entry.getValue() + " \t \t " + entry.getKey().toString() + "\n";
                counter++;
            }
            return  ResultText;
        }

    }

    @RequestMapping(value="/task/{frequency}",method= RequestMethod.POST)
    public String addTask(@PathVariable int frequency){
        TaskScheduler.scheduleWithFrequency(frequency);

        return  "Task have been scheduled!";
    }

    @RequestMapping(value="/task/{id}",method= RequestMethod.GET)
    public String getTask( @PathVariable int id){
        Map.Entry<ScheduledFuture, Boolean> futureTask = TaskScheduler.getScheduledTaskById(id);
        if (futureTask == null) {
            return "No task with id (" + id + ") founded!";
        } else {
            return "id: \t \t status: \t \t task \n" + id + " \t \t " + futureTask.getValue() + " \t \t " + futureTask.getKey().toString() + "\n";
        }
    }

    @RequestMapping("/task/cancel/{id}")
    public  String cancelTaskById( @PathVariable int id) {
        int Result = TaskScheduler.cancelById(id);

        return Result == -1 ? "No task with id (" + id + ") founded!" : "Task with id (" + id + ") have  been cancelled!";
    }

    @RequestMapping("/tasks/cancel")
    public  String cancelAllTasks() {
        TaskScheduler.cancelAll();

        return "All Tasks have been cancelled!";
    }
}
