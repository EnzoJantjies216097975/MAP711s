package com.map711s.namibiahockey.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.map711s.namibiahockey.data.local.dao.EventDao
import com.map711s.namibiahockey.data.local.dao.PlayerDao
import com.map711s.namibiahockey.data.local.dao.TeamDao
import com.map711s.namibiahockey.data.local.dao.UserDao
import com.map711s.namibiahockey.data.models.*
import com.map711s.namibiahockey.util.Constants.Database

/**
 * Main database class for the Namibia Hockey app.
 * This defines the database configuration and serves as the main access point
 * to the app's persisted data.
 */
@androidx.room.Database(
    entities = [
        User::class,
        Team::class,
        TeamPlayer::class,
        TeamStats::class,
        TeamMatchResult::class,
        Player::class,
        PlayerStats::class,
        PlayerMatchPerformance::class,
        Event::class,
        EventRegistration::class,
        Match::class
    ],
    version = Database.DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // Abstract methods returning DAOs - Room will generate implementations
    abstract fun userDao(): UserDao
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun eventDao(): EventDao

    companion object {
        // Singleton instance to ensure we don't create multiple database instances
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton database instance, creating it if necessary.
         *
         * @param context Application context used to create the database.
         * @return The singleton AppDatabase instance.
         */
        fun getInstance(context: Context): AppDatabase {
            // If instance exists, return it
            INSTANCE?.let { return it }

            // Otherwise create a new database instance with synchronized block
            // to ensure only one thread creates the database
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Database.DB_NAME
                )
                    // Allow destructive migration for simplified development
                    // In production, you should provide proper migration strategies
                    .fallbackToDestructiveMigration()
                    // Optional: Add a callback for database creation/opening
                    .addCallback(object : RoomDatabase.Callback() {
                        // You could override onCreate or onOpen for initialization
                    })
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}

/**
 * Type converters for Room to handle complex types that aren't natively supported.
 * This allows storing enum values, lists, dates, etc. in the database.
 */
class Converters {
    /**
     * Converts EventType enum to a string for storage
     */
    @androidx.room.TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    /**
     * Converts stored string back to EventType enum
     */
    @androidx.room.TypeConverter
    fun toEventType(value: String): EventType {
        return try {
            EventType.valueOf(value)
        } catch (e: Exception) {
            EventType.OTHER // Default value if conversion fails
        }
    }

    /**
     * Converts EventStatus enum to a string for storage
     */
    @androidx.room.TypeConverter
    fun fromEventStatus(value: EventStatus): String {
        return value.name
    }

    /**
     * Converts stored string back to EventStatus enum
     */
    @androidx.room.TypeConverter
    fun toEventStatus(value: String): EventStatus {
        return try {
            EventStatus.valueOf(value)
        } catch (e: Exception) {
            EventStatus.UPCOMING // Default value if conversion fails
        }
    }

    /**
     * Converts RegistrationStatus enum to a string for storage
     */
    @androidx.room.TypeConverter
    fun fromRegistrationStatus(value: RegistrationStatus): String {
        return value.name
    }

    /**
     * Converts stored string back to RegistrationStatus enum
     */
    @androidx.room.TypeConverter
    fun toRegistrationStatus(value: String): RegistrationStatus {
        return try {
            RegistrationStatus.valueOf(value)
        } catch (e: Exception) {
            RegistrationStatus.PENDING // Default value if conversion fails
        }
    }

    /**
     * Converts PaymentStatus enum to a string for storage
     */
    @androidx.room.TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String {
        return value.name
    }

    /**
     * Converts stored string back to PaymentStatus enum
     */
    @androidx.room.TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus {
        return try {
            PaymentStatus.valueOf(value)
        } catch (e: Exception) {
            PaymentStatus.PENDING // Default value if conversion fails
        }
    }

    /**
     * Converts MatchStatus enum to a string for storage
     */
    @androidx.room.TypeConverter
    fun fromMatchStatus(value: MatchStatus): String {
        return value.name
    }

    /**
     * Converts stored string back to MatchStatus enum
     */
    @androidx.room.TypeConverter
    fun toMatchStatus(value: String): MatchStatus {
        return try {
            MatchStatus.valueOf(value)
        } catch (e: Exception) {
            MatchStatus.SCHEDULED // Default value if conversion fails
        }
    }

    /**
     * Converts UserRole enum to a string for storage
     */
    @androidx.room.TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }

    /**
     * Converts stored string back to UserRole enum
     */
    @androidx.room.TypeConverter
    fun toUserRole(value: String): UserRole {
        return try {
            UserRole.valueOf(value)
        } catch (e: Exception) {
            UserRole.PLAYER // Default value if conversion fails
        }
    }

    /**
     * Converts MatchResult enum to a string for storage
     */
    @androidx.room.TypeConverter
    fun fromMatchResult(value: MatchResult): String {
        return value.name
    }

    /**
     * Converts stored string back to MatchResult enum
     */
    @androidx.room.TypeConverter
    fun toMatchResult(value: String): MatchResult {
        return try {
            MatchResult.valueOf(value)
        } catch (e: Exception) {
            MatchResult.DRAW // Default value if conversion fails
        }
    }

    /**
     * Additional TypeConverter methods could be added here for complex types
     * such as:
     * - Date objects
     * - Lists of primitives (using JSON serialization)
     * - Custom complex objects
     */
}