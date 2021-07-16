package io.github.rinx.playground.vald.example

import io.grpc.ManagedChannel
import java.io.Closeable
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.vdaas.vald.api.v1.agent.core.AgentGrpcKt
import org.vdaas.vald.api.v1.payload.Control
import org.vdaas.vald.api.v1.payload.Insert
import org.vdaas.vald.api.v1.payload.Object
import org.vdaas.vald.api.v1.payload.Search
import org.vdaas.vald.api.v1.vald.InsertGrpcKt
import org.vdaas.vald.api.v1.vald.SearchGrpcKt

class ValdClient(private val channel: ManagedChannel) : Closeable {
    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }

    suspend fun insert(id: String, v: List<Float>): Object.Location {
        val stub = InsertGrpcKt.InsertCoroutineStub(channel)

        val req =
                Insert.Request.newBuilder()
                        .setVector(Object.Vector.newBuilder().setId(id).addAllVector(v).build())
                        .setConfig(Insert.Config.newBuilder().setSkipStrictExistCheck(true).build())
                        .build()

        return stub.insert(req)
    }

    suspend fun streamInsert(vs: List<Vector>): List<Object.StreamLocation> {
        val stub = InsertGrpcKt.InsertCoroutineStub(channel)
        val requests = generateOutgoingInsertRequests(vs)

        val results = mutableListOf<Object.StreamLocation>()
        stub.streamInsert(requests).collect { loc -> results.add(loc) }

        return results
    }

    private fun generateOutgoingInsertRequests(vs: List<Vector>): Flow<Insert.Request> = flow {
        vs.forEach { v ->
            emit(
                    Insert.Request.newBuilder()
                            .setVector(
                                    Object.Vector.newBuilder()
                                            .setId(v.id)
                                            .addAllVector(v.vector)
                                            .build()
                            )
                            .setConfig(
                                    Insert.Config.newBuilder().setSkipStrictExistCheck(true).build()
                            )
                            .build()
            )
        }
    }

    suspend fun search(num: Int, radius: Float, epsilon: Float, v: List<Float>): Search.Response {
        val stub = SearchGrpcKt.SearchCoroutineStub(channel)

        val req =
                Search.Request.newBuilder()
                        .addAllVector(v)
                        .setConfig(
                                Search.Config.newBuilder()
                                        .setNum(num)
                                        .setRadius(radius)
                                        .setEpsilon(epsilon)
                                        .build()
                        )
                        .build()

        return stub.search(req)
    }

    suspend fun createIndex() {
        val stub = AgentGrpcKt.AgentCoroutineStub(channel)

        stub.createIndex(Control.CreateIndexRequest.newBuilder().setPoolSize(10000).build())
    }
}
