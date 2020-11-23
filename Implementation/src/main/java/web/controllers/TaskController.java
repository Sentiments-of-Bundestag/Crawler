package web.controllers;

import models.Crawler.ControllerRequest;
import models.Crawler.ControllerResponse;
import models.Crawler.PlanedTask;
import models.Crawler.Url;
import org.springframework.web.bind.annotation.*;
import web.service.DynamicScheduler;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@RestController
public class TaskController {

    DynamicScheduler TaskScheduler;

    public TaskController(DynamicScheduler TaskScheduler){
        this.TaskScheduler = TaskScheduler;
    }

    @GetMapping(value = "/", produces = "application/json")
    public ControllerResponse index() {
        ControllerResponse controllerResponse = new ControllerResponse();
        controllerResponse.setTitle("Hello welcome the the Crawl-Manager!");

        return controllerResponse;
    }

    @GetMapping(value = "/tasks", produces = "application/json")
    public ControllerResponse getTaskList() {
        ControllerResponse controllerResponse = new ControllerResponse();
        Map<ScheduledFuture, Boolean> futureMap = TaskScheduler.getScheduledTasksList();
        if (futureMap == null || futureMap.size() == 0) {
            controllerResponse.setTitle("Task list is currently empty!");
        }
        else {
            controllerResponse.setTitle("List of tasks");
            int counter = 0;
            Set<PlanedTask> planedTasks = new LinkedHashSet<>();
            for (Map.Entry<ScheduledFuture, Boolean> entry : futureMap.entrySet()) {
                planedTasks.add(new PlanedTask(counter, "Task Nr. " + counter, null, "ScheduledTask, Planed Status: " + entry.getValue().toString(), String.valueOf(entry.getKey()), null));
                counter++;
            }
            controllerResponse.setBody(planedTasks);
        }
        return controllerResponse;
    }

    @PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
    public ControllerResponse addTask(@RequestBody ControllerRequest request){
        int taskId = TaskScheduler.scheduleDefaultTask(request.getUrl(), request.getFrequency());

        ControllerResponse controllerResponse = new ControllerResponse();
        Map.Entry<ScheduledFuture, Boolean> futureTask = TaskScheduler.getScheduledTaskById(taskId);
        if (futureTask == null) {
            controllerResponse.setTitle("No task with id (" + taskId + ") founded!");
        } else {
            controllerResponse.setTitle("Default single time Task have been scheduled!\n Fixed next time execution task " + request.getFrequency() + " sec");
            Set<PlanedTask> planedTasks = new LinkedHashSet<>();

            planedTasks.add(new PlanedTask(taskId, "Task Nr. " + taskId, new Url("https://www.bundestag.de/services/opendata"), "ScheduledTask, Status: " + futureTask.getValue().toString(), String.valueOf(futureTask.getKey()), null));
            controllerResponse.setBody(planedTasks);
        }
        return  controllerResponse;
    }

    @PostMapping(value = "/task/cron", produces = "application/json")
    public ControllerResponse addDefaultCronTask() {
        int taskId = TaskScheduler.scheduleDefaultCron();
        ControllerResponse controllerResponse = new ControllerResponse();

        if (taskId == -1) {
            controllerResponse.setTitle("Default Cron Task could not been scheduled!");
        } else {
            Map.Entry<ScheduledFuture, Boolean> futureTask = TaskScheduler.getScheduledTaskById(taskId);
            if (futureTask == null) {
                controllerResponse.setTitle("No task with id (" + taskId + ") founded!");
            } else {
                controllerResponse.setTitle("Default Task have been successful scheduled!\n The task will be fired at 11pm every Monday, Tuesday, Wednesday, Thursday and Friday!");
                Set<PlanedTask> planedTasks = new LinkedHashSet<>();

                planedTasks.add(new PlanedTask(taskId, "Task Nr. " + taskId, new Url("https://www.bundestag.de/services/opendata"), "ScheduledTask, Status: " + futureTask.getValue().toString(), String.valueOf(futureTask.getKey()), null));
                controllerResponse.setBody(planedTasks);
            }
        }

        return  controllerResponse;
    }

    @PostMapping(value = "/task/default", produces = "application/json")
    public ControllerResponse addDefaultTask() {
        int taskId = TaskScheduler.scheduleDefaultTask("default", 5);

        ControllerResponse controllerResponse = new ControllerResponse();
        controllerResponse.setTitle("Default Task have been scheduled!\n One time execution task, will start in 5 sec");
        Map.Entry<ScheduledFuture, Boolean> futureTask = TaskScheduler.getScheduledTaskById(taskId);
        if (futureTask == null) {
            controllerResponse.setTitle("No task with id (" + taskId + ") founded!");
        } else {
            controllerResponse.setTitle("Default Task have been successful scheduled!\n The task will be fired at 11pm every Monday, Tuesday, Wednesday, Thursday and Friday!");
            Set<PlanedTask> planedTasks = new LinkedHashSet<>();

            planedTasks.add(new PlanedTask(taskId, "Task Nr. " + taskId, new Url("https://www.bundestag.de/services/opendata"), "ScheduledTask - Crawl, Status: " + futureTask.getValue().toString(), String.valueOf(futureTask.getKey()), null));
            controllerResponse.setBody(planedTasks);
        }
        return  controllerResponse;
    }

    @GetMapping(value = "/task/{id}", produces = "application/json")
    public ControllerResponse getTask( @PathVariable int id){
        ControllerResponse controllerResponse = new ControllerResponse();
        Map.Entry<ScheduledFuture, Boolean> futureTask = TaskScheduler.getScheduledTaskById(id);
        if (futureTask == null) {
            controllerResponse.setTitle("No task with id (" + id + ") founded!");
        } else {
            controllerResponse.setTitle("One task founded!");
            Set<PlanedTask> planedTasks = new LinkedHashSet<>();

            planedTasks.add(new PlanedTask(id, "Task Nr. " + id, null, "ScheduledTask, Status: " + futureTask.getValue().toString(), String.valueOf(futureTask.getKey()), null));
            controllerResponse.setBody(planedTasks);
        }

        return  controllerResponse;
    }

    @GetMapping(value = "/task/cancel/{id}", produces = "application/json")
    public  ControllerResponse cancelTaskById( @PathVariable int id) {
        int Result = TaskScheduler.cancelById(id);
        ControllerResponse controllerResponse = new ControllerResponse();

        if (Result == -1) {
            controllerResponse.setTitle("No task with id (" + id + ") founded!");
        } else {
            controllerResponse.setTitle("Task with id (" + id + ") have  been cancelled!");
        }

        return controllerResponse;
    }

    @GetMapping(value="/tasks/cancel", produces = "application/json")
    public  ControllerResponse cancelAllTasks() {
        TaskScheduler.cancelAll();
        ControllerResponse controllerResponse = new ControllerResponse();
        controllerResponse.setTitle("All tasks canceled");

        return controllerResponse;
    }
}
