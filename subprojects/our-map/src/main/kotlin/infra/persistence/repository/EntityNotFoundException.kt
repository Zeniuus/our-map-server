package infra.persistence.repository

class EntityNotFoundException(val msg: String) : RuntimeException(msg)
