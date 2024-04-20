package ru.stepup.task5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Task5Application {

	public static void main(String[] args) {
		SpringApplication.run(Task5Application.class, args);
//		ApplicationContext ctx=SpringApplication.run(Task5Application.class, args);
//		AccountTypeRepo accountTypeRepo=ctx.getBean(AccountTypeRepo.class);
//		AccountType accountType=new AccountType("test1fromApp");
//		accountTypeRepo.saveAndFlush(accountType);
	}

}
