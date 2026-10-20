package cli

import entities.TipoMovimento
import entities.User
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun parseData(texto: String): LocalDate =
    try {
        LocalDate.parse(texto)
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("Data inválida: '$texto'. Use o formato AAAA-MM-DD.")
    }

// Mapa construído para UM utilizador: todos os comandos actuam sobre o orçamento dele
fun comandosAutenticados(user: User, terminarSessao: () -> Unit): Comandos {
    val orcamento = user.orcamento

    return mapOf(
        "i" to Comando(5) { args ->
            orcamento.inserir(
                montante = args[0].toDouble(),
                dataInicial = parseData(args[1]),
                periodicidade = args[2].toInt(),
                tipo = TipoMovimento.deSimbolo(args[3]),
                descricao = args[4]
            )
        },
        "l" to Comando(1) { args ->
            orcamento.listar(parseData(args[0])).forEachIndexed { indice, m ->
                println("$indice ${m.montante} ${m.tipo.simbolo} ${m.descricao}")
            }
        },
        "r" to Comando(2) { args ->
            orcamento.remover(parseData(args[0]), args[1].toInt())
        },
        "b" to Comando(1) { args ->
            println(orcamento.balanco(parseData(args[0])))
        },
        "m" to Comando(1) { args ->
            orcamento.valorMinimo = args[0].toDouble()
        },
        "v" to Comando(0) {
            println(if (orcamento.valido()) "Valido" else "Invalido")
        },
        "w" to Comando(2) { args ->
            val data = orcamento.primeiraDataComportavel(parseData(args[0]), args[1].toDouble())
            println(data ?: "Incomportavel")
        },
        "exit" to Comando(0) { terminarSessao() }
    )
}