package route

import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.http.ContentType
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.jvm.jvmErasure

class ProtobufJsonContentConverter : ContentConverter {
    // ktor의 JacksonConverter를 참고하여 구현함.
    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val value = request.value as? ByteReadChannel ?: return null

        return withContext(Dispatchers.IO) {
            val reader = value.toInputStream().reader(context.call.request.contentCharset() ?: Charsets.UTF_8)
            reader.use {
                val messageOrBuilderClazz = request.typeInfo.jvmErasure.javaObjectType
                val builder = messageOrBuilderClazz.getMethod("newBuilder").invoke(null) as Message.Builder
                ProtobufJsonConverter.deserializer.merge(it.readText(), builder)
                builder.build()
            }
        }
    }

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any {
        return ProtobufJsonConverter.serializer.print(value as MessageOrBuilder)
    }
}
