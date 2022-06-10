package org.aydm.danak.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.aydm.danak.domain.enumeration.StaticPageStatus
import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.UUID
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A StaticPage.
 */

@Entity
@Table(name = "static_page")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "staticpage")
data class StaticPage(

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36)
    var id: UUID? = null,

    @get: NotNull

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Lob
    @Column(name = "content", nullable = false)
    var content: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: StaticPageStatus? = null,

    @Type(type = "uuid-char")
    @Column(name = "file_id", length = 36)
    var fileId: UUID? = null,

    @ManyToOne
    @JsonIgnoreProperties(
        value = [
            "staticPageIds",
        ],
        allowSetters = true
    )
    var helpApp: HelpApp? = null,
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun helpApp(helpApp: HelpApp?): StaticPage {
        this.helpApp = helpApp
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StaticPage) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "StaticPage{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", content='" + content + "'" +
            ", status='" + status + "'" +
            ", fileId='" + fileId + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
