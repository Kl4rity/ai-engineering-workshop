package stift.io.home.domain

import dev.langchain4j.agent.tool.Tool
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

interface DurablePantry {
    fun findAll(): List<PantryService.PantryEntry>
    fun findAllWhereNamesExist(
        names: List<String>
    ): List<PantryService.PantryEntry>

    fun saveAll(entries: List<PantryService.PantryEntry>): List<PantryService.PantryEntry>
}

@Service
class PantryService(
    private val durablePantry: DurablePantry,
) {
    data class LineItem(val name: String, val amount: Double, val unit: String)

    data class StorageRequest(val items: List<LineItem>)

    data class UseFoodRequest(val items: List<LineItem>)

    data class PantryEntry(
        val id: UUID,
        val name: String,
        val amount: Double,
        val unit: String
    )

    @Tool(
        """
        Save food items with quantities or update inventory to match stated amounts.
        This method OVERRIDES current quantities. It is idempotent and analogous to a PATCH in HTTP.
        
        WORKFLOW:
        1. Check existing inventory with getFood() first
        2. Match similar items (e.g., "coffee" might match "coffee beans")
        3. Use exact item names from inventory when matching
        4. Only create new entries when no match exists
        
        Use for:
        - Recording purchases
        - Updating inventory statements (e.g., "I have X of Y")
        
        Units: Use "ml"/"liter" for liquids, "g"/"kg" for weight, "piece" for countable items, 
        "loaf" for bread, "bunch" for grapes/bananas. Default to "piece" when unsure.
        
        Use singular forms and simple English categories. Combine same-type items.
        """
    )
    fun saveFood(request: StorageRequest): List<PantryEntry> {
        logger.info("Incoming save food Request: $request")
        val saveRequestList = request.items
        val existingEntries =
            durablePantry.findAllWhereNamesExist(request.items.map { it.name })

        val updateList =
            saveRequestList.map { entryToSave ->
                existingEntries
                    .find { it.name == entryToSave.name }
                    ?.copy(amount = entryToSave.amount, unit = entryToSave.unit)
                    ?: PantryEntry(
                        id = UUID.randomUUID(),
                        name = entryToSave.name,
                        amount = entryToSave.amount,
                        unit = entryToSave.unit
                    )
            }

        val savedEntries = durablePantry.saveAll(updateList)

        return savedEntries
    }

    @Tool(
        """
        Use saved food ONLY when items are consumed, used up, thrown away, or removed from inventory.
        
        WORKFLOW:
        1. Check inventory with getFood() first
        2. Match similar items (e.g., "coffee" might match "coffee beans")
        3. Use exact item names from inventory
        4. Only use items with a match in inventory
        
        DO NOT use for inventory statements like "I have X of Y" - use saveFood instead.
        
        Units: "ml"/"liter" for liquids, "g"/"kg" for weight, "piece" for countable items.
        Default to "piece" when unsure.
        
        Always check inventory first to avoid errors.
        """
    )
    fun useFood(request: UseFoodRequest): List<PantryEntry> {
        logger.info("Incoming use food Request: $request")
        val existingEntries =
            durablePantry.findAllWhereNamesExist(request.items.map { it.name })

        val updatedEntries =
            request.items.mapNotNull { itemToUse ->
                existingEntries
                    .find { it.name == itemToUse.name }
                    ?.let { existingItem ->
                        val newAmount = maxOf(0.0, existingItem.amount - itemToUse.amount)
                        existingItem.copy(amount = newAmount)
                    }
                    ?.also {
                        if (it.amount == 0.0) {
                            logger.info("Item ${it.name} is now depleted")
                        }
                    }
                    ?: run {
                        logger.error("Item not in storage: ${itemToUse.name}")
                        null
                    }
            }

        val savedEntries =
            if (updatedEntries.isNotEmpty()) {
                durablePantry.saveAll(updatedEntries)
            } else {
                emptyList()
            }

        return savedEntries
    }

    @Tool("""
        Get list of available food items with their quantities.
        ALWAYS call this function before any saveFood or useFood operation to check what's already in the inventory.
        Use the results to match similar items and update existing entries instead of creating new ones.
    """)
    fun getFood(): List<PantryEntry> {
        return durablePantry.findAll()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
