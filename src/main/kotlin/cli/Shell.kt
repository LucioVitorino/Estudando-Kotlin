package cli

import service.AuthService
import service.Autenticacao

class Shell(private val auth: AuthService) {
    private val separador = Regex("\\s+")
    private var atual: Autenticacao? = null
    private var comandos: Comandos = anonimos()

    fun correr() {
        while (true) {
            print(prompt())
            val linha = readlnOrNull()?.trim() ?: break
            if (linha.isBlank()) continue
            if (linha == "q") break          // termina o programa
            processar(linha)
        }
    }

    private fun processar(linha: String) {
        val partes = linha.split(separador)   // cada argumento é uma palavra, sem espaços
        val chave = partes.first()
        val args = partes.drop(1)

        val comando = comandos[chave]
        if (comando == null) {
            println("Comando desconhecido : $chave")
            return
        }
        if (args.size != comando.nArgs) {
            println("'$chave' espera ${comando.nArgs} argumento(s), recebeu ${args.size}.")
            return
        }
        try {
            comando.executar(args)
        } catch (e: IllegalArgumentException) {
            println("Erro: ${e.message}")
        }
    }

    private fun iniciarSessao(email: String, password: String) {
        val a = auth.iniciarSessao(email, password)
        atual = a
        comandos = comandosAutenticados(a.user) { terminarSessao() }   // troca de estado
        val quem = if (a.novo) "Utilizador criado. Sessão iniciada" else "Sessão iniciada"
        println("$quem (${a.user.email}). Token: ${a.sessao.token.take(8)}...")
    }

    private fun terminarSessao() {
        atual?.let { auth.terminarSessao(it) }
        atual = null
        comandos = anonimos()                                          // volta ao estado inicial
        println("Sessão terminada.")
    }

    private fun anonimos() = comandosAnonimos { email, password -> iniciarSessao(email, password) }

    private fun prompt() = atual?.let { "${it.user.email}> " } ?: "> "
}