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
import ru.stepup.task5.entity.*;
import ru.stepup.task5.model.Arrangement;
import ru.stepup.task5.model.InstanceModel;
import ru.stepup.task5.model.enums.ProductType;
import ru.stepup.task5.repo.*;
import ru.stepup.task5.service.InstanceService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestInstance {
    private ApplicationContext ctx;
    @LocalServerPort
    private Integer port;

    public TestInstance(ApplicationContext ctx) {
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
        InstanceModel model = new InstanceModel();
        Validator validator = ctx.getBean(Validator.class);

        Arrangement arrangement = new Arrangement();
        model.getInstanceArrangement().add(arrangement);

        Set<ConstraintViolation<InstanceModel>> violations = validator.validate(model);
        Assertions.assertEquals(13, violations.size());

        model.setProductType(ProductType.НСО);
        violations = validator.validate(model);
        Assertions.assertEquals(12, violations.size());

        model.setProductCode("03.012.002");
        violations = validator.validate(model);
        Assertions.assertEquals(11, violations.size());

        model.setRegisterType("03.012.002_47533_ComSoLd");
        violations = validator.validate(model);
        Assertions.assertEquals(10, violations.size());

        model.setMdmCode("15");
        violations = validator.validate(model);
        Assertions.assertEquals(9, violations.size());

        model.setContractNumber("4");
        violations = validator.validate(model);
        Assertions.assertEquals(8, violations.size());

        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        violations = validator.validate(model);
        Assertions.assertEquals(7, violations.size());

        model.setPriority(1);
        violations = validator.validate(model);
        Assertions.assertEquals(6, violations.size());

        model.setContractId(1L);
        violations = validator.validate(model);
        Assertions.assertEquals(5, violations.size());

        model.setBranchCode("0022");
        violations = validator.validate(model);
        Assertions.assertEquals(4, violations.size());

        model.setIsoCurrencyCode("800");
        violations = validator.validate(model);
        Assertions.assertEquals(3, violations.size());

        model.setUrgencyCode("00");
        violations = validator.validate(model);
        Assertions.assertEquals(2, violations.size());

        arrangement.setNumber("1");
        violations = validator.validate(model);
        Assertions.assertEquals(1, violations.size());

        try {
            arrangement.setOpeningDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        violations = validator.validate(model);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    @Order(2)
    @DisplayName("Проверка срабарывания ошибки при наличии дублей TppProduct")
    public void checkDoubleProduct() {
        InstanceModel model = new InstanceModel();

        TppProductRepo mockTppProductRepo = Mockito.mock(TppProductRepo.class);
        AgreementRepo mockAgreementRepo = Mockito.mock(AgreementRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        TppRefProductClassRepo mockTppRefProductClassRepo = Mockito.mock(TppRefProductClassRepo.class);
        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        InstanceService service = new InstanceService(
                mockTppProductRepo
                , mockAgreementRepo
                , mockTppRefProductRegisterTypeRepo
                , mockTppRefProductClassRepo
                , mockTppProductRegisterRepo
                , mockAccountPoolRepo
        );

        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");

        Mockito.when(mockTppProductRepo.findByNumber(model.getContractNumber())).thenReturn(List.of(new TppProduct()));
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.processModel(model));
    }

    @Test
    @Order(3)
    @DisplayName("Проверка срабатывания ошибки при наличии дублей Agreement")
    public void checkDoubleAgreement() {
        InstanceModel model = new InstanceModel();

        TppProductRepo mockTppProductRepo = Mockito.mock(TppProductRepo.class);
        AgreementRepo mockAgreementRepo = Mockito.mock(AgreementRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        TppRefProductClassRepo mockTppRefProductClassRepo = Mockito.mock(TppRefProductClassRepo.class);
        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        InstanceService service = new InstanceService(mockTppProductRepo, mockAgreementRepo, mockTppRefProductRegisterTypeRepo, mockTppRefProductClassRepo, mockTppProductRegisterRepo, mockAccountPoolRepo);

        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");
        Arrangement arrangement = new Arrangement();
        arrangement.setNumber("1");
        try {
            arrangement.setOpeningDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.getInstanceArrangement().add(arrangement);

        Mockito.when(mockAgreementRepo.findByNumber(arrangement.getNumber())).thenReturn(List.of(new Agreement()));
        Assertions.assertThrows(IllegalArgumentException.class, ()->service.processModel(model));
    }

    @Test
    @Order(4)
    @DisplayName("Проверка срабатывания ошибки при отсутствии подходящих записей в tpp_ref_product_register_type")
    public void checkNotFoundregisterType() {
        InstanceModel model = new InstanceModel();

        TppProductRepo mockTppProductRepo = Mockito.mock(TppProductRepo.class);
        AgreementRepo mockAgreementRepo = Mockito.mock(AgreementRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        TppRefProductClassRepo mockTppRefProductClassRepo = Mockito.mock(TppRefProductClassRepo.class);
        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        InstanceService service = new InstanceService(mockTppProductRepo, mockAgreementRepo, mockTppRefProductRegisterTypeRepo, mockTppRefProductClassRepo, mockTppProductRegisterRepo, mockAccountPoolRepo);

        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");

        Assertions.assertThrows(NotFoundException.class, ()->service.processModel(model));

        TppRefProductClass productClass = new TppRefProductClass();
        Mockito.when(mockTppRefProductClassRepo.findByValue(model.getProductCode())).thenReturn(productClass);

        Assertions.assertThrows(NotFoundException.class, ()->service.processModel(model));

        List<TppRefProductRegisterType> registerTypes = new ArrayList<>();
        TppRefProductRegisterType registerType = new TppRefProductRegisterType();
        registerType.setTppRefAccountType(new TppRefAccountType());
        registerTypes.add(registerType);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByTppRefProductClass(productClass)).thenReturn(registerTypes);

        Assertions.assertThrows(NotFoundException.class, ()->service.processModel(model));
    }

    @Test
    @Order(5)
    @DisplayName("Проверка срабатывания ошибки при отсутствии подходящих записей в account_pool")
    public void checkNotFoundAccountPool() {
        InstanceModel model = new InstanceModel();

        TppProductRepo mockTppProductRepo = Mockito.mock(TppProductRepo.class);
        AgreementRepo mockAgreementRepo = Mockito.mock(AgreementRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        TppRefProductClassRepo mockTppRefProductClassRepo = Mockito.mock(TppRefProductClassRepo.class);
        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        InstanceService service = new InstanceService(mockTppProductRepo, mockAgreementRepo, mockTppRefProductRegisterTypeRepo, mockTppRefProductClassRepo, mockTppProductRegisterRepo, mockAccountPoolRepo);

        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");

        TppRefProductClass productClass = new TppRefProductClass();
        List<TppRefProductRegisterType> registerTypes = new ArrayList<>();
        TppRefProductRegisterType registerType = new TppRefProductRegisterType();
        TppRefAccountType accountType = new TppRefAccountType();
        accountType.setValue("Клиентский");
        registerType.setTppRefAccountType(accountType);
        registerTypes.add(registerType);

        Mockito.when(mockTppRefProductClassRepo.findByValue(model.getProductCode())).thenReturn(productClass);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByTppRefProductClass(productClass)).thenReturn(registerTypes);

        Mockito.when(mockAccountPoolRepo.findAccountPool(null, null, null, null, null)).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, ()->service.processModel(model));
    }

    @Test
    @Order(6)
    @DisplayName("Проверка срабатывания ошибки при отсутствии подходящих записей в tpp_product")
    public void checkNotFoundProduct() {
        InstanceModel model = new InstanceModel();

        TppProductRepo mockTppProductRepo = Mockito.mock(TppProductRepo.class);
        AgreementRepo mockAgreementRepo = Mockito.mock(AgreementRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        TppRefProductClassRepo mockTppRefProductClassRepo = Mockito.mock(TppRefProductClassRepo.class);
        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        InstanceService service = new InstanceService(mockTppProductRepo, mockAgreementRepo, mockTppRefProductRegisterTypeRepo, mockTppRefProductClassRepo, mockTppProductRegisterRepo, mockAccountPoolRepo);

        model.setInstanceId(1L);
        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");

        TppRefProductClass productClass = new TppRefProductClass();
        List<TppRefProductRegisterType> registerTypes = new ArrayList<>();
        TppRefProductRegisterType registerType = new TppRefProductRegisterType();
        TppRefAccountType accountType = new TppRefAccountType();
        accountType.setValue("Клиентский");
        registerType.setTppRefAccountType(accountType);
        registerTypes.add(registerType);

        Mockito.when(mockTppRefProductClassRepo.findByValue(model.getProductCode())).thenReturn(productClass);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByTppRefProductClass(productClass)).thenReturn(registerTypes);
        AccountPool accountPool = new AccountPool();
        accountPool.setAccounts(List.of(new Account()));
        Mockito.when(mockAccountPoolRepo.findAccountPool(model.getBranchCode(), model.getIsoCurrencyCode(), model.getMdmCode(), model.getUrgencyCode(), model.getRegisterType())).thenReturn(accountPool);

        Assertions.assertThrows(NotFoundException.class, ()->service.processModel(model));
    }

    @Test
    @Order(7)
    @DisplayName("Проверка положительного срабатывания processModel для добавления ПР")
    public void checkProcessModelProductRegistry() {
        InstanceModel model = new InstanceModel();

        TppProductRepo mockTppProductRepo = Mockito.mock(TppProductRepo.class);
        AgreementRepo mockAgreementRepo = Mockito.mock(AgreementRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        TppRefProductClassRepo mockTppRefProductClassRepo = Mockito.mock(TppRefProductClassRepo.class);
        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        InstanceService service = new InstanceService(mockTppProductRepo, mockAgreementRepo, mockTppRefProductRegisterTypeRepo, mockTppRefProductClassRepo, mockTppProductRegisterRepo, mockAccountPoolRepo);

        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");

        TppRefProductClass productClass = new TppRefProductClass();
        List<TppRefProductRegisterType> registerTypes = new ArrayList<>();
        TppRefProductRegisterType registerType = new TppRefProductRegisterType();
        TppRefAccountType accountType = new TppRefAccountType();
        accountType.setValue("Клиентский");
        accountType.setId(1L);
        registerType.setTppRefAccountType(accountType);
        registerTypes.add(registerType);

        Mockito.when(mockTppRefProductClassRepo.findByValue(model.getProductCode())).thenReturn(productClass);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByTppRefProductClass(productClass)).thenReturn(registerTypes);
        AccountPool accountPool = new AccountPool();
        accountPool.setAccounts(List.of(new Account()));
        Mockito.when(mockAccountPoolRepo.findAccountPool(model.getBranchCode(), model.getIsoCurrencyCode(), model.getMdmCode(), model.getUrgencyCode(), model.getRegisterType())).thenReturn(accountPool);

        Assertions.assertDoesNotThrow(()->service.processModel(model));
    }

    @Test
    @Order(8)
    @DisplayName("Проверка положительного срабатывания processModel для добавления ДС")
    public void checkProcessModelAgreement() {
        InstanceModel model = new InstanceModel();

        TppProductRepo mockTppProductRepo = Mockito.mock(TppProductRepo.class);
        AgreementRepo mockAgreementRepo = Mockito.mock(AgreementRepo.class);

        TppRefProductRegisterTypeRepo mockTppRefProductRegisterTypeRepo = Mockito.mock(TppRefProductRegisterTypeRepo.class);
        TppRefProductClassRepo mockTppRefProductClassRepo = Mockito.mock(TppRefProductClassRepo.class);
        TppProductRegisterRepo mockTppProductRegisterRepo = Mockito.mock(TppProductRegisterRepo.class);
        AccountPoolRepo mockAccountPoolRepo = Mockito.mock(AccountPoolRepo.class);

        InstanceService service = new InstanceService(mockTppProductRepo, mockAgreementRepo, mockTppRefProductRegisterTypeRepo, mockTppRefProductClassRepo, mockTppProductRegisterRepo, mockAccountPoolRepo);

        model.setInstanceId(1L);
        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");
        Arrangement arrangement = new Arrangement();
        arrangement.setNumber("1");
        try {
            arrangement.setOpeningDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.getInstanceArrangement().add(arrangement);


        TppRefProductClass productClass = new TppRefProductClass();
        List<TppRefProductRegisterType> registerTypes = new ArrayList<>();
        TppRefProductRegisterType registerType = new TppRefProductRegisterType();
        TppRefAccountType accountType = new TppRefAccountType();
        accountType.setValue("Клиентский");
        accountType.setId(1L);
        registerType.setTppRefAccountType(accountType);
        registerTypes.add(registerType);

        Mockito.when(mockTppRefProductClassRepo.findByValue(model.getProductCode())).thenReturn(productClass);
        Mockito.when(mockTppRefProductRegisterTypeRepo.findByTppRefProductClass(productClass)).thenReturn(registerTypes);
        AccountPool accountPool = new AccountPool();
        accountPool.setAccounts(List.of(new Account()));
        Mockito.when(mockAccountPoolRepo.findAccountPool(model.getBranchCode(), model.getIsoCurrencyCode(), model.getMdmCode(), model.getUrgencyCode(), model.getRegisterType())).thenReturn(accountPool);
        Mockito.when(mockTppProductRepo.findById(model.getInstanceId())).thenReturn(Optional.of(new TppProduct()));

        Assertions.assertDoesNotThrow(()->service.processModel(model));
    }

    @Test
    @Order(9)
    @DisplayName("Интеграционный тест сохранения ПР")
    public void checkIntegrationProductRegistry() {
        TppProductRegisterRepo registerRepo = ctx.getBean(TppProductRegisterRepo.class);
        registerRepo.deleteAll();
        TppProductRepo productRepo = ctx.getBean(TppProductRepo.class);
        productRepo.deleteAll();

        InstanceModel model = new InstanceModel();
        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");

        InstanceService service = ctx.getBean(InstanceService.class);
        Assertions.assertDoesNotThrow(()->service.processModel(model));
        Assertions.assertEquals(1, productRepo.count());
        Assertions.assertEquals(1, registerRepo.count());
    }

    @Test
    @Order(10)
    @DisplayName("Интеграционный тест сохранения ДС")
    public void checkIntegrationAgreement() {
        TppProductRegisterRepo registerRepo = ctx.getBean(TppProductRegisterRepo.class);
        registerRepo.deleteAll();
        TppProductRepo productRepo = ctx.getBean(TppProductRepo.class);
        productRepo.deleteAll();
        AgreementRepo agreementRepo = ctx.getBean(AgreementRepo.class);
        agreementRepo.deleteAll();

        TppProduct product = new TppProduct();
        productRepo.save(product);

        InstanceModel model = new InstanceModel();
        model.setInstanceId(product.getId());
        model.setProductType(ProductType.НСО);
        model.setProductCode("03.012.002");
        model.setRegisterType("03.012.002_47533_ComSoLd");
        model.setMdmCode("15");
        model.setContractNumber("4");
        try {
            model.setContractDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.setPriority(1);
        model.setContractId(1L);
        model.setBranchCode("0022");
        model.setIsoCurrencyCode("800");
        model.setUrgencyCode("00");
        Arrangement arrangement = new Arrangement();
        arrangement.setNumber("1");
        try {
            arrangement.setOpeningDate(new SimpleDateFormat("yyyy-mm-dd").parse("2024-01-01"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        model.getInstanceArrangement().add(arrangement);

        InstanceService service = ctx.getBean(InstanceService.class);
        Assertions.assertDoesNotThrow(()->service.processModel(model));
        Assertions.assertEquals(1, agreementRepo.count());
    }

    @Test
    @Order(11)
    @DisplayName("Тест rest api создания ПР")
    public void checkRestApiProductRegistry() {
        TppProductRegisterRepo registerRepo = ctx.getBean(TppProductRegisterRepo.class);
        registerRepo.deleteAll();
        TppProductRepo productRepo = ctx.getBean(TppProductRepo.class);
        productRepo.deleteAll();

        HashMap<String, Object> request = new HashMap<>();
        request.put("productType", "НСО");
        request.put("productCode", "03.012.002");
        request.put("registerType", "03.012.002_47533_ComSoLd");
        request.put("mdmCode", "15");
        request.put("contractNumber", "4");
        request.put("contractDate", "2024-03-26");
        request.put("priority", "1");
        request.put("contractId", "1");
        request.put("branchCode", "0022");
        request.put("isoCurrencyCode", "800");
        request.put("urgencyCode", "00");

        given()
                .contentType(ContentType.JSON)
                .with()
                .body(request)
                .when()
                .post("/corporate-settlement-instance/create")
                .then()
                .statusCode(200)
        ;

        Assertions.assertEquals(1, productRepo.count());
        Assertions.assertEquals(1, registerRepo.count());
    }

    @Test
    @Order(12)
    @DisplayName("Тест rest api создания ДС")
    public void checkRestApiAgreement() {
        TppProductRegisterRepo registerRepo = ctx.getBean(TppProductRegisterRepo.class);
        registerRepo.deleteAll();
        TppProductRepo productRepo = ctx.getBean(TppProductRepo.class);
        productRepo.deleteAll();
        AgreementRepo agreementRepo = ctx.getBean(AgreementRepo.class);
        agreementRepo.deleteAll();

        TppProduct product = new TppProduct();
        productRepo.save(product);

        HashMap<String, Object> request = new HashMap<>();
        request.put("instanceId", product.getId());
        request.put("productType", "НСО");
        request.put("productCode", "03.012.002");
        request.put("registerType", "03.012.002_47533_ComSoLd");
        request.put("mdmCode", "15");
        request.put("contractNumber", "4");
        request.put("contractDate", "2024-03-26");
        request.put("priority", "1");
        request.put("contractId", "1");
        request.put("branchCode", "0022");
        request.put("isoCurrencyCode", "800");
        request.put("urgencyCode", "00");

        HashMap<String, Object> agr = new HashMap<>();
        agr.put("number", "1");
        agr.put("openingDate", "2024-03-26");
        Object[] agrs = new Object[1];
        agrs[0] = agr;

        request.put("instanceArrangement", agrs);

        given()
                .contentType(ContentType.JSON)
                .with()
                .body(request)
                .when()
                .post("/corporate-settlement-instance/create")
                .then()
                .statusCode(200)
        ;

        Assertions.assertEquals(1, agreementRepo.count());
    }

}

