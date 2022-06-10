package org.aydm.danak.service.dto

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.web.rest.equalsVerifier
import org.junit.jupiter.api.Test
import java.util.UUID

class ContactUsDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(ContactUsDTO::class)
        val contactUsDTO1 = ContactUsDTO()
        contactUsDTO1.id = UUID.randomUUID()
        val contactUsDTO2 = ContactUsDTO()
        assertThat(contactUsDTO1).isNotEqualTo(contactUsDTO2)
        contactUsDTO2.id = contactUsDTO1.id
        assertThat(contactUsDTO1).isEqualTo(contactUsDTO2)
        contactUsDTO2.id = UUID.randomUUID()
        assertThat(contactUsDTO1).isNotEqualTo(contactUsDTO2)
        contactUsDTO1.id = null
        assertThat(contactUsDTO1).isNotEqualTo(contactUsDTO2)
    }
}
