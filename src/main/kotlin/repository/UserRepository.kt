package repository

import entities.User

interface UserRepository {
    fun buscarPorEmail(email: String): User?;
    fun guardar(user: User);
}