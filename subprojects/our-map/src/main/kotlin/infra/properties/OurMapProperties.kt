package infra.properties

import java.util.Properties

object OurMapProperties {
    private val properties = javaClass.classLoader.getResourceAsStream("application.properties").use {
        it?.let {
            Properties().apply { load(it) }
        } ?: Properties()
    }

    operator fun get(key: String): String? {
        return properties.getProperty(key)
    }
}
