package com.example.backend.Schedule;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public Object create(@RequestBody ScheduleEntity scheduleEntity) {
        return scheduleService.createSchedule(scheduleEntity);
    }

    @GetMapping
    public List<ScheduleEntity> getAll() {
        return scheduleService.findAll();
    }

    @GetMapping("/{id}")
    public ScheduleEntity getOne(@PathVariable Long id) {
        return scheduleService.findById(id);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody ScheduleEntity scheduleEntity) {
        return scheduleService.updateSchedule(id, scheduleEntity);
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) {
        return scheduleService.deleteSchedule(id);
    }
}