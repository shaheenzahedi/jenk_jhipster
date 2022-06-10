package org.aydm.danak.service.dto

import java.io.Serializable
import java.util.Objects
import java.util.UUID

/**
 * A DTO for the [org.aydm.danak.domain.HelpApp] entity.
 */
data class HelpAppDTO(

    var id: UUID? = null,

    var staticPageId: UUID? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HelpAppDTO) return false
        val helpAppDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, helpAppDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
