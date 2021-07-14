package infra.persistence.configuration

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

/**
 * https://www.baeldung.com/hibernate-naming-strategy
 */
class SnakeCasePhysicalNamingStrategy : PhysicalNamingStrategy {
    override fun toPhysicalCatalogName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return identifier?.let { convertToSnakeCase(it) }
    }

    override fun toPhysicalColumnName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return identifier?.let { convertToSnakeCase(it) }
    }

    override fun toPhysicalSchemaName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return identifier?.let { convertToSnakeCase(it) }
    }

    override fun toPhysicalSequenceName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return identifier?.let { convertToSnakeCase(it) }
    }

    override fun toPhysicalTableName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return identifier?.let { convertToSnakeCase(it) }
    }

    private fun convertToSnakeCase(identifier: Identifier): Identifier {
        val regex = "([a-z])([A-Z])".toRegex()
        val replacement = "$1_$2"
        val newName = identifier.text
            .replace(regex, replacement)
            .toLowerCase()
        return Identifier.toIdentifier(newName)
    }
}
