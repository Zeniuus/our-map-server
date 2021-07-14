import infra.persistence.schema.LiquibaseMigrator

fun main() {
    LiquibaseMigrator.dropAndMigrate()
}
