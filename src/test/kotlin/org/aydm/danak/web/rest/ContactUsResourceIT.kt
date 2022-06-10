package org.aydm.danak.web.rest

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.IntegrationTest
import org.aydm.danak.domain.ContactUs
import org.aydm.danak.repository.ContactUsRepository
import org.aydm.danak.repository.search.ContactUsSearchRepository
import org.aydm.danak.service.mapper.ContactUsMapper
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import java.util.stream.Stream
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [ContactUsResource] REST controller.
 */
@IntegrationTest
@Extensions(
    ExtendWith(MockitoExtension::class)
)
@AutoConfigureMockMvc
@WithMockUser
class ContactUsResourceIT {
    @Autowired
    private lateinit var contactUsRepository: ContactUsRepository

    @Autowired
    private lateinit var contactUsMapper: ContactUsMapper

    /**
     * This repository is mocked in the org.aydm.danak.repository.search test package.
     *
     * @see org.aydm.danak.repository.search.ContactUsSearchRepositoryMockConfiguration
     */
    @Autowired
    private lateinit var mockContactUsSearchRepository: ContactUsSearchRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restContactUsMockMvc: MockMvc

    private lateinit var contactUs: ContactUs

    @BeforeEach
    fun initTest() {
        contactUs = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createContactUs() {
        val databaseSizeBeforeCreate = contactUsRepository.findAll().size
        // Create the ContactUs
        val contactUsDTO = contactUsMapper.toDto(contactUs)
        restContactUsMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(contactUsDTO))
        ).andExpect(status().isCreated)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeCreate + 1)
        val testContactUs = contactUsList[contactUsList.size - 1]

        assertThat(testContactUs.userId).isEqualTo(DEFAULT_USER_ID)
        assertThat(testContactUs.email).isEqualTo(DEFAULT_EMAIL)
        assertThat(testContactUs.message).isEqualTo(DEFAULT_MESSAGE)
        assertThat(testContactUs.createTime).isEqualTo(DEFAULT_CREATE_TIME)

        // Validate the ContactUs in Elasticsearch
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createContactUsWithExistingId() {
        // Create the ContactUs with an existing ID
        contactUsRepository.saveAndFlush(contactUs)
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        val databaseSizeBeforeCreate = contactUsRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactUsMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(contactUsDTO))
        ).andExpect(status().isBadRequest)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeCreate)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(0)).save(contactUs)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkEmailIsRequired() {
        val databaseSizeBeforeTest = contactUsRepository.findAll().size
        // set the field null
        contactUs.email = null

        // Create the ContactUs, which fails.
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        restContactUsMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(contactUsDTO))
        ).andExpect(status().isBadRequest)

        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactuses() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList
        restContactUsMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactUs.id.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getContactUs() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        val id = contactUs.id
        assertNotNull(id)

        // Get the contactUs
        restContactUsMockMvc.perform(get(ENTITY_API_URL_ID, contactUs.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contactUs.id.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE.toString()))
            .andExpect(jsonPath("$.createTime").value(sameInstant(DEFAULT_CREATE_TIME)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getContactusesByIdFiltering() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)
        val id = contactUs.id

        defaultContactUsShouldBeFound("id.equals=$id")
        defaultContactUsShouldNotBeFound("id.notEquals=$id")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByUserIdIsEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where userId equals to DEFAULT_USER_ID
        defaultContactUsShouldBeFound("userId.equals=$DEFAULT_USER_ID")

        // Get all the contactUsList where userId equals to UPDATED_USER_ID
        defaultContactUsShouldNotBeFound("userId.equals=$UPDATED_USER_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByUserIdIsNotEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where userId not equals to DEFAULT_USER_ID
        defaultContactUsShouldNotBeFound("userId.notEquals=$DEFAULT_USER_ID")

        // Get all the contactUsList where userId not equals to UPDATED_USER_ID
        defaultContactUsShouldBeFound("userId.notEquals=$UPDATED_USER_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByUserIdIsInShouldWork() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where userId in DEFAULT_USER_ID or UPDATED_USER_ID
        defaultContactUsShouldBeFound("userId.in=$DEFAULT_USER_ID,$UPDATED_USER_ID")

        // Get all the contactUsList where userId equals to UPDATED_USER_ID
        defaultContactUsShouldNotBeFound("userId.in=$UPDATED_USER_ID")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByUserIdIsNullOrNotNull() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where userId is not null
        defaultContactUsShouldBeFound("userId.specified=true")

        // Get all the contactUsList where userId is null
        defaultContactUsShouldNotBeFound("userId.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByEmailIsEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where email equals to DEFAULT_EMAIL
        defaultContactUsShouldBeFound("email.equals=$DEFAULT_EMAIL")

        // Get all the contactUsList where email equals to UPDATED_EMAIL
        defaultContactUsShouldNotBeFound("email.equals=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByEmailIsNotEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where email not equals to DEFAULT_EMAIL
        defaultContactUsShouldNotBeFound("email.notEquals=$DEFAULT_EMAIL")

        // Get all the contactUsList where email not equals to UPDATED_EMAIL
        defaultContactUsShouldBeFound("email.notEquals=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByEmailIsInShouldWork() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultContactUsShouldBeFound("email.in=$DEFAULT_EMAIL,$UPDATED_EMAIL")

        // Get all the contactUsList where email equals to UPDATED_EMAIL
        defaultContactUsShouldNotBeFound("email.in=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByEmailIsNullOrNotNull() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where email is not null
        defaultContactUsShouldBeFound("email.specified=true")

        // Get all the contactUsList where email is null
        defaultContactUsShouldNotBeFound("email.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByEmailContainsSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where email contains DEFAULT_EMAIL
        defaultContactUsShouldBeFound("email.contains=$DEFAULT_EMAIL")

        // Get all the contactUsList where email contains UPDATED_EMAIL
        defaultContactUsShouldNotBeFound("email.contains=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByEmailNotContainsSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where email does not contain DEFAULT_EMAIL
        defaultContactUsShouldNotBeFound("email.doesNotContain=$DEFAULT_EMAIL")

        // Get all the contactUsList where email does not contain UPDATED_EMAIL
        defaultContactUsShouldBeFound("email.doesNotContain=$UPDATED_EMAIL")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime equals to DEFAULT_CREATE_TIME
        defaultContactUsShouldBeFound("createTime.equals=$DEFAULT_CREATE_TIME")

        // Get all the contactUsList where createTime equals to UPDATED_CREATE_TIME
        defaultContactUsShouldNotBeFound("createTime.equals=$UPDATED_CREATE_TIME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsNotEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime not equals to DEFAULT_CREATE_TIME
        defaultContactUsShouldNotBeFound("createTime.notEquals=$DEFAULT_CREATE_TIME")

        // Get all the contactUsList where createTime not equals to UPDATED_CREATE_TIME
        defaultContactUsShouldBeFound("createTime.notEquals=$UPDATED_CREATE_TIME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsInShouldWork() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime in DEFAULT_CREATE_TIME or UPDATED_CREATE_TIME
        defaultContactUsShouldBeFound("createTime.in=$DEFAULT_CREATE_TIME,$UPDATED_CREATE_TIME")

        // Get all the contactUsList where createTime equals to UPDATED_CREATE_TIME
        defaultContactUsShouldNotBeFound("createTime.in=$UPDATED_CREATE_TIME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsNullOrNotNull() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime is not null
        defaultContactUsShouldBeFound("createTime.specified=true")

        // Get all the contactUsList where createTime is null
        defaultContactUsShouldNotBeFound("createTime.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime is greater than or equal to DEFAULT_CREATE_TIME
        defaultContactUsShouldBeFound("createTime.greaterThanOrEqual=$DEFAULT_CREATE_TIME")

        // Get all the contactUsList where createTime is greater than or equal to UPDATED_CREATE_TIME
        defaultContactUsShouldNotBeFound("createTime.greaterThanOrEqual=$UPDATED_CREATE_TIME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsLessThanOrEqualToSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime is less than or equal to DEFAULT_CREATE_TIME
        defaultContactUsShouldBeFound("createTime.lessThanOrEqual=$DEFAULT_CREATE_TIME")

        // Get all the contactUsList where createTime is less than or equal to SMALLER_CREATE_TIME
        defaultContactUsShouldNotBeFound("createTime.lessThanOrEqual=$SMALLER_CREATE_TIME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsLessThanSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime is less than DEFAULT_CREATE_TIME
        defaultContactUsShouldNotBeFound("createTime.lessThan=$DEFAULT_CREATE_TIME")

        // Get all the contactUsList where createTime is less than UPDATED_CREATE_TIME
        defaultContactUsShouldBeFound("createTime.lessThan=$UPDATED_CREATE_TIME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllContactusesByCreateTimeIsGreaterThanSomething() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        // Get all the contactUsList where createTime is greater than DEFAULT_CREATE_TIME
        defaultContactUsShouldNotBeFound("createTime.greaterThan=$DEFAULT_CREATE_TIME")

        // Get all the contactUsList where createTime is greater than SMALLER_CREATE_TIME
        defaultContactUsShouldBeFound("createTime.greaterThan=$SMALLER_CREATE_TIME")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */

    @Throws(Exception::class)
    private fun defaultContactUsShouldBeFound(filter: String) {
        restContactUsMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactUs.id?.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))

        // Check, that the count call also returns 1
        restContactUsMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultContactUsShouldNotBeFound(filter: String) {
        restContactUsMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restContactUsMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingContactUs() {
        // Get the contactUs
        restContactUsMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString()))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewContactUs() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size

        // Update the contactUs
        val updatedContactUs = contactUsRepository.findById(contactUs.id).get()
        // Disconnect from session so that the updates on updatedContactUs are not directly saved in db
        em.detach(updatedContactUs)
        updatedContactUs.userId = UPDATED_USER_ID
        updatedContactUs.email = UPDATED_EMAIL
        updatedContactUs.message = UPDATED_MESSAGE
        updatedContactUs.createTime = UPDATED_CREATE_TIME
        val contactUsDTO = contactUsMapper.toDto(updatedContactUs)

        restContactUsMockMvc.perform(
            put(ENTITY_API_URL_ID, contactUsDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(contactUsDTO))
        ).andExpect(status().isOk)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)
        val testContactUs = contactUsList[contactUsList.size - 1]
        assertThat(testContactUs.userId).isEqualTo(UPDATED_USER_ID)
        assertThat(testContactUs.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testContactUs.message).isEqualTo(UPDATED_MESSAGE)
        assertThat(testContactUs.createTime).isEqualTo(UPDATED_CREATE_TIME)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository).save(testContactUs)
    }

    @Test
    @Transactional
    fun putNonExistingContactUs() {
        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size
        contactUs.id = UUID.randomUUID()

        // Create the ContactUs
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactUsMockMvc.perform(
            put(ENTITY_API_URL_ID, contactUsDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(contactUsDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(0)).save(contactUs)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchContactUs() {
        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size
        contactUs.id = UUID.randomUUID()

        // Create the ContactUs
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactUsMockMvc.perform(
            put(ENTITY_API_URL_ID, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(contactUsDTO))
        ).andExpect(status().isBadRequest)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(0)).save(contactUs)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamContactUs() {
        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size
        contactUs.id = UUID.randomUUID()

        // Create the ContactUs
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactUsMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(contactUsDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(0)).save(contactUs)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateContactUsWithPatch() {
        contactUsRepository.saveAndFlush(contactUs)

        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size

// Update the contactUs using partial update
        val partialUpdatedContactUs = ContactUs().apply {
            id = contactUs.id

            email = UPDATED_EMAIL
            message = UPDATED_MESSAGE
        }

        restContactUsMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedContactUs.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedContactUs))
        )
            .andExpect(status().isOk)

// Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)
        val testContactUs = contactUsList.last()
        assertThat(testContactUs.userId).isEqualTo(DEFAULT_USER_ID)
        assertThat(testContactUs.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testContactUs.message).isEqualTo(UPDATED_MESSAGE)
        assertThat(testContactUs.createTime).isEqualTo(DEFAULT_CREATE_TIME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateContactUsWithPatch() {
        contactUsRepository.saveAndFlush(contactUs)

        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size

// Update the contactUs using partial update
        val partialUpdatedContactUs = ContactUs().apply {
            id = contactUs.id

            userId = UPDATED_USER_ID
            email = UPDATED_EMAIL
            message = UPDATED_MESSAGE
            createTime = UPDATED_CREATE_TIME
        }

        restContactUsMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedContactUs.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedContactUs))
        )
            .andExpect(status().isOk)

// Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)
        val testContactUs = contactUsList.last()
        assertThat(testContactUs.userId).isEqualTo(UPDATED_USER_ID)
        assertThat(testContactUs.email).isEqualTo(UPDATED_EMAIL)
        assertThat(testContactUs.message).isEqualTo(UPDATED_MESSAGE)
        assertThat(testContactUs.createTime).isEqualTo(UPDATED_CREATE_TIME)
    }

    @Throws(Exception::class)
    fun patchNonExistingContactUs() {
        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size
        contactUs.id = UUID.randomUUID()

        // Create the ContactUs
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactUsMockMvc.perform(
            patch(ENTITY_API_URL_ID, contactUsDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(contactUsDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(0)).save(contactUs)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchContactUs() {
        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size
        contactUs.id = UUID.randomUUID()

        // Create the ContactUs
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactUsMockMvc.perform(
            patch(ENTITY_API_URL_ID, UUID.randomUUID())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(contactUsDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(0)).save(contactUs)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamContactUs() {
        val databaseSizeBeforeUpdate = contactUsRepository.findAll().size
        contactUs.id = UUID.randomUUID()

        // Create the ContactUs
        val contactUsDTO = contactUsMapper.toDto(contactUs)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactUsMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(contactUsDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the ContactUs in the database
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeUpdate)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(0)).save(contactUs)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteContactUs() {
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)

        val databaseSizeBeforeDelete = contactUsRepository.findAll().size

        // Delete the contactUs
        restContactUsMockMvc.perform(
            delete(ENTITY_API_URL_ID, contactUs.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val contactUsList = contactUsRepository.findAll()
        assertThat(contactUsList).hasSize(databaseSizeBeforeDelete - 1)

        // Validate the ContactUs in Elasticsearch
        verify(mockContactUsSearchRepository, times(1)).deleteById(contactUs.id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchContactUs() {
        // Configure the mock search repository
        // Initialize the database
        contactUsRepository.saveAndFlush(contactUs)
        `when`(mockContactUsSearchRepository.search("id:${contactUs.id}"))
            .thenReturn(Stream.of(contactUs))
        // Search the contactUs
        restContactUsMockMvc.perform(get("$ENTITY_SEARCH_API_URL?query=id:${contactUs.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contactUs.id.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE.toString())))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(sameInstant(DEFAULT_CREATE_TIME))))
    }

    companion object {

        private val DEFAULT_USER_ID: UUID = UUID.randomUUID()
        private val UPDATED_USER_ID: UUID = UUID.randomUUID()

        private const val DEFAULT_EMAIL = "Y[f@9.<x?"
        private const val UPDATED_EMAIL = "G@q,b:(8.B"

        private const val DEFAULT_MESSAGE = "AAAAAAAAAA"
        private const val UPDATED_MESSAGE = "BBBBBBBBBB"

        private val DEFAULT_CREATE_TIME: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC)
        private val UPDATED_CREATE_TIME: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0)
        private val SMALLER_CREATE_TIME: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC)

        private val ENTITY_API_URL: String = "/api/contactuses"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"
        private val ENTITY_SEARCH_API_URL: String = "/api/_search/contactuses"

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): ContactUs {
            val contactUs = ContactUs(
                userId = DEFAULT_USER_ID,

                email = DEFAULT_EMAIL,

                message = DEFAULT_MESSAGE,

                createTime = DEFAULT_CREATE_TIME

            )

            return contactUs
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ContactUs {
            val contactUs = ContactUs(
                userId = UPDATED_USER_ID,

                email = UPDATED_EMAIL,

                message = UPDATED_MESSAGE,

                createTime = UPDATED_CREATE_TIME

            )

            return contactUs
        }
    }
}
