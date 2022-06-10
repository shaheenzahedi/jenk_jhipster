package org.aydm.danak.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.UUID
import javax.persistence.*

/**
 * A HelpApp.
 */

@Entity
@Table(name = "help_app")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "helpapp")
data class HelpApp(

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36)
    var id: UUID? = null,

    @Type(type = "uuid-char")
    @Column(name = "static_page_id", length = 36)
    var staticPageId: UUID? = null,

    @OneToMany(mappedBy = "helpApp")
    @JsonIgnoreProperties(
        value = [
            "helpApp",
        ],
        allowSetters = true
    )
    var staticPageIds: MutableSet<StaticPage>? = mutableSetOf(),
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addStaticPageId(staticPage: StaticPage): HelpApp {
        this.staticPageIds?.add(staticPage)
        staticPage.helpApp = this
        return this
    }
    fun removeStaticPageId(staticPage: StaticPage): HelpApp {
        this.staticPageIds?.remove(staticPage)
        staticPage.helpApp = null
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HelpApp) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "HelpApp{" +
            "id=" + id +
            ", staticPageId='" + staticPageId + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
