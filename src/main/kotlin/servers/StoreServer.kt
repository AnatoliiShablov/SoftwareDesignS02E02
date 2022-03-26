package servers

import entities.Currency
import entities.Item
import entities.User
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.HttpMethod
import io.reactivex.netty.protocol.http.server.HttpServer
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import rx.Observable

class StoreServer(port: Int) {
    fun awaitShutdown() {
        httpServer.awaitShutdown()
    }

    private fun processRequest(request: HttpServerRequest<ByteBuf>): Observable<String> {
        return when (request.httpMethod) {
            HttpMethod.GET -> return when (request.decodedPath) {
                "/list_items" -> {
                    val id = request.queryParameters["id"]?.get(0)?.toIntOrNull()
                    return if (id != null) {
                        mongoServer.getUser(id).flatMap { mongoServer.getAllItems(it.currency) }
                            .map { it.toString() + "\r\n" }.onErrorReturn { "No such user: $id" }
                    } else {
                        Observable.just("No id parameter in query")
                    }
                }
                "/create_user" -> {
                    val id = request.queryParameters["id"]?.get(0)?.toIntOrNull()
                    val currency = Currency.from(request.queryParameters["currency"]?.get(0))
                    return if (id != null && currency != null) {
                        mongoServer.createUser(User(id, currency)).map { "User created $id: $currency" }
                            .onErrorReturn { "Unable to create user: $it" }
                    } else {
                        Observable.just("No id or/and currency parameter in query")
                    }
                }
                "/create_item" -> {
                    val id = request.queryParameters["id"]?.get(0)?.toIntOrNull()
                    val price = request.queryParameters["price"]?.get(0)?.toDoubleOrNull()
                    return if (id != null && price != null) {
                        mongoServer.createItem(Item(id, price)).map { "Item created $id: $price" }
                            .onErrorReturn { "Unable to create item: $it" }
                    } else {
                        Observable.just("No id or/and price parameter in query")
                    }
                }
                else -> Observable.just(request.decodedPath.substring(1)).map { "There is no such page: $it" }
            }
            else -> Observable.just("Unsupported method")
        }
    }

    private val httpServer = HttpServer.newServer(port).start { request, response ->
        response.writeString(processRequest(request))
    }

    private val mongoServer = MongoDB()
}