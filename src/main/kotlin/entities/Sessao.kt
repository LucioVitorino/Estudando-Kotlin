package entities

import util.Hash
import java.time.LocalDateTime
import java.util.UUID

class Sessao private constructor(
    val token: String,
    val dataHora : LocalDateTime,
) {
    companion object {
        fun nova() : Sessao {
            val agora = LocalDateTime.now();
            val token = Hash.sha256("${UUID.randomUUID()}$agora");
            return Sessao(token, agora);
        }
    }
}