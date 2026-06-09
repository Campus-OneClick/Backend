package com.example.backend.RoomUsage;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room-usage")
public class RoomUsageController {

    private final RoomUsageService roomUsageService;

    @GetMapping("/status/{roomName}")
    public Map<String, String> getStatus(@PathVariable String roomName) {
        return roomUsageService.getStatusForRoom(roomName);
    }
}
