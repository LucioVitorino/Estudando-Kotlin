package cli

// Antes de iniciar sessão só existe o comando "u"
fun comandosAnonimos(iniciarSessao: (String, String) -> Unit): Comandos = mapOf(
    "u" to Comando(2) { args -> iniciarSessao(args[0], args[1]) }
)