package com.leandro.monthly_budget.model.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.CreditCard;
import com.leandro.monthly_budget.model.entities.CreditCardSubscription;
import com.leandro.monthly_budget.model.entities.FixedExpense;
import com.leandro.monthly_budget.model.entities.FixedIncome;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Project;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;
import com.leandro.monthly_budget.model.enums.ProjectType;

@Component
public class SeedData {

	private static final Logger log = LoggerFactory.getLogger(SeedData.class);

	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;
	private final PersonRepository personRepository;
	private final AccountRepository accountRepository;
	private final SubcategoryRepository subcategoryRepository;
	private final FixedIncomeRepository fixedIncomeRepository;
	private final FixedExpenseRepository fixedExpenseRepository;
	private final ProjectRepository projectRepository;
	private final CreditCardSubscriptionRepository creditCardSubscriptionRepository;
	private final CreditCardRepository creditCardRepository;

	public SeedData(
			CategoryRepository categoryRepository,
			TagRepository tagRepository,
			PersonRepository personRepository,
			AccountRepository accountRepository,
			SubcategoryRepository subcategoryRepository,
			FixedIncomeRepository fixedIncomeRepository,
			FixedExpenseRepository fixedExpenseRepository,
			ProjectRepository projectRepository,
			CreditCardSubscriptionRepository creditCardSubscriptionRepository,
			CreditCardRepository creditCardRepository) {
		this.categoryRepository = categoryRepository;
		this.tagRepository = tagRepository;
		this.personRepository = personRepository;
		this.accountRepository = accountRepository;
		this.subcategoryRepository = subcategoryRepository;
		this.fixedIncomeRepository = fixedIncomeRepository;
		this.fixedExpenseRepository = fixedExpenseRepository;
		this.projectRepository = projectRepository;
		this.creditCardSubscriptionRepository = creditCardSubscriptionRepository;
		this.creditCardRepository = creditCardRepository;
	}

	@Transactional
	public void init() {

		incluirCategorias(categoryRepository);
		incluirTags(tagRepository);
		incluirPessoas(personRepository);
		incluirContas(accountRepository);
		incluirCreditCards(creditCardRepository);
		incluirProjetos();
		incluirDespesasFixas();
		incluirReceitasFixas();
		incluirAssinaturasCartao();

	}

	private void incluirPessoas(PersonRepository repository) {
		
		if(repository.findAll().size() <=0) {
			List.of(
				new Person("Leandro"),
				new Person("Meire"),
				new Person("Leo"),
				new Person("Isa"),
				new Person("todos")
			).forEach(person ->{
				repository.save(person);
				log.debug("Seeded {} {}", person.getClass().getSimpleName(), person.getId());
			});
		}
	}
	
	private void incluirContas(AccountRepository repository) {
		
		if(repository.findAll().size() <=0) {
			List.of(
				new Account("Nubank", new BigDecimal("1000.00")),
				new Account("C6", new BigDecimal("500.00")),
				new Account("Caixa", new BigDecimal("250.00")),
				new Account("Picpay", new BigDecimal("0.00"))
			).forEach(account ->{
				repository.save(account);
				log.debug("Seeded {} {}", account.getClass().getSimpleName(), account.getId());
			});
		}
	}
	
	private void incluirTags(TagRepository repository) {
		
		if(repository.findAll().size() <=0) {
			List.of(
				new Tag("amortização-caixa"),
				new Tag("renda+2045"),
				new Tag("caixinha-feliz-saida"),
				new Tag("caixinha-feliz-isa"),
				new Tag("leandro-40-anos"),
				new Tag("projeto-garagem"),
				new Tag("cc-recorrente")
			).forEach(tag ->{
				repository.save(tag);
				log.debug("Seeded {} {}", tag.getClass().getSimpleName(), tag.getId());
			});
		}
	}
	
