package magetower.town

import com.beust.klaxon.Klaxon
import kotlinx.serialization.Serializable
import magetower.TowerState
import magetower.contract.Contract
import magetower.contract.ContractTemplate
import magetower.contractPayment
import java.util.*
import kotlin.collections.ArrayList

@Serializable
class TownHall {


    private var contractTemplates : ArrayList<ContractTemplate> = ArrayList()
    private var currentContracts : ArrayList<Contract> = ArrayList()

    fun getCurrentContracts(day : Int, state : TowerState.TowerView) : List<Contract> {
        val stillCurrent = currentContracts.filter { it.expires > day }
        var newContracts = Random().nextInt(3) + 1 - stillCurrent.size
        newContracts = if(newContracts > contractTemplates.size) contractTemplates.size else newContracts
        if(newContracts > 0 ) {
            currentContracts = ArrayList(stillCurrent.plus(
                    contractTemplates
                    .shuffled().subList(0,newContracts)
                    .map { createContractFromTemplate(it, day, state) }))
        }
        return currentContracts
    }

    private fun createContractFromTemplate(t: ContractTemplate, day : Int, state : TowerState.TowerView): Contract {
        return Contract(t.title, t.description, contractPayment(day), day + 20).calculateRequirements(state)
    }

    fun takeContract(contract: Contract) {
        currentContracts.remove(contract)
    }

    init {
        val allContractTemplates =
                Klaxon().parseArray<ContractTemplate>(this.javaClass.classLoader.getResource("contracts.json").readText())
        contractTemplates = ArrayList(allContractTemplates!!)
    }


}