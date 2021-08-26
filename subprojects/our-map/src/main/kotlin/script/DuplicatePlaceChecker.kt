package script

import application.TransactionManager
import domain.place.entity.Place
import infra.persistence.transaction.EntityManagerHolder
import org.koin.core.context.GlobalContext

fun main() {
    OurMapIoCFactory.configGlobally { this }
    val koin = GlobalContext.get()
    val transactionManager = koin.get<TransactionManager>()
    val places = transactionManager.doInTransaction {
        val em = EntityManagerHolder.get()!!
        em.createQuery("SELECT p FROM Place p JOIN FETCH p.building b", Place::class.java).resultList
    }
    val duplicatedPlacesList = listDuplicatedPlaces(places)
    transactionManager.doInTransaction {
        duplicatedPlacesList.forEach { duplicatedPlaces ->
            println(
                duplicatedPlaces.joinToString(" / ") {
                    "${it.address} ${it.name}"
                }
            )
        }
//        val em = EntityManagerHolder.get()!!
//        duplicatedPlacesList.flatMap { it.dropLast(1) }.forEach {
//            em.createQuery("DELETE FROM Place where id = :placeId").setParameter("placeId", it.id).resultList
//        }
    }
}

private fun listDuplicatedPlaces(places: List<Place>): List<List<Place>> {
    val duplicatedPlacePairs = places.flatMapIndexed { i1, p1 ->
        places.mapIndexedNotNull { i2, p2 ->
            if (i1 >= i2) {
                return@mapIndexedNotNull null
            }
            if (p1.name.replace(" ", "") == p2.name.replace(" ", "") &&
                p1.address.siDo == p2.address.siDo &&
                p1.address.siGunGu == p2.address.siGunGu &&
                p1.address.roadName == p2.address.roadName) {
                listOf(p1, p2)
            } else {
                null
            }
        }
    }

    return duplicatedPlacePairs.fold(emptyList()) { acc, (p1, p2) ->
        if (acc.any { places -> places.any { it.id == p1.id } }) {
            return@fold acc
        }

        val p1DuplicatedPlacesList = duplicatedPlacePairs.fold(setOf(p1, p2)) { p1DuplicatedPlacesSet, (currP1, currP2) ->
            if (p1DuplicatedPlacesSet.any { it.id == currP1.id || it.id == currP2.id }) {
                p1DuplicatedPlacesSet + currP1 + currP2
            } else {
                p1DuplicatedPlacesSet
            }
        }.toList()

        val temp = acc.toMutableList()
        temp.add(p1DuplicatedPlacesList)
        temp.toList()
    }
}
