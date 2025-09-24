# 📚 Exercícios de Programação Orientada a Objetos em Java

Este repositório contém uma série de exercícios práticos que cobrem os principais conceitos de Programação Orientada a Objetos (POO) em Java, desde fundamentos básicos até padrões avançados.

## 🎯 Objetivos de Aprendizagem

- **Encapsulamento**: Controle de acesso e validação de dados
- **Herança**: Reutilização e especialização de código
- **Polimorfismo**: Flexibilidade através de interfaces e classes abstratas
- **Abstração**: Modelagem de conceitos complexos
- **Imutabilidade**: Criação de objetos seguros e previsíveis
- **Generics**: Programação type-safe e reutilizável
- **Design Patterns**: Soluções elegantes para problemas comuns

## 📋 Lista de Exercícios

### 🔒 Exercício 1 - Encapsulamento (Classe Produto)
**Conceitos:** Atributos privados, getters/setters, validações

Implemente uma classe `Produto` com validações rigorosas:
- ✅ Atributos privados: `nome`, `preco`, `quantidadeEmEstoque`
- ✅ Validações: preço e quantidade não negativos, nome não nulo/vazio
- ✅ Tratamento de exceções com `IllegalArgumentException`

### 📈 Exercício 2 - Encapsulamento com Validação de Regra (Desconto)
**Conceitos:** Extensão de classes, validações de negócio, exceções customizadas

Estende a classe `Produto` com funcionalidade de desconto:
- ✅ Método `aplicarDesconto(double porcentagem)`
- ✅ Validação: desconto entre 0% e 50%
- ✅ Exceção customizada `DescontoInvalidoException`

### 👥 Exercício 3 - Herança (Hierarquia de Funcionários)
**Conceitos:** Herança, sobrescrita de métodos, polimorfismo básico

Sistema hierárquico de funcionários:
- ✅ Classe base `Funcionario` com atributos protegidos
- ✅ Subclasses `Gerente` (bônus 20%) e `Desenvolvedor` (bônus 10%)
- ✅ Coleções polimórficas `List<Funcionario>`

### 🚗 Exercício 4 - Polimorfismo com Interface (IMeioTransporte)
**Conceitos:** Interfaces, polimorfismo, tratamento de exceções

Sistema de transportes com diferentes comportamentos:
- ✅ Interface `IMeioTransporte` com `acelerar()` e `frear()`
- ✅ Implementações: `Carro`, `Bicicleta`, `Trem`
- ✅ Limites de velocidade específicos por transporte
- ✅ Exceções para operações inválidas

### 💳 Exercício 5 - Abstração (Sistema de Pagamentos)
**Conceitos:** Classes abstratas, validações específicas, exceções customizadas

Sistema flexível de formas de pagamento:
- ✅ Classe abstrata `FormaPagamento`
- ✅ Implementações: `CartaoCredito`, `Boleto`, `Pix`
- ✅ Validações específicas (número do cartão, formato do boleto, chave Pix)
- ✅ Exceção `PagamentoInvalidoException`

### 🛒 Exercício 6 - Imutabilidade e Objetos de Valor (Carrinho de Compras)
**Conceitos:** Imutabilidade, objetos de valor, operações funcionais

Sistema de carrinho completamente imutável:
- ✅ Objeto de valor `Dinheiro` (BigDecimal + enum Moeda)
- ✅ Classes imutáveis: `Produto`, `ItemCarrinho`, `Carrinho`
- ✅ Operações retornam novas instâncias
- ✅ Validações: quantidades positivas, cupons limitados a 30%
- ✅ Arredondamento bancário

### 📦 Exercício 7 - Generics (Repositório Genérico em Memória)
**Conceitos:** Generics, type safety, Optional, coleções imutáveis

Repositório genérico reutilizável:
- ✅ Interface `Identificavel<ID>` para entidades
- ✅ Repositório genérico `IRepository<T extends Identificavel<ID>, ID>`
- ✅ Implementação `InMemoryRepository` com `Map<ID, T>`
- ✅ Métodos que retornam `Optional<T>` e listas imutáveis
- ✅ Exceção `EntidadeNaoEncontradaException`

### 🚚 Exercício 8 - Padrão Strategy (Cálculo de Frete com Lambdas)
**Conceitos:** Strategy Pattern, lambdas, injeção de dependência

Sistema flexível de cálculo de frete:
- ✅ Interface `CalculadoraFrete` 
- ✅ Estratégias: `Sedex`, `PAC`, `RetiradaNaLoja`
- ✅ Estratégias promocionais via lambdas
- ✅ Validação de CEP e região
- ✅ Troca de estratégia em tempo de execução

## 🛠️ Tecnologias e Ferramentas

- **Java 11+** - Linguagem principal
- **BigDecimal** - Para cálculos monetários precisos
- **Optional** - Para tratamento seguro de nulos
- **Collections Framework** - Listas, maps e streams
- **Generics** - Para type safety
- **Lambdas** - Para programação funcional
- **Exceções Customizadas** - Para tratamento específico de erros

## 📚 Conceitos Avançados Demonstrados

### Imutabilidade

### Generics com Bounded Types

### Strategy Pattern com Lambdas

## 🎯 Benefícios Educacionais

1. **Progressão Gradual**: Dos conceitos básicos aos avançados
2. **Exemplos Práticos**: Cenários realistas do mundo real
3. **Boas Práticas**: Código limpo, SOLID principles, design patterns
4. **Tratamento de Erros**: Uso adequado de exceções
5. **Type Safety**: Uso correto de generics
6. **Programação Funcional**: Integração de lambdas com OOP
