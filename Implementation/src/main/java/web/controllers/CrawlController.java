package web.controllers;

import models.Crawler.ControllerRequest;
import models.Crawler.ControllerResponse;
import models.Crawler.PlanedTask;
import org.springframework.web.bind.annotation.*;
import web.service.DynamicScheduler;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@RestController
public class CrawlController {

    DynamicScheduler TaskScheduler;

    public CrawlController (DynamicScheduler TaskScheduler){
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
            String ResultText = "id: \t \t status: \t \t task \n";
            int counter = 0;
            Set<PlanedTask> planedTasks = new LinkedHashSet<>();
            for (Map.Entry<ScheduledFuture, Boolean> entry : futureMap.entrySet()) {
                planedTasks.add(new PlanedTask(counter, "Task Nr. " + counter, null, "ScheduledTask", entry.getKey().toString(), null));
                counter++;
            }
            controllerResponse.setBody(planedTasks);
        }
        return controllerResponse;
    }

    @PostMapping(value = "/task", consumes = "application/json", produces = "application/json")
    public ControllerResponse addTask(@RequestBody ControllerRequest request){
        int taskId = TaskScheduler.scheduleWithFrequency(request.getFrequency());
        ControllerResponse controllerResponse = new ControllerResponse();
        controllerResponse.setTitle("New task created!");
        Set<PlanedTask> planedTasks = new LinkedHashSet<>();
        planedTasks.add(new PlanedTask(taskId, "Task Nr. " + taskId, null, "ScheduledTask", "Frequency = " + request.getFrequency(), null));
        controllerResponse.setBody(planedTasks);
        return  controllerResponse;
    }

    @PostMapping(value = "/task/default", produces = "application/json")
    public ControllerResponse addDefaultTask() {
        int taskId = TaskScheduler.scheduleDefaultTask();

        ControllerResponse controllerResponse = new ControllerResponse();
        controllerResponse.setTitle("Default Task have been scheduled!");
        Set<PlanedTask> planedTasks = new LinkedHashSet<>();
        planedTasks.add(new PlanedTask(taskId, "Task Nr. " + taskId, null, "ScheduledTask", " One time execution task, will start in 5 sec.", null));
        controllerResponse.setBody(planedTasks);
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

            planedTasks.add(new PlanedTask(id, "Task Nr. " + id, null, "ScheduledTask, Status: " + futureTask.getValue().toString(), futureTask.getKey().toString(), null));
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
