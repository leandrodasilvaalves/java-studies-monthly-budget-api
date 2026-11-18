# AGENTS.md — monthly-budget

## 1. Build e testes

**Só existe Maven wrapper (`./mvnw`). Não use `mvn`, nem `gradle`/`./gradlew` (não há `build.gradle`). Java alvo: 26 (`<java.version>` no `pom.xml`).**

```bash
# compilar
./mvnw clean compile

# todos os testes (79 testes)
./mvnw clean test

# somente unitários (69 testes: Mockito + regras puras)
./mvnw test -Dtest='*ServiceImplTest,ReferenceDataValidatorTest,TransactionInvariantsTest,ApiExceptionHandlerTest,CategoryMapperTest,ControllerRepositoryIsolationTest,TransactionalConfigurationTest'

# somente integração (10 testes: @SpringBootTest + H2, perfil "test")
./mvnw test -Dtest='MonthlyBudgetApplicationTests,ControllerValidationTest'

# um teste específico
./mvnw test -Dtest=AccountServiceImplTest

# empacotar / subir a aplicação
./mvnw clean package
./mvnw spring-boot:run
```

- **Sempre use `clean` antes de validar tudo.** `target/` com classes velhas já gerou falhas falsas (`NoClassDefFoundError: SubcategoryService`, falha de contexto) que `./mvnw clean test` resolve.
- **Não há Testcontainers** no `pom.xml`. Integração = `@SpringBootTest @ActiveProfiles("test")` + H2 em memória (`src/test/resources/application-test.properties`, `MODE=MySQL`, `ddl-auto=create-drop`). Não troque por MySQL real.
- Separação real da suíte: unitários em `model/services/*ServiceImplTest`, `services/support`, `model/entities` e `controller/{handlers,mappers}` + `ControllerRepositoryIsolationTest`/`TransactionalConfigurationTest`; integração em `MonthlyBudgetApplicationTests` e `ControllerValidationTest`.
- Se adicionar Testcontainers, adicione dependência explícita e mantenha `application-test.properties` apenas como fallback.

## 2. Banco de dados e migrations

- **Não há Flyway nem Liquibase no `pom.xml` e não existe diretório de migrations.** `src/main/resources` contém só `application.properties`.
- Hoje o schema é gerado por `spring.jpa.hibernate.ddl-auto=update` (dev) / `create-drop` (teste). **Isso não autoriza mexer no banco na mão.**
- **Regra estrita:** nunca alterar schema diretamente no MySQL, nunca editar DDL já aplicado e nunca "consertar" coluna/tabela/índice/constraint manualmente. Toda mudança de schema exige uma **nova migration versionada**, imutável, junto com a alteração da entidade e o ajuste dos testes H2.
- Ao introduzir migrations, use Flyway em `src/main/resources/db/migration/` com `V<sequência>__descricao_snake_case.sql` (ex.: `V1__create_accounts.sql`). Nunca reescreva uma `V1` já aplicada — crie `V2`. Troque `ddl-auto` para `validate` e desative Flyway no perfil `test` se os testes continuarem usando H2.
- Convenção de nomes extraída das entidades:
  - Tabelas: plural `snake_case` — `accounts`, `categories`, `subcategories`, `fixed_incomes`, `fixed_expenses`, `credit_card_bills`, `credit_card_subscriptions`, `credit_card_transactions`, `expense_transactions`, `income_transactions`, `project_transactions`, `transactions`, `transaction_tags`, `people`, `projects`. Exceção atual a não replicar: `creditcards` (padrão seria `credit_cards`); a classe `CreditCardSubscriptions` também deveria ser singular.
  - Colunas: `snake_case`; nomes explícitos quando compostos: `registered_date`, `paid_date`, `received_date`, `due_date`, `movement_date`, `total_installments`, `installment_number`, `purchase_id`, `ignored`, `tx_kind`, `category_id`, `subcategory_id`, `created_at`, `updated_at`.
  - FKs: `<entidade>_id`. Join tables: plural `snake_case`. Unique constraints nomeadas: `unique_categories_name_type`, `unique_subcategory_by_category`.
  - IDs `Long` com `GenerationType.IDENTITY`; dinheiro `BigDecimal(15,2)`; datas `LocalDate`; enums com `@Enumerated(EnumType.STRING)`; `active boolean = true`; auditoria `createdAt`/`updatedAt` via `EntityBase` + `@EnableJpaAuditing`.
  - Herança de transações: `JOINED` com discriminador `tx_kind` (20).
- Credenciais vêm de `DB_USER` / `DB_PASS` (defaults locais em `application.properties`). Nunca comite usuario/senha reais, URL de outro ambiente nem altere `server.port=8091` sem necessidade.
- Dados iniciais são inseridos automaticamente por `SeedData` via `CommandLineRunner` na subida; não popule o banco manualmente nem edite seed para "corrigir" ambiente.

