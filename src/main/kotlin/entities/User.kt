package entities

import org.bson.Document

class User(val id: Int, val currency: Currency) {
    constructor(document: Document) : this(document.getInteger("id"), Currency.valueOf(document.getString("currency")))

    override fun toString(): String = "Entities.User{id=$id, currency=$currency}"

    fun toDocument(): Document = Document(mapOf(Pair("id", id), Pair("currency", currency.toString())))
}