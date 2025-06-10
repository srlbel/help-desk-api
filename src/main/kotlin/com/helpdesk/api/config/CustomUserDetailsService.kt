package com.helpdesk.api.config

import com.helpdesk.api.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * This class is responsible for loading user-specific data during authentication.
 * It fetches the [User] entity from our [UserService] and wraps it in a [CustomUserDetails] object.
 *
 * @param userService The [UserService] used to retrieve user data from the database.
 */
@Service
class CustomUserDetailsService(private val userService: UserService) : UserDetailsService {

    /**
     * Locates the user based on the username.
     *
     * @param username The username identifying the user whose data is required.
     * @return A [UserDetails] object (specifically, our [CustomUserDetails] instance).
     * @throws UsernameNotFoundException if the user could not be found.
     */
    override fun loadUserByUsername(username: String): UserDetails {
        // Retrieve the User entity using your UserService.
        // findUserByUsername will throw ResourceNotFoundException if not found,
        // which Spring Security will convert to UsernameNotFoundException.
        val user = userService.findUserByUsername(username)
        return CustomUserDetails(user) // Wrap the User entity in your CustomUserDetails
    }
}