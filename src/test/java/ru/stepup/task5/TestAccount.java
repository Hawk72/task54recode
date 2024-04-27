package ru.stepup.task5;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.stepup.task5.entity.Account;
import ru.stepup.task5.entity.AccountPool;
import ru.stepup.task5.entity.TppProductRegister;
import ru.stepup.task5.entity.TppRefProductRegisterType;
import ru.stepup.task5.model.AccountModel;
import ru.stepup.task5.repo.*;
import ru.stepup.task5.service.AccountService;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAccount {
    private ApplicationContext ctx;
    @LocalServerPort
    private Integer port;

    public TestAccount(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Sql(scripts = "/pg1_create.sql")
    @Sql(scripts = "/pg2_insert.sql")

    @Test
    @Order(1)
    @DisplayName("Проверка срабатывания ошибки при незаполнении обязательных параметров")
    public void checkRequired() {
        AccountModel model = new AccountModel();
        Validator validator = ctx.getBean(Validator.class);
        Set<ConstraintViolation<AccountModel>> violations = validator.validate(model);
        Assertions.assertEquals(1, violations.size());
        model.setInstanceId(1L);
        violations = validator.validate(model);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @Order(2)
    @DisplayName("ПроScript_insertверка срабатывания ошибки при наличии дублей")
    public void checkDouble() {
        AccountModel model = new AccountModel();
        model.setInstanceId(1L);
        model.setRegistryTypeCode("03.012.002_47533_ComSoLd");

        TppRefProductRegisterType tppRefProductRegisterType = new TppRefProductRegisterType();
        tppRefProductRegisterType.setValue(model.getRegistryTypeCode());

        TppProductRegister tppProductRegister = new TppProductRegister();
        tppProductRegister.setTppRefProductRegisterType(tppRefProductRegisterType);

        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        Mockito.when(mockTppProductRegisterRepo.findByProductId(model.getInstanceId())).thenReturn(List.of(tppProductRegister));

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        AccountService service = new AccountService(
                mockTppProductRegisterRepo, mockTppRefProductRegisterTypeRepo, mockAccountPoolRepo);

        Assertions.assertThrows(IllegalArgumentException.class, ()->service.processModel(model));    }

    @Test
    @Order(3)
    @DisplayName("Проверка срабатывания ошибки при отсутствии подходящих записей в tpp_ref_product_register_type")
    public void checkNotFoundRegisterType() {
        AccountModel model = new AccountModel();
        model.setInstanceId(1L);
        model.setRegistryTypeCode("03.012.002_47533_ComSoLd");

        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByValue(model.getRegistryTypeCode())).thenReturn(null);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        AccountService service = new AccountService(mockTppProductRegisterRepo, mockTppRefProductRegisterTypeRepo, mockAccountPoolRepo);

        Assertions.assertThrows(NotFoundException.class, ()->service.processModel(model));    }

    @Test
    @Order(4)
    @DisplayName("Проверка срабатывания ошибки при отсутствии подходящих записей в account_pool")
    public void checkNotFoundAccountPool() {
        AccountModel model = new AccountModel();
        model.setInstanceId(1L);

        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByValue(model.getRegistryTypeCode())).thenReturn(new TppRefProductRegisterType());
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);
        Mockito.when(mockAccountPoolRepo.findAccountPool(null, null, null, null, null)).thenReturn(null);

        AccountService service = new AccountService(mockTppProductRegisterRepo, mockTppRefProductRegisterTypeRepo, mockAccountPoolRepo);
        Assertions.assertThrows(NotFoundException.class, ()->service.processModel(model));
    }

    @Test
    @Order(5)
    @DisplayName("Проверка положительного срабатывания processModel")
    public void checkProcessModel() {
        AccountModel model = new AccountModel();
        model.setInstanceId(1L);
        TppProductRegister tppProductRegister = new TppProductRegister();

        AccountPool accountPool = new AccountPool();
        accountPool.setAccounts(List.of(new Account()));

        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        Mockito.when(mockTppProductRegisterRepo.save(tppProductRegister)).thenReturn(tppProductRegister);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByValue(model.getRegistryTypeCode())).thenReturn(new TppRefProductRegisterType());
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);
        Mockito.when(
                mockAccountPoolRepo.findAccountPool(
                null, null, null, null, null
                )
        ).thenReturn(accountPool);

        AccountService service = new AccountService(mockTppProductRegisterRepo, mockTppRefProductRegisterTypeRepo, mockAccountPoolRepo);

        Assertions.assertDoesNotThrow(()->service.processModel(model));
    }

    @Test
    @Order(6)
    @DisplayName("Интеграционный тест")
    public void checkIntegration() {
        TppProductRegisterRepo tppProductRegisterRepo = ctx.getBean(TppProductRegisterRepo.class);
        tppProductRegisterRepo.deleteAll();
        AccountModel model = new AccountModel();
        model.setInstanceId(1L);
        model.setRegistryTypeCode("03.012.002_47533_ComSoLd");
        model.setCurrencyCode("800");
        model.setBranchCode("0022");
        model.setPriorityCode("00");
        model.setMdmCode("15");

        AccountService service = ctx.getBean(AccountService.class);
        Assertions.assertDoesNotThrow(()->service.processModel(model));
        Assertions.assertEquals(1, tppProductRegisterRepo.count());
    }

    @Test
    @Order(7)
    @DisplayName("Проверка запросов TppRefProductRegisterTypeRepo")
    public void checkTppRefProductRegisterTypeRepo() {
        TppRefProductRegisterTypeRepo repo = ctx.getBean(TppRefProductRegisterTypeRepo.class);
        Assertions.assertEquals("public", repo.getSchema());
        Assertions.assertEquals("tpp_ref_product_register_type", repo.getTableName());
    }

    @Test
    @Order(8)
    @DisplayName("Проверка запросов TppProductRegisterRepo")
    public void checkTppProductRegisterRepo() {
        TppProductRegisterRepo tppProductRegisterRepo = ctx.getBean(TppProductRegisterRepo.class);
        tppProductRegisterRepo.deleteAll();
        AccountModel model = new AccountModel();
        model.setInstanceId(1L);
        model.setRegistryTypeCode("03.012.002_47533_ComSoLd");
        model.setCurrencyCode("800");
        model.setBranchCode("0022");
        model.setPriorityCode("00");
        model.setMdmCode("15");

        AccountService service = ctx.getBean(AccountService.class);
        service.processModel(model);

        List<TppProductRegister> tppProductRegisterList = tppProductRegisterRepo.findByProductId(0L);
        Assertions.assertEquals(0, tppProductRegisterList.size());

        tppProductRegisterList = tppProductRegisterRepo.findByProductId(1L);
        Assertions.assertEquals(1, tppProductRegisterList.size());
    }

    @Test
    @Order(9)
    @DisplayName("Проверка запросов AccountPoolRepo")
    public void checkAccountPoolRepo() {
        AccountPoolRepo repo = ctx.getBean(AccountPoolRepo.class);
        AccountPool accountPool = repo.findAccountPool("0020", null, null, null, null);
        Assertions.assertTrue(accountPool == null);

        accountPool = repo.findAccountPool("0021", null, null, null, null);
        Assertions.assertTrue(accountPool != null);
    }

    @Test
    @Order(10)
    @DisplayName("Тест rest api")
    public void checkRestApi() {
        TppProductRegisterRepo tppProductRegisterRepo = ctx.getBean(TppProductRegisterRepo.class);
        tppProductRegisterRepo.deleteAll();
        given()
                .contentType(ContentType.JSON)
                .with()
                .body(new HashMap<String, Object>() {{
                    put("instanceId", "1");
                    put("registryTypeCode", "03.012.002_47533_ComSoLd");
                    put("accountType", "A");
                    put("currencyCode", "800");
                    put("branchCode", "0022");
                    put("priorityCode", "00");
                    put("mdmCode", "15");
                    put("clientCode", "AAA");
                    put("trainRegion", "AAA");
                    put("counter", "AAA");
                    put("salesCode", "AAA");
                }})
                .when()
                .post("/corporate-settlement-account/create")
                .then()
                .statusCode(200)
        ;

        Assertions.assertEquals(1, tppProductRegisterRepo.count());
    }

}
