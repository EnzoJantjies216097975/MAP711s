package com.map711s.namibiahockey.data.model

import java.util.Date

data class RoleChangeRequest(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val currentRole: UserRole = UserRole.PLAYER,
    val requestedRole: UserRole = UserRole.PLAYER,
    val reason: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val requestedAt: Date = Date(),
    val reviewedBy: String? = null,
    val reviewedAt: Date? = null,
    val adminResponse: String = "",
    val priority: RequestPriority = RequestPriority.NORMAL
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "userName" to userName,
            "userEmail" to userEmail,
            "currentRole" to currentRole.name,
            "requestedRole" to requestedRole.name,
            "reason" to reason,
            "status" to status.name,
            "requestedAt" to requestedAt,
            "reviewedBy" to (reviewedBy ?: ""),
            "reviewedAt" to (reviewedAt ?: Date()),
            "adminResponse" to adminResponse,
            "priority" to priority.name
        )
    }

    fun canBeApproved(): Boolean {
        return status == RequestStatus.PENDING
    }

    fun getRoleChangeDescription(): String {
        return "${currentRole.name.lowercase().replaceFirstChar { it.uppercase() }} → ${requestedRole.name.lowercase().replaceFirstChar { it.uppercase() }}"
    }
}

enum class RequestPriority {
    LOW, NORMAL, HIGH, URGENT
}