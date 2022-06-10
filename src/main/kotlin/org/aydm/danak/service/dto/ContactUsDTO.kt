package org.aydm.danak.service.dto

import java.io.Serializable
import java.time.ZonedDateTime
import java.util.Objects
import java.util.UUID
import javax.persistence.Lob
import javax.validation.constraints.*

/**
 * A DTO for the [org.aydm.danak.domain.ContactUs] entity.
 */
data class ContactUsDTO(

    var id: UUID? = null,

    var userId: UUID? = null,

    @get: NotNull
    @get: Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    var email: String? = null,

    @Lob var message: String? = null,

    var createTime: ZonedDateTime? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContactUsDTO) return false
        val contactUsDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, contactUsDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