## 3. Arquitetura e camadas

Base: `com.leandro.monthly_budget`. Fluxo obrigatório:

`controller` → `model.services.interfaces` → `model.services.*ServiceImpl` → `model.repositories` → `model.entities`

- `controller/`: `@RestController`, `@RequestMapping("api/v1/...")`, injeção por construtor, `@Valid @RequestBody`, paginação `page` (1-based) / `size` (1..50) / `status` (`StatusRequest.ALL|ACTIVE|INACTIVE`). Conversão: `PageRequest.of(page - 1, size)`.
- **`ControllerRepositoryIsolationTest` impõe: controller nunca deve declarar ou receber `Repository`.** Toda entidade exposta em controller exige interface `*Service` + `*ServiceImpl` com `@Service` e `@Transactional` (`TransactionalConfigurationTest` também impõe isso).
- `model/services/support/ReferenceDataValidator.java`: único lugar para buscar/validar referências (categoria, subcategoria, pessoa, tag, cartão, conta) e período. Não duplique essas buscas no controller nem no mapper.
- `model/dtos/requests`, `model/dtos/responses`, `model/dtos/requests/enums`, `model/enums`, `model/exceptions` — DTOs de entrada e saída separados; nunca exponha entidade JPA na resposta.
- **Records vs classes:** DTOs e entidades são **classes Lombok** (`@Data` nos DTOs; `@Getter/@Setter/@NoArgsConstructor/@AllArgsConstructor` nas entidades). Único `record` no repo é `Payload` em `ApiExceptionHandlerTest`. Não converta DTO/entidade para `record`: MapStruct + Lombok dependem de construtor sem args e setters.
- **MapStruct 1.6.3** (`@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)`), com `lombok-mapstruct-binding 0.2.0` e `mapstruct-processor` configurados no `maven-compiler-plugin` para `compile` **e** `testCompile`. Não escreva implementações de mapper à mão, não remova esses `annotationProcessorPaths` e não use ModelMapper.
- Em `toEntity`, use `@Mapping(target = "id", ignore = true)` e o mesmo para `createdAt`, `updatedAt`, relações e `active` (padrão nos mappers de `FixedIncome`, `FixedExpense`, `Project`, `CreditCardSubscription`). Nunca sete `id`/auditoria manualmente.
- `controller/handlers/ApiExceptionHandler.java`: `@RestControllerAdvice` + `@Order(HIGHEST_PRECEDENCE)` + `ResponseEntityExceptionHandler`, contrato RFC 9457 (`ProblemDetail`). Erros de validação vão em `errors[]` (`field`, `message`, `rejectedValue`).
- Mapeamento de exceções: `ResourceNotFoundException` → 404, `BusinessRuleException` → 400, `DuplicateResourceException` → 409, `DataIntegrityViolationException` → 409. Erros padrão do MVC já vêm da classe pai — não adicione handlers duplicados nem respostas `Map`/`String` customizadas.

## 4. Regras de negócio e segurança

- **Segurança:** não existe `spring-boot-starter-security`, JWT, filtros ou login no `pom.xml`. Endpoints `api/v1/**` são públicos. Não invente autenticação; se for adicionar, atualize `ControllerValidationTest` e `MonthlyBudgetApplicationTests` (hoje esperam 200/400, não 401). Nunca comite tokens, senhas ou segredos — use `DB_USER`/`DB_PASS`.
- Validação obrigatória: `@Valid` em todo `@RequestBody`; `@NotBlank`/`@NotNull` nas requests; `@Min(1)` em `id`/`page`, `@Max(50)` em `size`. Respostas de validação devem continuar no formato do `ApiExceptionHandler`.
- Regras centralizadas (não reimplementar): request não nulo; `startDate < endDate`; tipo de categoria compatível com a operação (`INCOME`/`EXPENSE`); subcategoria pertence à categoria informada; pessoas/tags existem e ids são distintos; cartão/conta existem; nome duplicado → `DuplicateResourceException`; ativar/inativar apenas via `PATCH /{id}/activate` e `/{id}/inactivate`.
- Invariantes cobertos por `TransactionInvariantsTest` — manter os métodos `is*Consistent()` nas entidades: despesa `PAID` exige `paidDate` + conta; `PENDING` não pode ter dados de pagamento; receita `RECEIVED` exige `receivedDate` + conta; parcela entre `1` e `totalInstallments`; transação de projeto `DONE` exige `movementDate`; tipo `YIELD` não pode ter conta.
- Auditoria `createdAt`/`updatedAt` é automática; não altere manualmente.
- Nunca comite: `target/`, `.mvn/wrapper/maven-wrapper.jar`, credenciais, arquivos de IDE (`.classpath`, `.project`, `.settings/`, `.vscode/`, `.idea/`) — já ignorados no `.gitignore`.
