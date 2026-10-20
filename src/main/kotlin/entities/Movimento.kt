package entities

import java.time.LocalDate

class Movimento(
    val montante : Double,
    val data : LocalDate,
    val periodicidade : Int,
    val tipo : TipoMovimento,
    val descricao : String
){

    init{
        require(montante >= 0){"O montante precisa ser um valor positivo"}
        require(periodicidade >= 0){"O periodicidade deve ser um valor positivo"}
        require(descricao.isNotBlank()){"o Movimento precisa conter uma descrição"}
        require(descricao.length <= MAX_DESCRICAO){"A descrição pode ter no máximo $MAX_DESCRICAO caracteres (tem ${descricao.length})."};
    }

    val unico: Boolean get() = periodicidade == 0;

    companion object{
        const val MAX_DESCRICAO = 60;
    }
}