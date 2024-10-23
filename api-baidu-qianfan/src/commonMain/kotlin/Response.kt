import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("session_key")
    val sessionKey: String,
    @SerialName("access_token")
    val accessToken: String,
    val scope: String,
    @SerialName("session_secret")
    val sessionSecret: String
)

@Serializable
data class AccessTokenError(
    @SerialName("error_description")
    val errorDescription: String,
    val error: String
)