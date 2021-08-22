package route

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

/**
 * ALB health check route
 */
fun Route.health() {
    get("/health") {
        call.respond(HttpStatusCode.NoContent)
    }
}
