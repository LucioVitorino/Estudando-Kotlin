package entities

import util.Hash
import java.time.LocalDate

class User(email: String, password: String) {
    val email: String = normalizarEmail(email)
    private val passwordHash: String

    private val _movimentos = mutableListOf<Movimento>()
    private val _sessoes = mutableListOf<Sessao>()

    // Só leitura para quem está de fora; a lista mutável fica privada
    val movimentos: List<Movimento> get() = _movimentos
    val sessoes: List<Sessao> get() = _sessoes

    init {
        require(this.email.isNotBlank() && "@" in this.email) { "Email inválido: '$email'." }
        require(password.isNotBlank()) { "A password não deve ser vazia." }
        passwordHash = Hash.sha256(password)
    }

    companion object {
        // Mesma regra em todo o lado: Ana@x.com e ana@x.com são o mesmo email
        fun normalizarEmail(email: String) = email.trim().lowercase()
    }

    fun verificarPassword(password: String) = passwordHash == Hash.sha256(password)

    fun abrirSessao(): Sessao = Sessao.nova().also { _sessoes.add(it) }

    fun terminarSessao(sessao: Sessao) {
        require(_sessoes.remove(sessao)) { "Sessão inexistente." }
    }

    fun inserirMovimento(movimento: Movimento) {
        _movimentos.add(movimento);
    }

    // Devolve dados em vez de imprimir: quem mostra é a camada cli
    fun listarMovimentos(data: LocalDate): List<Movimento> = _movimentos.filter { it.data == data }
}