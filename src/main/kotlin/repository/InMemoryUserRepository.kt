package repository

import entities.User

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>();

    override  fun buscarPorEmail(email: String): User? =
        users[User.normalizarEmail(email)]

    override fun guardar(user: User){
        require(user.email !in users){"O email ${user.email} já está registrado"};
        users[user.email] = user;
    }
}