package org.aydm.danak.domain

import org.hibernate.annotations.Type
import java.io.Serializable
import java.time.ZonedDateTime
import java.util.UUID
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A ContactUs.
 */

@Entity
@Table(name = "contact_us")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "contactus")
data class ContactUs(

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36)
    var id: UUID? = null,

    @Type(type = "uuid-char")
    @Column(name = "user_id", length = 36)
    var userId: UUID? = null,

    @get: NotNull
    @get: Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    @Column(name = "email", nullable = false)
    var email: String? = null,

    @Lob
    @Column(name = "message")
    var message: String? = null,

    @Column(name = "create_time")
    var createTime: ZonedDateTime? = null,
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContactUs) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "ContactUs{" +
            "id=" + id +
            ", userId='" + userId + "'" +
            ", email='" + email + "'" +
            ", message='" + message + "'" +
            ", createTime='" + createTime + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
