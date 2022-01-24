package com.shahad.dontsayit


import com.shahad.dontsayit.util.validateMail
import com.shahad.dontsayit.util.validatePasswords
import com.shahad.dontsayit.util.validateUsername
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationTest {

    @Test
    fun checkUsername() {
        val result1 = validateUsername("testtt") == null
        val result2 = validateUsername("test") == null
        val result3 = validateUsername("testtesttest") == null

        assertTrue(result1)//assertTrue
        assertFalse(result2)//assertFalse
        assertFalse(result3)//assertFalse
    }

    @Test
    fun checkEmail() {
        val result1 = validateMail("test123@gmail.com") == null
        val result2 = validateMail("test123gmail.com") == null

        assertTrue(result1)//assertTrue
        assertFalse(result2)//assertFalse
    }

    @Test
    fun checkPassword() {
        val result1 = validatePasswords("test1") == null
        val result2 = validatePasswords("test") == null
        val result3 = validatePasswords("Aatest") == null

        assertFalse(result1)//assertFalse
        assertFalse(result2)//assertFalse
        assertTrue(result3)//assertTrue
    }

    /*  @Test
      fun testAPI(){
              // call the api
              val api = MockBuilder.mockAPI
              val response = api.getWordsResponse().execute()
              // verify the response is OK
          assertTrue(response.code() == 200)

      }*/
}