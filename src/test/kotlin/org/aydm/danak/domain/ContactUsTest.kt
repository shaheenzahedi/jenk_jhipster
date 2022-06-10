package org.aydm.danak.domain

import org.assertj.core.api.Assertions.assertThat
import org.aydm.danak.web.rest.equalsVerifier
import org.junit.jupiter.api.Test
import java.util.UUID

class ContactUsTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ContactUs::class)
        val contactUs1 = ContactUs()
        contactUs1.id = UUID.randomUUID()
        val contactUs2 = ContactUs()
        contactUs2.id = contactUs1.id
        assertThat(contactUs1).isEqualTo(contactUs2)
        contactUs2.id = UUID.randomUUID()
        assertThat(contactUs1).isNotEqualTo(contactUs2)
        contactUs1.id = null
        assertThat(contactUs1).isNotEqualTo(contactUs2)
    }
}
