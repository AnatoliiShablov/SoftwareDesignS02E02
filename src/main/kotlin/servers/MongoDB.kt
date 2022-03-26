package servers

import entities.Currency
import entities.Item
import entities.User
import com.mongodb.client.model.IndexOptions
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.Success
import org.bson.Document
import rx.Observable

class MongoDB {
    fun getAllItems(currency: Currency): Observable<Item> = items.find().toObservable().map { Item(it, currency) }
    fun getUser(id: Int): Observable<User> =
        users.find().toObservable().map { User(it) }.filter { it.id == id }.single()

    fun createItem(item: Item): Observable<Success> = items.insertOne(item.toDocument())
    fun createUser(user: User): Observable<Success> = users.insertOne(user.toDocument())


    private val db = MongoClients.create("mongodb://localhost:27017").getDatabase("S02E02")
    private val items = db.getCollection("items")
    private val users = db.getCollection("users")

    init {
        items.createIndex(Document("id", 1), IndexOptions().unique(true)).toBlocking().single()
        users.createIndex(Document("id", 1), IndexOptions().unique(true)).toBlocking().single()
    }

    fun dropBase() {
        // For testing purposes
        items.drop().toBlocking().single()
        users.drop().toBlocking().single()
    }
}