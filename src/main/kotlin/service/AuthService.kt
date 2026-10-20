package service

import entities.Sessao
import entities.User
import repository.UserRepository

class Autenticacao(val user: User, val sessao: Sessao, val novo: Boolean)

class AuthService(private val repository: UserRepository) {

    fun iniciarSessao(email: String, password: String) : Autenticacao {
        val existente = repository.buscarPorEmail(email);

        val user: User;
        val novo: Boolean;
        if (existente == null)
        {
            user = User(email, password);
            repository.guardar(user);
            novo = true;
        }else{
            require(existente.verificarPassword(password)) {"Email ou password incorreta"};
            user = existente;
            novo = false;
        }
        return Autenticacao(user, user.abrirSessao(), novo);
    }


    fun terminarSessao(autenticacao: Autenticacao) =
        autenticacao.user.terminarSessao(autenticacao.sessao);
}