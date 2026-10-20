package cli

typealias Handler = (List<String>) -> Unit
typealias Comandos = Map<String, Comando>

class Comando(val nArgs: Int, val executar: Handler);