	private void incluirCategorias(CategoryRepository repository) {
		if (repository.findAll().size() <= 0) {

			var receitasFixas = new Category("💰 Receitas Fixas", CategoryType.INCOME);
			var receitasVariaveis = new Category("🪙 Receitas Variáveis", CategoryType.INCOME);
			var habitacao = new Category("🏠 Habitação", CategoryType.EXPENSE);
			var educacao = new Category("📙 Educação", CategoryType.EXPENSE);
			var transporte = new Category("🚘 Transporte", CategoryType.EXPENSE);
			var saude = new Category("🩵 Saúde", CategoryType.EXPENSE);
			var lazer = new Category("🏖️ Lazer", CategoryType.EXPENSE);
			var despesasTemporarias = new Category("⏳ Despesas Temporárias", CategoryType.EXPENSE);
			var despesasPessoais = new Category("👨‍🦱 Despesas Pessoais", CategoryType.EXPENSE);
			var investimentos = new Category("💰 Investimentos", CategoryType.EXPENSE);
			
			incluirSubcategoriasEmReceitasFixas(receitasFixas);
			incluirSubcategoriasEmReceitasVariaveis(receitasVariaveis);
			incluirSubcategoriasEmHabitacao(habitacao);
			incluirSubcategoriasEmEducacao(educacao);
			incluirSubcategoriasEmTransporte(transporte);
			incluirSubcategoriasEmSaude(saude);
			incluirSubcategoriasEmLazer(lazer);
			incluirSubcategoriasEmDespesasTemporarias(despesasTemporarias);
			incluirSubcategoriasEmDespesasPessoais(despesasPessoais);
			incluirSubcategoriasEmInvestimentos(investimentos);

			repository.save(receitasFixas);
			repository.save(receitasVariaveis);
			repository.save(habitacao);
			repository.save(educacao);
			repository.save(transporte);
			repository.save(saude);
			repository.save(lazer);
			repository.save(despesasTemporarias);
			repository.save(despesasPessoais);	
			repository.save(investimentos);	
		}
	}
	
	private static void incluirSubcategoriasEmReceitasFixas(Category receitasFixas)
	{
		var salarioLiquido = new Subcategory("Salário Líquido", receitasFixas);
		var valeAlimentacao = new Subcategory("Vale Alimentação", receitasFixas);
		
		receitasFixas.setSubcategories(List.of(
				salarioLiquido, valeAlimentacao));
	}
	
	private static void incluirSubcategoriasEmReceitasVariaveis(Category receitasVariaveis)
	{
		var decimoTerceiroSalarioLiquido = new Subcategory("13º Salário Líquido", receitasVariaveis);
		var cashbacks = new Subcategory("Cashbacks", receitasVariaveis);
		var ferias = new Subcategory("Férias", receitasVariaveis);
		var participacoesDeLucro = new Subcategory("Participações de Lucro", receitasVariaveis);
		var presentes = new Subcategory("Presentes", receitasVariaveis);
		var proventosAcoesFIIs = new Subcategory("Proventos Ações/FIIs", receitasVariaveis);
		var recisoesDeTrabalho = new Subcategory("Recisões de Trabalho", receitasVariaveis);
		var reembolsosRestituicoes = new Subcategory("Reembolsos + Restituições", receitasVariaveis);
		var resgateDeInvestimentos = new Subcategory("Resgate de investimentos", receitasVariaveis);
		var resgateFGTS = new Subcategory("Resgate FGTS", receitasVariaveis);
		
		receitasVariaveis.setSubcategories(List.of(
				decimoTerceiroSalarioLiquido, cashbacks, ferias, participacoesDeLucro,
				presentes, proventosAcoesFIIs, recisoesDeTrabalho, reembolsosRestituicoes,
				resgateDeInvestimentos, resgateFGTS));
	}
	
