package route

import com.google.protobuf.util.JsonFormat

object ProtobufJsonConverter {
    val serializer = JsonFormat.printer()!!
    val deserializer = JsonFormat.parser().ignoringUnknownFields()!!
}
