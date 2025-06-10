package com.helpdesk.api.config

import com.helpdesk.api.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This wraps our application's [User] entity to provide user details to Spring Security.
 * It exposes the [User]'s UUID [id] for easier access in security expressions.
 *
 * @param user The [User] entity to wrap.
 */
class CustomUserDetails(val user: User) : UserDetails {

    /**
     * Expose the user's UUID for custom security checks like @userSecurity.canAccessUser(#id).
     */
    val id: UUID
        get() = user.id

    /**
     * Returns the authorities granted to the user.
     * Spring Security expects roles to be prefixed with "ROLE_".
     */
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    }

    /**
     * Returns the password used to authenticate the user.
     */
    override fun getPassword(): String = user.passwordHash

    /**
     * Returns the username used to authenticate the user.
     */
    override fun getUsername(): String = user.username

    /**
     * Indicates whether the user's account has expired.
     * Always true for this application (account never expires).
     */
    override fun isAccountNonExpired(): Boolean = true

    /**
     * Indicates whether the user is locked or unlocked.
     * Always true for this application (account is never locked).
     */
    override fun isAccountNonLocked(): Boolean = true

    /**
     * Indicates whether the user's credentials (password) has expired.
     * Always true for this application (credentials never expire).
     */
    override fun isCredentialsNonExpired(): Boolean = true

    /**
     * Indicates whether the user is enabled or disabled.
     * Always true for this application (user is always enabled).
     */
    override fun isEnabled(): Boolean = true
}