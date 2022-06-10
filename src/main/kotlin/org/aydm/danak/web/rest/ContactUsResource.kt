package org.aydm.danak.web.rest

import org.aydm.danak.repository.ContactUsRepository
import org.aydm.danak.service.ContactUsQueryService
import org.aydm.danak.service.ContactUsService
import org.aydm.danak.service.criteria.ContactUsCriteria
import org.aydm.danak.service.dto.ContactUsDTO
import org.aydm.danak.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "contactUs"
/**
 * REST controller for managing [org.aydm.danak.domain.ContactUs].
 */
@RestController
@RequestMapping("/api")
class ContactUsResource(
    private val contactUsService: ContactUsService,
    private val contactUsRepository: ContactUsRepository,
    private val contactUsQueryService: ContactUsQueryService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "contactUs"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /contactuses` : Create a new contactUs.
     *
     * @param contactUsDTO the contactUsDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new contactUsDTO, or with status `400 (Bad Request)` if the contactUs has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/contactuses")
    fun createContactUs(@Valid @RequestBody contactUsDTO: ContactUsDTO): ResponseEntity<ContactUsDTO> {
        log.debug("REST request to save ContactUs : $contactUsDTO")
        if (contactUsDTO.id != null) {
            throw BadRequestAlertException(
                "A new contactUs cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = contactUsService.save(contactUsDTO)
        return ResponseEntity.created(URI("/api/contactuses/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /contactuses/:id} : Updates an existing contactUs.
     *
     * @param id the id of the contactUsDTO to save.
     * @param contactUsDTO the contactUsDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated contactUsDTO,
     * or with status `400 (Bad Request)` if the contactUsDTO is not valid,
     * or with status `500 (Internal Server Error)` if the contactUsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/contactuses/{id}")
    fun updateContactUs(
        @PathVariable(value = "id", required = false) id: UUID,
        @Valid @RequestBody contactUsDTO: ContactUsDTO
    ): ResponseEntity<ContactUsDTO> {
        log.debug("REST request to update ContactUs : {}, {}", id, contactUsDTO)
        if (contactUsDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, contactUsDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!contactUsRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = contactUsService.update(contactUsDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    contactUsDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /contactuses/:id} : Partial updates given fields of an existing contactUs, field will ignore if it is null
     *
     * @param id the id of the contactUsDTO to save.
     * @param contactUsDTO the contactUsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contactUsDTO,
     * or with status {@code 400 (Bad Request)} if the contactUsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the contactUsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the contactUsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/contactuses/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateContactUs(
        @PathVariable(value = "id", required = false) id: UUID,
        @NotNull @RequestBody contactUsDTO: ContactUsDTO
    ): ResponseEntity<ContactUsDTO> {
        log.debug("REST request to partial update ContactUs partially : {}, {}", id, contactUsDTO)
        if (contactUsDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, contactUsDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!contactUsRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = contactUsService.partialUpdate(contactUsDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contactUsDTO.id.toString())
        )
    }

    /**
     * `GET  /contactuses` : get all the contactuses.
     *

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of contactuses in body.
     */
    @GetMapping("/contactuses") fun getAllContactuses(
        criteria: ContactUsCriteria
    ): ResponseEntity<MutableList<ContactUsDTO>> {
        log.debug("REST request to get Contactuses by criteria: $criteria")
        val entityList = contactUsQueryService.findByCriteria(criteria)
        return ResponseEntity.ok().body(entityList)
    }

    /**
     * `GET  /contactuses/count}` : count all the contactuses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/contactuses/count")
    fun countContactuses(criteria: ContactUsCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Contactuses by criteria: $criteria")
        return ResponseEntity.ok().body(contactUsQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /contactuses/:id` : get the "id" contactUs.
     *
     * @param id the id of the contactUsDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the contactUsDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/contactuses/{id}")
    fun getContactUs(@PathVariable id: UUID): ResponseEntity<ContactUsDTO> {
        log.debug("REST request to get ContactUs : $id")
        val contactUsDTO = contactUsService.findOne(id)
        return ResponseUtil.wrapOrNotFound(contactUsDTO)
    }
    /**
     *  `DELETE  /contactuses/:id` : delete the "id" contactUs.
     *
     * @param id the id of the contactUsDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/contactuses/{id}")
    fun deleteContactUs(@PathVariable id: UUID): ResponseEntity<Void> {
        log.debug("REST request to delete ContactUs : $id")

        contactUsService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/contactuses?query=:query` : search for the contactUs corresponding
     * to the query.
     *
     * @param query the query of the contactUs search.
     * @return the result of the search.
     */
    @GetMapping("/_search/contactuses")
    fun searchContactuses(@RequestParam query: String): MutableList<ContactUsDTO> {
        log.debug("REST request to search Contactuses for query $query")
        return contactUsService.search(query)
    }
}
