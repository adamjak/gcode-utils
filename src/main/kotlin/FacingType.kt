enum class FacingType {
    RECTANGLE,
    CIRCLE;

    companion object {
        fun getTypeFromString(str: String): FacingType? {
            return values().firstOrNull { ft -> ft.name.equals(str, true) }
        }
    }
}