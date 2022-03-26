package entities

import org.bson.Document

class Item constructor(private val id: Int, private val price: Double) {
    constructor(doc: Document, currency: Currency) : this(
        doc.getInteger("id"), currency.course * doc.getDouble("price")
    )

    fun toDocument(): Document = Document(mapOf(Pair("id", id), Pair("price", price)))

    override fun toString(): String = "Entities.Item $id: price = $price"
}