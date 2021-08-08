package infra.properties

import java.io.File
import java.util.Properties

object OurMapProperties {
    private val defaultProperties = loadProperties("application.properties")
    private var overridingProperties: Properties? = null

    init {
        System.getenv("OUR_MAP_OVERRIDING_PROPERTIES_FILENAME")?.let {
            overridingProperties = loadProperties(it)
        }
    }

    private fun loadProperties(propertiesFilename: String): Properties {
        val resource = javaClass.classLoader.getResourceAsStream(propertiesFilename)
        if (resource != null) {
            return resource.use {
                it.let {
                    Properties().apply { load(it) }
                }
            }
        }
        val file = File(propertiesFilename)
        if (file.exists()) {
            return file.inputStream().use {
                it.let {
                    Properties().apply { load(it) }
                }
            }
        }
        return Properties()
    }

    operator fun get(key: String): String? {
        return overridingProperties?.getProperty(key) ?: defaultProperties.getProperty(key)
    }
}
