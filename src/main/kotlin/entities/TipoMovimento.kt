package entities

enum class TipoMovimento(val simbolo: Char) {
    RECEITA('r'),
    DESPESA('d');
    companion object {
        private val porSimbolo: Map<Char, TipoMovimento> = entries.associateBy { it.simbolo };

        fun deSimbolo(texto: String): TipoMovimento =
            texto.singleOrNull()?.let{ porSimbolo[it] }
                ?: throw IllegalArgumentException("Tipo inválido: '$texto'. Use 'r' (receita) ou 'd' (despesa).")
    }

}