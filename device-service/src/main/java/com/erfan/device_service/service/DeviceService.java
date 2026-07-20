package com.erfan.device_service.service;


import com.erfan.device_service.dto.DeviceDto;
import com.erfan.device_service.entity.Device;
import com.erfan.device_service.exception.DeviceNotFoundException;
import com.erfan.device_service.repository.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceDto getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(()-> new DeviceNotFoundException("Device not found"));
        return mapToDto(device);
    }

    public DeviceDto createDevice(DeviceDto deviceDto) {
        Device device = new Device();
        device.setName(deviceDto.getName());
        device.setType(deviceDto.getType());
        device.setLocation(deviceDto.getLocation());
        device.setUserId(deviceDto.getUserId());
         final Device savedDevice = deviceRepository.save(device);
         return mapToDto(savedDevice);
    }

    public DeviceDto updateDevice(Long id, DeviceDto deviceDto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found"));
        device.setName(deviceDto.getName());
        device.setType(deviceDto.getType());
        device.setLocation(deviceDto.getLocation());
        device.setUserId(deviceDto.getUserId());
        final Device updatedDevice = deviceRepository.save(device);
        return mapToDto(updatedDevice);
    }

    public void delectDevice(Long id) {
        if(!deviceRepository.existsById(id)){
            throw new DeviceNotFoundException("Device not found with id "+ id);
        }
        deviceRepository.deleteById(id);
    }

    private DeviceDto mapToDto(Device device) {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(device.getId());
        deviceDto.setName(device.getName());
        deviceDto.setType(device.getType());
        deviceDto.setLocation(device.getLocation());
        deviceDto.setUserId(device.getUserId());
        return deviceDto;
    }
}
