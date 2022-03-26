package entities

enum class Currency(val course: Double) {
    RUB(1.0), USD(102.0), EUR(112.0);

    companion object {
        fun from(type: String?): Currency? = values().find { it.name == type }
    }

}