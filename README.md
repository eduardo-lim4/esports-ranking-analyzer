# Esports Ranking Analyzer

Sistema em Java para análise de desempenho de algoritmos de ordenação aplicado
a rankings de jogadores de e-sports.

## Objetivo

Desenvolver um sistema capaz de organizar dados de jogadores utilizando
diferentes algoritmos de ordenação e comparar o desempenho de cada algoritmo
através do tempo de execução.

O projeto demonstra, na prática, a diferença entre a complexidade teórica dos
algoritmos e o tempo real medido em milissegundos.

## Algoritmos implementados

| Algoritmo      | Complexidade média/pior caso | Espaço auxiliar |
|----------------|------------------------------|-----------------|
| Bubble Sort    | O(n²)                        | O(1)            |
| Insertion Sort | O(n²)                        | O(1)            |
| Merge Sort     | O(n log n)                   | O(n)            |

Os três algoritmos implementam a interface `SortingAlgorithm<T>` e ordenam a
lista recebida in-place, sem utilizar métodos prontos de ordenação da
biblioteca padrão do Java.

## Categorias de jogos

O sistema trabalha com três categorias de e-sports:

### Dança

Representa jogos de ritmo. Critérios de ordenação (do mais importante para o
menos importante):

1. Maior pontuação.
2. Maior precisão (%).
3. Maior combo máximo.
4. Nome crescente (desempate determinístico).

Atributos principais: `pontuacao`, `precisao`, `comboMaximo`.

### Corrida

Representa jogos de corrida. Critérios de ordenação:

1. Menor tempo de prova (em milissegundos).
2. Menor posição de chegada.
3. Maior número de voltas.
4. Nome crescente.

Atributos principais: `tempoProvaMs`, `posicaoChegada`, `voltas`.

### Luta

Representa jogos de luta. Critérios de ordenação:

1. Maior aproveitamento (`vitórias / (vitórias + derrotas) * 100`).
2. Maior número absoluto de vitórias.
3. Menor número de derrotas.
4. Nome crescente.

Atributos principais: `vitorias`, `derrotas`.

## Modelo de dados

A classe `Jogador` é o modelo central e armazena todos os atributos
necessários às três categorias. A ordenação é feita por meio de um
`Comparator<Jogador>` obtido através de
`Jogador.comparadorPorCategoria(Categoria)`.

Essa abordagem mantém o modelo simples e evita hierarquias de classes
excessivamente complexas, facilitando a manutenção por estudantes iniciantes.

## Fontes de dados

### 1. Geração interna

A classe `DataGenerator` produz conjuntos de jogadores com quantidades
pré-definidas (1.000, 10.000, 50.000, 100.000 ou 500.000). Os valores são
aleatórios, mas reprodutíveis quando uma semente é fornecida.

### 2. Arquivo `.txt`

A classe `DataReader` lê arquivos de texto com o formato a seguir:

```text
CATEGORIA=<DANCA|CORRIDA|LUTA>
ID;NOME;<colunas específicas>
<dados>
```

#### Exemplo - Dança

```text
CATEGORIA=DANCA
ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO
1;Ana Silva;985000;99,5;342
2;Bruno Santos;872000;97,2;215
```

#### Exemplo - Corrida

```text
CATEGORIA=CORRIDA
ID;NOME;TEMPO_PROVA_MS;VOLTAS;POSICAO_CHEGADA
1;Carlos Oliveira;125430;10;1
2;Ana Souza;128900;10;2
```

#### Exemplo - Luta

```text
CATEGORIA=LUTA
ID;NOME;VITORIAS;DERROTAS
1;Ryu Silva;45;5
2;Ken Oliveira;42;8
```

Arquivos de exemplo para as três categorias estão disponíveis no diretório
`data/`.

## Benchmark

A classe `Benchmark` executa os três algoritmos sobre cópias equivalentes do
mesmo conjunto inicial de dados. O tempo é medido exclusivamente durante a
ordenação, utilizando `System.nanoTime()`, e convertido para milissegundos.

**Não são contabilizados no tempo medido:**

- Geração dos dados.
- Leitura do arquivo.
- Criação das cópias.
- Validação dos resultados.
- Atualização da interface.

