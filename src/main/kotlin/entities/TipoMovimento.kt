package entities

enum class TipoMovimento(val simbolo: Char, val sinal: Int) {
    RECEITA('r', 1),
    DESPESA('d', -1);
    companion object {
        private val porSimbolo: Map<Char, TipoMovimento> = entries.associateBy { it.simbolo };

        fun deSimbolo(texto: String): TipoMovimento =
            texto.singleOrNull()?.let{ porSimbolo[it] }
                ?: throw IllegalArgumentException("Tipo inválido: '$texto'. Use 'r' (receita) ou 'd' (despesa).")
    }

}