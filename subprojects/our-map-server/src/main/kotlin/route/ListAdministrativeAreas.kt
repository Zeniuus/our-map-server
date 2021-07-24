package route

import application.TransactionManager
import auth.UserAuthenticator
import domain.village.repository.EupMyeonDongRepository
import domain.village.repository.SiGunGuRepository
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.ListAdministrativeAreasResult
import ourMap.protocol.Model

fun Route.listAdministrativeAreas() {
    val koin = GlobalContext.getKoinApplicationOrNull()!!.koin

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val siGunGuRepository = koin.get<SiGunGuRepository>()
    val eupMyeonDongRepository = koin.get<EupMyeonDongRepository>()

    post("/listAdministrativeAreas") {
        userAuthenticator.checkAuth(call.request)

        call.respond(
            transactionManager.doInTransaction {
                val siGunGus = siGunGuRepository.listAll()
                val eupMyeonDongs = eupMyeonDongRepository.listAll()

                ListAdministrativeAreasResult.newBuilder()
                    .addAllSiGunGus(
                        siGunGus.map {
                            Model.SiGunGu.newBuilder()
                                .setId(it.id)
                                .setName(it.name)
                                .build()
                        }
                    )
                    .addAllEupMyeonDongs(
                        eupMyeonDongs.map {
                            Model.EupMyeonDong.newBuilder()
                                .setId(it.id)
                                .setName(it.name)
                                .setSiGunGuId(it.siGunGu.id)
                                .build()
                        }
                    )
                    .build()
            }
        )
    }
}