	private static void incluirSubcategoriasEmHabitacao(Category habitacao)
	{
		var contaDeAgua = new Subcategory("Conta de Água", habitacao);
		var contaDeEnergia = new Subcategory("Conta de Energia", habitacao);
		var feira = new Subcategory("Feira", habitacao);
		var gas = new Subcategory("Gás", habitacao);
		var internet = new Subcategory("Internet", habitacao);
		var iptuTaxasImpostos = new Subcategory("IPTU/Taxas/Impostos", habitacao);
		var jardinagem = new Subcategory("Jardinagem", habitacao);
		var mobiliaEletrodomesticosParcelados = new Subcategory("Mobília / eletrodomésticos (a partir de 3x)", habitacao);
		var padaria = new Subcategory("Padaria", habitacao);
		var planoFunerarioJazigo = new Subcategory("Plano Funerário/Jazigo", habitacao);
		var prestacaoFinCaixa = new Subcategory("Prestação Fin Caixa", habitacao);
		var amortizacaoFinCaixa = new Subcategory("Amortização Fin Caixa", habitacao);
		var servicosStreamAssinaturas = new Subcategory("Serviços Stream/Assinaturas", habitacao);
		var supermercado = new Subcategory("Supermercado", habitacao);
		var telefonesCelulares = new Subcategory("Telefones Celulares", habitacao);
		
		habitacao.setSubcategories(List.of(
				contaDeAgua, contaDeEnergia, feira, gas, internet,
				iptuTaxasImpostos, jardinagem, mobiliaEletrodomesticosParcelados, padaria,
				planoFunerarioJazigo, prestacaoFinCaixa, amortizacaoFinCaixa,
				servicosStreamAssinaturas, supermercado, telefonesCelulares));
	}
	
	private static void incluirSubcategoriasEmEducacao(Category educacao)
	{
		var cursosInscricoes = new Subcategory("Cursos / Inscrições", educacao);
		var escolaFaculdade = new Subcategory("Escola / Faculdade", educacao);
		var festividadesRifasEventos = new Subcategory("Festividades/Rifas/Eventos", educacao);
		var lancheEscolar = new Subcategory("Lanche Escolar", educacao);
		var livros = new Subcategory("Livros", educacao);
		var materialEscolar = new Subcategory("Material Escolar", educacao);
		var uniformes = new Subcategory("Uniformes", educacao);
		var outros = new Subcategory("Outros", educacao);
		
		educacao.setSubcategories(List.of(
				cursosInscricoes, escolaFaculdade, festividadesRifasEventos, lancheEscolar,
				livros, materialEscolar, uniformes, outros));
	}
	
	private static void incluirSubcategoriasEmTransporte(Category transporte)
	{
		var acessorios = new Subcategory("Acessórios", transporte);
		var bicicleta = new Subcategory("Bicicleta", transporte);
		var combustivel = new Subcategory("Combustível", transporte);
		var estacionamentos = new Subcategory("Estacionamentos", transporte);
		var ipvaSegObrigLicen = new Subcategory("IPVA + Seg. Obrig + Licen", transporte);
		var lavagens = new Subcategory("Lavagens", transporte);
		var mecanicoManutencoes = new Subcategory("Mecânico/Manutenções", transporte);
		var multas = new Subcategory("Multas", transporte);
		var parcela = new Subcategory("Parcela", transporte);
		var seguro = new Subcategory("Seguro", transporte);
		var taxiUber = new Subcategory("Taxi/Uber", transporte);
		var transporteEscolar = new Subcategory("Transporte Escolar", transporte);
		
		transporte.setSubcategories(List.of(
				acessorios, bicicleta, combustivel, estacionamentos, ipvaSegObrigLicen,
				lavagens, mecanicoManutencoes, multas, parcela, seguro, taxiUber,
				transporteEscolar));
	}
	
	private static void incluirSubcategoriasEmSaude(Category saude)
	{
		var dentista = new Subcategory("Dentista", saude);
		var examesVacinas = new Subcategory("Exames/Vacinas", saude);
		var farmacia = new Subcategory("Farmácia", saude);
		var suplementosCreatina = new Subcategory("Suplementos/Creatina", saude);
		var vitaminas = new Subcategory("Vitaminas", saude);
		
		saude.setSubcategories(List.of(
				dentista, examesVacinas, farmacia, suplementosCreatina, vitaminas));
	}
	
