package ragalik.brain.balinasoft.data.vo


data class Content (

    var id: Int? = null,

    var name: String? = null,

    var image: String? = null
)

data class TypeResponse (

    var content: List<Content>? = null
)