import servers.StoreServer

fun main(args: Array<String>) {
    StoreServer(8080).awaitShutdown()
}