	private static void incluirSubcategoriasEmLazer(Category lazer)
	{
		var cafesSorvetesLanches = new Subcategory("Cafés, Sorvetes, Lanches", lazer);
		var passeiosEventos = new Subcategory("Passeios/Eventos", lazer);
		var praia = new Subcategory("Praia", lazer);
		var restaurantes = new Subcategory("Restaurantes", lazer);
		
		lazer.setSubcategories(List.of(
				cafesSorvetesLanches, passeiosEventos, praia, restaurantes));
	}
	
	private static void incluirSubcategoriasEmDespesasTemporarias(Category despesasTemporarias)
	{
		var assinaturaTrimestreSemestreAno = new Subcategory("Assinatura Trimestre/Semestre/Ano", despesasTemporarias);
		var listaDeDesejos = new Subcategory("Lista de desejos", despesasTemporarias);
		var brinquedosAVista = new Subcategory("Brinquedos (a vista)", despesasTemporarias);
		var cartorios = new Subcategory("Cartórios", despesasTemporarias);
		var comemoracoesAniversarios = new Subcategory("Comemorações / Aniversários", despesasTemporarias);
		var correio = new Subcategory("Correio", despesasTemporarias);
		var decoracao = new Subcategory("Decoração", despesasTemporarias);
		var doacoesGorjetasCaixinhas = new Subcategory("Doações / Gorjetas / Caixinhas", despesasTemporarias);
		var eletronicosAVista = new Subcategory("Eletrônicos (à vista)", despesasTemporarias);
		var emprestimos = new Subcategory("Empréstimos", despesasTemporarias);
		var ferramentasAVista = new Subcategory("Ferramentas (à vista)", despesasTemporarias);
		var gratificacoes = new Subcategory("Gratificações", despesasTemporarias);
		var manutencoesEReparos = new Subcategory("Manutenções e reparos", despesasTemporarias);
		var medicosETerapeutasEsporadicos = new Subcategory("Médicos e Terapeutas Esporádicos", despesasTemporarias);
		var mobiliaEletrodomesticosAVista = new Subcategory("Mobília / eletrodomésticos (à vista)", despesasTemporarias);
		var outros = new Subcategory("Outros", despesasTemporarias);
		var papelariaXerox = new Subcategory("Papelaria / Xerox", despesasTemporarias);
		var presentes = new Subcategory("Presentes", despesasTemporarias);
		var tarifasEsporadicas = new Subcategory("Tarifas Esporádicas", despesasTemporarias);
		var utilidadeDomestica = new Subcategory("Utilidade doméstica", despesasTemporarias);
		
		despesasTemporarias.setSubcategories(List.of(
				assinaturaTrimestreSemestreAno, listaDeDesejos, brinquedosAVista, cartorios,
				comemoracoesAniversarios, correio, decoracao, doacoesGorjetasCaixinhas,
				eletronicosAVista, emprestimos, ferramentasAVista, gratificacoes,
				manutencoesEReparos, medicosETerapeutasEsporadicos,
				mobiliaEletrodomesticosAVista, outros, papelariaXerox, presentes,
				tarifasEsporadicas, utilidadeDomestica));
	}
	