Após cada execução, o benchmark valida:

- Se os elementos estão ordenados pelo critério selecionado.
- Se nenhum registro foi perdido.
- Se nenhum registro foi duplicado ou alterado indevidamente.

## Interface gráfica

A interface utiliza Java Swing com tema escuro. Permite:

- Selecionar a categoria do jogo.
- Escolher a fonte dos dados (geração interna ou arquivo `.txt`).
- Definir a quantidade de jogadores (quando aplicável).
- Executar a análise em segundo plano (sem congelar a interface).
- Visualizar o ranking ordenado dos jogadores.

A execução da análise utiliza `SwingWorker`, garantindo que operações
longas não bloqueiem a thread de eventos do Swing.

## Como compilar e executar

Pré-requisito: JDK 17 ou superior.

### Compilar

```bash
javac -d out -sourcepath src src/model/*.java src/algorithms/*.java src/data/*.java src/benchmark/*.java src/ui/Main.java
```

No Windows (cmd):

```cmd
javac -d out -sourcepath src src\model\Categoria.java src\model\Jogador.java src\algorithms\SortingAlgorithm.java src\algorithms\BubbleSort.java src\algorithms\InsertionSort.java src\algorithms\MergeSort.java src\data\DataGenerator.java src\data\DataReader.java src\benchmark\BenchmarkResult.java src\benchmark\Benchmark.java src\ui\Main.java
```

### Executar a aplicação

```bash
java -cp out ui.Main
```

### Compilar e executar os testes

Os testes estão no diretório `tests/` e não dependem de frameworks externos.

```bash
javac -d out -cp out tests/*.java
java -cp out TestRunner
```

No Windows (cmd):

```cmd
javac -d out -cp out tests\TestRunner.java tests\SortingTest.java tests\DataGeneratorTest.java tests\DataReaderTest.java tests\BenchmarkTest.java tests\SmokeTest.java
java -cp out TestRunner
```

## Testes

Os testes cobrem:

- Algoritmos: lista vazia, um elemento, ordem aleatória, ordem inversa,
  dados já ordenados, empates e equivalência entre os três algoritmos.
- Dados: geração com diferentes quantidades, compatibilidade de categoria,
  reprodutibilidade e validação de quantidades inválidas.
- Leitura: arquivos válidos, vazios, malformados e com categoria
  incompatível.
- Benchmark: comparação justa, validação da ordenação, preservação do
  conjunto original e tempos não negativos.
- Integração: leitura dos arquivos de exemplo e execução completa do
  benchmark (`SmokeTest`).

## Complexidade teórica vs. tempo medido

A complexidade teórica descreve como o tempo de execução cresce em função do
tamanho da entrada, ignorando constantes e detalhes do hardware. O tempo
medido na prática depende de:

- Tamanho e organização dos dados.
- Características da máquina (CPU, memória, garbage collector).
- Implementação específica do algoritmo.

Por isso, o Merge Sort tende a ser mais rápido que Bubble Sort e Insertion
Sort para entradas grandes, embora ambos os algoritmos O(n²) possam ser
competitivos para listas pequenas ou quase ordenadas.

## Limitações conhecidas

- A visualização do ranking exibe no máximo as 1.000 primeiras posições para
  evitar sobrecarga da interface com conjuntos muito grandes.
- A geração de 500.000 registros pode consumir memória significativa; em
  máquinas com pouca RAM, recomenda-se iniciar com quantidades menores.
- A leitura de arquivos espera o formato documentado; arquivos com
  delimitadores diferentes não são suportados.

## Estrutura do projeto

```text
ESPORTS-RANKING-ANALYZER/
├── data/                       # Arquivos de exemplo
├── results/                    # Diretório para resultados exportados
├── src/
│   ├── algorithms/             # Algoritmos de ordenação
│   ├── benchmark/              # Benchmark e resultados
│   ├── data/                   # Gerador e leitor de dados
│   ├── model/                  # Modelo dos jogadores e categorias
│   └── ui/                     # Interface gráfica
├── tests/                      # Testes simples sem dependências externas
└── README.md
```
