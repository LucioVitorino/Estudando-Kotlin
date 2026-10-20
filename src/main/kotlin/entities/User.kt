package entities

import util.Hash

class User(email: String, password: String) {
    val email: String = normalizarEmail(email)
    private val passwordHash: String

    val orcamento = Orcamento()
    private val _sessoes = mutableListOf<Sessao>()
    val sessoes: List<Sessao> get() = _sessoes

    init {
        require(this.email.isNotBlank() && "@" in this.email) { "Email inválido: '$email'." }
        require(password.isNotBlank()) { "A password não deve ser vazia." }
        passwordHash = Hash.sha256(password)
    }

    companion object {
        fun normalizarEmail(email: String) = email.trim().lowercase()
    }

    fun verificarPassword(password: String) = passwordHash == Hash.sha256(password)

    fun abrirSessao(): Sessao = Sessao.nova().also { _sessoes.add(it) }

    fun terminarSessao(sessao: Sessao) {
        require(_sessoes.remove(sessao)) { "Sessão inexistente." }
    }
}