import cli.Shell
import repository.InMemoryUserRepository
import service.AuthService

fun main() {
    val repo = InMemoryUserRepository()
    val auth = AuthService(repo)
    Shell(auth).correr()
}