	private static void incluirSubcategoriasEmDespesasPessoais(Category despesasPessoais)
	{
		var academia = new Subcategory("Academia", despesasPessoais);
		var ajudaDeCusto = new Subcategory("Ajuda de custo", despesasPessoais);
		var cabeleireiro = new Subcategory("Cabeleireiro", despesasPessoais);
		var docHabilitacaoRenovacaoOutros = new Subcategory("Doc/Habilitação/renovação/outros", despesasPessoais);
		var eletronicosParcelados = new Subcategory("Eletrônicos (parcelado)", despesasPessoais);
		var higienePessoal = new Subcategory("Higiene pessoal (unhas, depilação, cosméticos, etc)", despesasPessoais);
		var instrumentosMusicais = new Subcategory("Instrumentos Musicais", despesasPessoais);
		var mesadas = new Subcategory("Mesadas", despesasPessoais);
		var seguroDeVida = new Subcategory("Seguro de Vida", despesasPessoais);
		var vestuario = new Subcategory("Vestuário", despesasPessoais);
		var viagens = new Subcategory("Viagens", despesasPessoais);
		var presentes = new Subcategory("Presentes", despesasPessoais);
		var outros = new Subcategory("Outros", despesasPessoais);
		var natacao = new Subcategory("Natação", despesasPessoais);
		var pet = new Subcategory("Pet", despesasPessoais);
		
		despesasPessoais.setSubcategories(List.of(
				academia, ajudaDeCusto, cabeleireiro, docHabilitacaoRenovacaoOutros,
				eletronicosParcelados, higienePessoal, instrumentosMusicais, mesadas,
				seguroDeVida, vestuario, viagens, presentes, outros, natacao, pet));
	}
	
	private static void incluirSubcategoriasEmInvestimentos(Category investimentos)
	{
		var acoes = new Subcategory("Ações", investimentos);
		var criptoBitcoins = new Subcategory("Cripto Bitcoins", investimentos);
		var criptoOutras = new Subcategory("Cripto Outras", investimentos);
		var fiis = new Subcategory("FIIs", investimentos);
		var previdencia = new Subcategory("Previdência", investimentos);
		var reservaDeEmergencia = new Subcategory("Reserva de Emergência", investimentos);
		var tesouroRenda2045 = new Subcategory("Tesouro Renda+ 2045", investimentos);
		
		investimentos.setSubcategories(List.of(
				acoes, criptoBitcoins, criptoOutras, fiis, previdencia,
				reservaDeEmergencia, tesouroRenda2045));
	}

	private void incluirCreditCards(CreditCardRepository repository) {
		if (repository.findAll().size() <= 0) {
			CreditCard nubank = new CreditCard();
			nubank.setName("Nubank");
			nubank.setDueDay(10);
			List.of(nubank).forEach(card -> {
				repository.save(card);
				log.debug("Seeded {} {}", card.getClass().getSimpleName(), card.getId());
			});
		}
	}

	private void incluirProjetos() {
		if (projectRepository.findAll().size() <= 0) {
			LocalDate startDate = LocalDate.of(2026, 1, 1);
			LocalDate endDate = LocalDate.of(2026, 12, 31);
			Category categoria = requireCategory("👨‍🦱 Despesas Pessoais");
			Subcategory subcategoria = requireSubcategory("Presentes", categoria);
			Account conta = requireAccount("Nubank");

			List.of(
				newProject(categoria, "Aniversário Meire", subcategoria, List.of(requirePerson("Meire")), conta, startDate, endDate, new BigDecimal("100.00")),
				newProject(categoria, "Aniversário Leandro", subcategoria, List.of(requirePerson("Leandro")), conta, startDate, endDate, new BigDecimal("100.00")),
				newProject(categoria, "Aniversário Isa", subcategoria, List.of(requirePerson("Isa")), conta, startDate, endDate, new BigDecimal("100.00")),
				newProject(categoria, "Aniversário Leo", subcategoria, List.of(requirePerson("Leo")), conta, startDate, endDate, new BigDecimal("100.00"))
			).forEach(project -> {
				projectRepository.save(project);
				log.debug("Seeded {} {}", project.getClass().getSimpleName(), project.getId());
			});
		}
	}

