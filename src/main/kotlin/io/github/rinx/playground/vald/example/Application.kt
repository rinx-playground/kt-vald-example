package io.github.rinx.playground.vald.example

import com.google.gson.Gson
import io.grpc.ManagedChannelBuilder
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

data class Vector(val id: String, val vector: List<Float>)

data class SearchRequest(
    val num: Int,
    val radius: Float,
    val epsilon: Float,
    val vector: List<Float>
)

fun main() {
    val host = "localhost"
    val port = 8081

    val channel =
            ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .executor(Dispatchers.Default.asExecutor())
                    .build()

    val client = ValdClient(channel)

    embeddedServer(Netty, port = 8080) {
                install(ContentNegotiation) { gson { setPrettyPrinting() } }

                routing {
                    route("/insert") {
                        post {
                            var vec = call.receive<Vector>()
                            try {
                                val res = client.insert(vec.id, vec.vector)
                                call.respond(res)
                            } catch (e: Exception) {
                                call.respondText("error: $e")
                            }
                        }
                    }

                    route("/streaminsert") {
                        post {
                            var vecs = call.safeReceive<Array<Vector>>()
                            try {
                                val res = client.streamInsert(vecs.toList())
                                call.respondText("result: $res")
                            } catch (e: Exception) {
                                call.respondText("error: $e")
                            }
                        }
                    }

                    route("/search") {
                        post {
                            var r = call.receive<SearchRequest>()
                            try {
                                val res = client.search(r.num, r.radius, r.epsilon, r.vector)
                                call.respond(res)
                            } catch (e: Exception) {
                                call.respondText("error: $e")
                            }
                        }
                    }

                    route("/index") {
                        post {
                            try {
                                client.createIndex()
                                call.respondText("create index finished")
                            } catch (e: Exception) {
                                call.respondText("error: $e")
                            }
                        }
                    }
                }
            }
            .start(wait = true)
}

suspend inline fun <reified T> ApplicationCall.safeReceive(): T {
    val json = this.receiveOrNull<String>()
    return Gson().fromJson(json, T::class.java)
}
