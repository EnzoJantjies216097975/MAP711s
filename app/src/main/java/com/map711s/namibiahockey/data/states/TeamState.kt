
import com.map711s.namibiahockey.data.model.Team

// State for a single Team
data class TeamState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val team: Team? = null,
    val teamId: String? = null,
    val error: String? = null
)


// State for a list of Teams
data class TeamListState(
    val isLoading: Boolean = false,
    val teams: List<Team> = emptyList(),
    val error: String? = null
)