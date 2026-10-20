package entities

import java.time.LocalDate

class Orcamento {
    var valorMinimo: Double = 0.0

    // Data do primeiro movimento inserido: origem do orçamento (o "dia 0" do enunciado)
    private var inicio: LocalDate? = null

    // porData[d] = movimentos da data d, pela ordem de inserção
    private val porData = mutableMapOf<LocalDate, MutableList<Movimento>>()

    // Devolve quantos movimentos foram inseridos
    fun inserir(montante: Double, dataInicial: LocalDate, periodicidade: Int, tipo: TipoMovimento, descricao: String): Int {
        require(periodicidade >= 0) { "A periodicidade não pode ser negativa (0 = movimento único)." }

        val origem = inicio ?: dataInicial
        val ultima = origem.plusDays(DIAS_DO_ORCAMENTO.toLong())
        require(dataInicial >= origem) { "A data $dataInicial é anterior ao início do orçamento ($origem)." }
        require(dataInicial <= ultima) { "A data $dataInicial é posterior ao fim do orçamento ($ultima)." }

        // Um movimento por data, de dataInicial até ao fim do orçamento
        val datas =
            if (periodicidade == 0) listOf(dataInicial)
            else generateSequence(dataInicial) { it.plusDays(periodicidade.toLong()) }
                .takeWhile { it <= ultima }
                .toList()

        // Tudo ou nada: valida-se tudo primeiro e só depois se acrescenta
        val novos = datas.map { Movimento(montante, it, tipo, descricao) }
        datas.forEach {
            require((porData[it]?.size ?: 0) < MAX_POR_DATA) { "A data $it já tem $MAX_POR_DATA movimentos." }
        }
        novos.forEach { porData.getOrPut(it.data) { mutableListOf() }.add(it) }

        inicio = origem
        return novos.size
    }

    fun listar(dia: LocalDate): List<Movimento> = porData[dia]?.toList() ?: emptyList()

    fun remover(dia: LocalDate, indice: Int) {
        val doDia = porData[dia]
            ?: throw IllegalArgumentException("Não existem movimentos em $dia.")
        require(indice in doDia.indices) { "Não existe movimento com índice $indice em $dia." }

        doDia.removeAt(indice)
        if (doDia.isEmpty()) porData.remove(dia)
    }

    // Soma de todos os movimentos com data <= dia
    fun balanco(dia: LocalDate): Double =
        porData.filterKeys { it <= dia }.values.sumOf { movimentos -> movimentos.sumOf { it.efeito } }

    fun valido(): Boolean {
        val datas = datasDoOrcamento()
        if (datas.isEmpty()) return 0.0 >= valorMinimo   // sem movimentos o balanço é sempre 0
        return saldos(datas).all { it >= valorMinimo }
    }

    // Inserir uma despesa em "d" reduz o balanço de d até ao fim do orçamento.
    // Cabe se o menor balanço desse intervalo, menos a despesa, ainda for >= mínimo.
    fun primeiraDataComportavel(dataInicial: LocalDate, despesa: Double): LocalDate? {
        val datas = datasDoOrcamento()
        val saldos = saldos(datas)

        // menorDaquiEmDiante[i] = menor saldo entre a posição i e o fim
        val menorDaquiEmDiante = DoubleArray(saldos.size)
        var menor = Double.POSITIVE_INFINITY
        for (i in saldos.indices.reversed()) {
            menor = minOf(menor, saldos[i])
            menorDaquiEmDiante[i] = menor
        }

        val posicao = datas.indices.firstOrNull {
            datas[it] >= dataInicial && menorDaquiEmDiante[it] - despesa >= valorMinimo
        }
        return posicao?.let { datas[it] }
    }

    // Da origem até DIAS_DO_ORCAMENTO dias depois; vazia se ainda não há movimentos
    private fun datasDoOrcamento(): List<LocalDate> {
        val origem = inicio ?: return emptyList()
        return (0..DIAS_DO_ORCAMENTO).map { origem.plusDays(it.toLong()) }
    }

    // saldos[i] = balanço no fim da data datas[i]
    private fun saldos(datas: List<LocalDate>): List<Double> {
        var acumulado = 0.0
        return datas.map { dia ->
            acumulado += porData[dia]?.sumOf { it.efeito } ?: 0.0
            acumulado
        }
    }

    companion object {
        const val DIAS_DO_ORCAMENTO = 999
        const val MAX_POR_DATA = 100
    }
}