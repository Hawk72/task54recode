package ru.stepup.task5.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.stepup.task5.model.InstanceModel;
import ru.stepup.task5.service.InstanceService;

@RestController
public class InstanceController {
    private InstanceService service;

    public InstanceController(InstanceService service) {
        this.service = service;
    }

    @PostMapping("corporate-settlement-instance/create")
    public ResponseEntity<Object> handle(@Valid @RequestBody InstanceModel model) {
        service.processModel(model);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getResult());
    }
}
