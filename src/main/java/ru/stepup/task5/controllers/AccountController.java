package ru.stepup.task5.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.stepup.task5.model.AccountModel;
import ru.stepup.task5.service.AccountService;

@RestController
public class AccountController {
    private AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("corporate-settlement-account/create")
    public ResponseEntity<Object> handle(@Valid @RequestBody AccountModel model) {
        System.out.println("*** Зашли");
        service.processModel(model);
        System.out.println("*** После");
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getResult());
    }
}

