package com.example.backend.Classroom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody ClassroomEntity classroomEntity) {
        try {
            ClassroomEntity saved = classroomService.create(classroomEntity);
            return Map.of(
                    "success", true,
                    "data", saved
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

    @GetMapping
    public List<ClassroomEntity> getAll() {
        return classroomService.findAll();
    }

    @GetMapping("/{id}")
    public ClassroomEntity getOne(@PathVariable String id) {
        return classroomService.findById(id);
    }
}