	private Project newProject(Category category, String name, Subcategory subcategory, List<Person> people,
			Account account, LocalDate firstDepositDate, LocalDate goalDate, BigDecimal plannedAmount) {
		Project project = new Project();
		project.setCategory(category);
		project.setName(name);
		project.setSubcategory(subcategory);
		project.setPeople(people);
		project.setAccount(account);
		project.setFirstDepositDate(firstDepositDate);
		project.setGoalDate(goalDate);
		project.setPlannedAmount(plannedAmount);
		project.setType(ProjectType.INDIVIDUAL);
		project.setBrokerageAccount("");
		return project;
	}

	private void incluirDespesasFixas() {
		if (fixedExpenseRepository.findAll().size() <= 0) {
			LocalDate startDate = LocalDate.of(2026, 1, 1);
			LocalDate endDate = LocalDate.of(2026, 12, 31);

			List.of(
				newFixedExpense("🏠 Habitação", "Internet", "Alares", "", List.of("todos"), new BigDecimal("101.10"), startDate, endDate),
				newFixedExpense("🏠 Habitação", "Conta de Energia", "Cosern", "", List.of("todos"), new BigDecimal("358.00"), startDate, endDate),
				newFixedExpense("🏠 Habitação", "Conta de Água", "Caern", "", List.of("todos"), new BigDecimal("84.00"), startDate, endDate),
				newFixedExpense("🏠 Habitação", "Prestação Fin Caixa", "Caixa", "", List.of("todos"), new BigDecimal("1150.00"), startDate, endDate),
				newFixedExpense("📙 Educação", "Escola / Faculdade", "Esc. Peixoto", "", List.of("Leo", "Isa"), new BigDecimal("890.00"), startDate, endDate),
				newFixedExpense("🏠 Habitação", "Telefones Celulares", "", "Claro", List.of("Leandro", "Meire"), new BigDecimal("149.99"), startDate, endDate),
				newFixedExpense("🚘 Transporte", "Parcela", "C6 Bank", "financiamento", List.of("todos"), new BigDecimal("1300.00"), startDate, endDate),
				newFixedExpense("👨‍🦱 Despesas Pessoais", "Mesadas", "", "Mesada Leo", List.of("Leo"), new BigDecimal("70.00"), startDate, endDate),
				newFixedExpense("👨‍🦱 Despesas Pessoais", "Mesadas", "", "Mesada Leo", List.of("Isa"), new BigDecimal("100.00"), startDate, endDate)
			).forEach(expense -> {
				fixedExpenseRepository.save(expense);
				log.debug("Seeded {} {}", expense.getClass().getSimpleName(), expense.getId());
			});
		}
	}

	private FixedExpense newFixedExpense(String categoryName, String subcategoryName, String store,
			String description, List<String> personNames, BigDecimal amount,
			LocalDate startDate, LocalDate endDate) {
		Category category = requireCategoryByType(categoryName, CategoryType.EXPENSE);
		Subcategory subcategory = requireSubcategory(subcategoryName, category);
		List<Person> people = requirePeopleByNames(personNames);

		FixedExpense expense = new FixedExpense();
		expense.setCategory(category);
		expense.setSubcategory(subcategory);
		expense.setStore(store);
		expense.setPeople(people);
		expense.setDescription(description);
		expense.setAmount(amount);
		expense.setStartDate(startDate);
		expense.setEndDate(endDate);
		expense.setIgnore(false);
		return expense;
	}

	private void incluirReceitasFixas() {
		if (fixedIncomeRepository.findAll().size() <= 0) {
			LocalDate startDate = LocalDate.of(2026, 1, 1);
			LocalDate endDate = LocalDate.of(2026, 12, 31);
			Category categoria = requireCategory("💰 Receitas Fixas");
			Person recipient = requirePerson("Leandro");

			List.of(
				newFixedIncome("Salário Líquido", categoria, "Banky", recipient, "", new BigDecimal("50000.00"), startDate, endDate),
				newFixedIncome("Vale Alimentação", categoria, "Bankly", recipient, "", new BigDecimal("4900.00"), startDate, endDate)
			).forEach(income -> {
				fixedIncomeRepository.save(income);
				log.debug("Seeded {} {}", income.getClass().getSimpleName(), income.getId());
			});
		}
	}

