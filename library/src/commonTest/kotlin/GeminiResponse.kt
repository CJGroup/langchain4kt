import kotlinx.serialization.Serializable

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
){
    override fun toString(): String {
        return """
            Response(
                candidates=$candidates, 
                usageMetadata=$usageMetadata)
            """
    }
}

@Serializable
data class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int,
){
    override fun toString(): String {
        return """
            UsageMetadata(
                promptTokenCount=$promptTokenCount, 
                candidatesTokenCount=$candidatesTokenCount, 
                totalTokenCount=$totalTokenCount)
            """
    }
}

@Serializable
data class Candidate(
    val content: GeminiContent,
    val finishReason: String,
    val index: Int,
    val safetyRatings: List<SafetyRating>,
){
    override fun toString(): String {
        return """
            Candidate(
                content=$content, 
                finishReason=$finishReason, 
                index=$index, 
                safetyRatings=$safetyRatings)
            """
    }
}

@Serializable
data class GeminiContent(
    val parts: List<MutableMap<String, String>>,
    val role: String
){
    override fun toString(): String {
        return """
            Content(
                parts=$parts, 
                role=$role)
            """
    }
}

var GeminiContent.text: String
    get() = parts[0]["text"]!!
    set(value) {
        parts[0]["text"] = value
    }

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
){
    override fun toString(): String {
        return """
            SafetyRating(
                category=$category, 
                probability=$probability)
            """
    }
}