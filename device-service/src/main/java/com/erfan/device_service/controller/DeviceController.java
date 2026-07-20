package com.erfan.device_service.controller;

import com.erfan.device_service.dto.DeviceDto;
import com.erfan.device_service.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceController {
    private DeviceService deviceService;


    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable Long id){
        DeviceDto device=deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }

    @PostMapping("/create")
    public ResponseEntity<DeviceDto> createDevice(@RequestBody DeviceDto deviceDto){
        DeviceDto createDevice=deviceService.createDevice(deviceDto);
        return ResponseEntity.ok(createDevice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDto> updateDeviceById(
            @PathVariable Long id, @RequestBody DeviceDto deviceDto){
        DeviceDto updateDevice=deviceService.updateDevice(id,deviceDto);
        return ResponseEntity.ok(deviceDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceById(@PathVariable Long id){
        deviceService.delectDevice(id);
        return ResponseEntity.noContent().build();
    }
}
