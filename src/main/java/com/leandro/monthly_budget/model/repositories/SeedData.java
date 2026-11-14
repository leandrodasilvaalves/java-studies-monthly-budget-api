package com.leandro.monthly_budget.model.repositories;

import java.math.BigDecimal;
import java.util.List;

import com.leandro.monthly_budget.model.entities.Account;
import com.leandro.monthly_budget.model.entities.Category;
import com.leandro.monthly_budget.model.entities.Person;
import com.leandro.monthly_budget.model.entities.Subcategory;
import com.leandro.monthly_budget.model.entities.Tag;
import com.leandro.monthly_budget.model.enums.CategoryType;

public class SeedData {

	public static void init(
			CategoryRepository categoryRepository,
			TagRepository tagRepository,
			PersonRepository personRepository,
			AccountRepository accountRepository) {

		incluirCategorias(categoryRepository);
		incluirTags(tagRepository);
		incluirPessoas(personRepository);
		incluirContas(accountRepository);

	}
	
	private static void incluirPessoas(PersonRepository repository) {
		
		if(repository.findAll().size() <=0) {
			List.of(
				new Person("Leandro"),
				new Person("Meire"),
				new Person("Leo"),
				new Person("Isa"),
				new Person("todos")
			).forEach(person ->{
				repository.save(person);
				System.out.println(person);
			});
		}
	}
	
	private static void incluirContas(AccountRepository repository) {
		
		if(repository.findAll().size() <=0) {
			List.of(
				new Account("Nubank", new BigDecimal("1000.00")),
				new Account("C6", new BigDecimal("500.00")),
				new Account("Caixa", new BigDecimal("250.00")),
				new Account("Picpay", new BigDecimal("0.00"))
			).forEach(account ->{
				repository.save(account);
				System.out.println(account);
			});
		}
	}
	
	private static void incluirTags(TagRepository repository) {
		
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
				System.out.println(tag);
			});
		}
	}
	
	private static void incluirCategorias(CategoryRepository repository) {
		if (repository.findAll().size() <= 0) {

			var receitasFixas = new Category("💰 Receitas Fixas", CategoryType.Income);
			var receitasVariaveis = new Category("🪙 Receitas Variáveis", CategoryType.Income);
			var habitacao = new Category("🏠 Habitação", CategoryType.Expense);
			var educacao = new Category("📙 Educação", CategoryType.Expense);
			var transporte = new Category("🚘 Transporte", CategoryType.Expense);
			var saude = new Category("🩵 Saúde", CategoryType.Expense);
			var lazer = new Category("🏖️ Lazer", CategoryType.Expense);
			var despesasTemporarias = new Category("⏳ Despesas Temporárias", CategoryType.Expense);
			var despesasPessoais = new Category("👨‍🦱 Despesas Pessoais", CategoryType.Expense);
			var investimentos = new Category("💰 Investimentos", CategoryType.Expense);
			
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
	
}
