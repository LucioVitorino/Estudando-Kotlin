package cli

import entities.TipoMovimento
import entities.Movimento
import entities.User
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun parseData(texto: String): LocalDate =
    try {
        LocalDate.parse(texto)
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("Data inválida: '$texto'. Use o formato AAAA-MM-DD.")
    }

fun comandosAutenticados(user: User, terminarSessao: () -> Unit): Comandos = mapOf(
    "i" to Comando(5) { args ->
        val movimento = Movimento(
            montante = args[0].toDouble(),
            data = parseData(args[1]),
            periodicidade = args[2].toInt(),
            tipo = TipoMovimento.deSimbolo(args[3]),
            descricao = args[4]
        )
        user.inserirMovimento(movimento);
        println("Movimento inserido.")
    },
    "l" to Comando(1) { args ->
        val movimentos = user.listarMovimentos(parseData(args[0]))
        if (movimentos.isEmpty()) println("Sem movimentos nessa data.")
        else movimentos.forEach { println("${it.tipo} | ${it.montante} | ${it.descricao}") }
    },
    "exit" to Comando(0) { terminarSessao() }
)