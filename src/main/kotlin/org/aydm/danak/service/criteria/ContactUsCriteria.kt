package org.aydm.danak.service.criteria

import org.springdoc.api.annotations.ParameterObject
import tech.jhipster.service.Criteria
import tech.jhipster.service.filter.StringFilter
import tech.jhipster.service.filter.UUIDFilter
import tech.jhipster.service.filter.ZonedDateTimeFilter
import java.io.Serializable

/**
 * Criteria class for the [org.aydm.danak.domain.ContactUs] entity. This class is used in
 * [org.aydm.danak.web.rest.ContactUsResource] to receive all the possible filtering options from the
 * Http GET request parameters.
 * For example the following could be a valid request:
 * ```/contactuses?id.greaterThan=5&attr1.contains=something&attr2.specified=false```
 * As Spring is unable to properly convert the types, unless specific [Filter] class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
data class ContactUsCriteria(
    var id: UUIDFilter? = null,
    var userId: UUIDFilter? = null,
    var email: StringFilter? = null,
    var createTime: ZonedDateTimeFilter? = null,
    var distinct: Boolean? = null
) : Serializable, Criteria {

    constructor(other: ContactUsCriteria) :
        this(
            other.id?.copy(),
            other.userId?.copy(),
            other.email?.copy(),
            other.createTime?.copy(),
            other.distinct
        )

    override fun copy() = ContactUsCriteria(this)

    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