	private FixedIncome newFixedIncome(String subcategoryName, Category category, String payer,
			Person recipient, String description, BigDecimal amount,
			LocalDate startDate, LocalDate endDate) {
		Subcategory subcategory = requireSubcategory(subcategoryName, category);

		FixedIncome income = new FixedIncome();
		income.setCategory(category);
		income.setSubcategory(subcategory);
		income.setPayer(payer);
		income.setRecipient(recipient);
		income.setDescription(description);
		income.setAmount(amount);
		income.setStartDate(startDate);
		income.setEndDate(endDate);
		income.setIgnore(false);
		return income;
	}

	private void incluirAssinaturasCartao() {
		if (creditCardSubscriptionRepository.findAll().size() <= 0) {
			LocalDate startDate = LocalDate.of(2026, 1, 1);
			LocalDate endDate = LocalDate.of(2026, 12, 31);
			Category categoria = requireCategory("🏠 Habitação");
			Subcategory subcategory = requireSubcategory("Serviços Stream/Assinaturas", categoria);
			CreditCard cartao = requireCreditCard("Nubank");
			List<Person> people = requirePeopleByNames(List.of("todos"));

			List.of(
				newSubscription("Netflix", subcategory, cartao, people, "", new BigDecimal("40.90"), startDate, endDate),
				newSubscription("Spotfiy", subcategory, cartao, people, "", new BigDecimal("28.90"), startDate, endDate)
			).forEach(subscription -> {
				creditCardSubscriptionRepository.save(subscription);
				log.debug("Seeded {} {}", subscription.getClass().getSimpleName(), subscription.getId());
			});
		}
	}

	private CreditCardSubscription newSubscription(String store, Subcategory subcategory,
			CreditCard creditCard, List<Person> people, String description,
			BigDecimal amount, LocalDate startDate, LocalDate endDate) {
		CreditCardSubscription subscription = new CreditCardSubscription();
		subscription.setCreditCard(creditCard);
		subscription.setCategory(subcategory.getCategory());
		subscription.setSubcategory(subcategory);
		subscription.setStore(store);
		subscription.setPeople(people);
		subscription.setDescription(description);
		subscription.setAmount(amount);
		subscription.setStartDate(startDate);
		subscription.setEndDate(endDate);
		subscription.setIgnore(false);
		return subscription;
	}

	/* ---------- helpers de busca por nome (referencias ja semeadas) ---------- */

	private Category requireCategory(String name) {
		return categoryRepository.findByName(name)
				.orElseThrow(() -> new IllegalStateException("Categoria nao encontrada no seed: " + name));
	}

	private Category requireCategoryByType(String name, CategoryType type) {
		Category category = requireCategory(name);
		if (category.getType() != type) {
			throw new IllegalStateException("Categoria '" + name + "' nao eh do tipo " + type);
		}
		return category;
	}

	private Subcategory requireSubcategory(String name, Category category) {
		return subcategoryRepository.findByNameAndCategoryId(name, category.getId())
				.orElseThrow(() -> new IllegalStateException(
						"Subcategoria nao encontrada no seed: " + name + " em " + category.getName()));
	}

	private Person requirePerson(String name) {
		return personRepository.findByName(name)
				.orElseThrow(() -> new IllegalStateException("Pessoa nao encontrada no seed: " + name));
	}

	private List<Person> requirePeopleByNames(List<String> names) {
		return names.stream().map(this::requirePerson).toList();
	}

	private Account requireAccount(String name) {
		return accountRepository.findByName(name)
				.orElseThrow(() -> new IllegalStateException("Conta nao encontrada no seed: " + name));
	}

	private CreditCard requireCreditCard(String name) {
		return creditCardRepository.findByName(name)
				.orElseThrow(() -> new IllegalStateException("Cartao nao encontrado no seed: " + name));
	}

}
