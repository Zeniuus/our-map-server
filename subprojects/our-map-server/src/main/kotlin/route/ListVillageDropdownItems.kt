package route

import application.TransactionManager
import application.village.VillageApplicationService
import auth.UserAuthenticator
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.core.context.GlobalContext
import ourMap.protocol.ListVillageDropdownItemsResult

fun Route.listVillageDropdownItems() {
    val koin = GlobalContext.get()

    val transactionManager = koin.get<TransactionManager>()
    val userAuthenticator = koin.get<UserAuthenticator>()
    val villageApplicationService = koin.get<VillageApplicationService>()

    post("/listVillageDropdownItems") {
        userAuthenticator.checkAuth(call.request)

        val villageDropdownItems = villageApplicationService.listDropdownItems()

        call.respond(
            transactionManager.doInTransaction {

                ListVillageDropdownItemsResult.newBuilder()
                    .addAllVillageDropdownItems(
                        villageDropdownItems.map { dropdownItem ->
                            ListVillageDropdownItemsResult.VillageDropdownItem.newBuilder()
                                .setVillageId(dropdownItem.village.id)
                                .setVillageName(dropdownItem.villageName)
                                .build()
                        }
                    )
            }
        )
    }
}
