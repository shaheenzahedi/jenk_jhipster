package org.aydm.danak.service.dto

import org.aydm.danak.domain.enumeration.StaticPageStatus
import java.io.Serializable
import java.util.Objects
import java.util.UUID
import javax.persistence.Lob
import javax.validation.constraints.*

/**
 * A DTO for the [org.aydm.danak.domain.StaticPage] entity.
 */
data class StaticPageDTO(

    var id: UUID? = null,

    @get: NotNull
    var name: String? = null,

    @Lob var content: String? = null,

    var status: StaticPageStatus? = null,

    var fileId: UUID? = null,

    var helpApp: HelpAppDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StaticPageDTO) return false
        val staticPageDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, staticPageDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
