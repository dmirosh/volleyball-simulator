package game

data class Score(
    var teamA: Int = 0,
    var teamB: Int = 0
)

class Game {
    var score: Score = Score()
    var currentTeam: TeamName = TeamName.TEAM_A
    var teams: Map<TeamName, Team> = mapOf()

    init {
        reset()
    }

    fun reset() {
        score = Score()
        currentTeam = TeamName.TEAM_A
        teams = TeamName.values().associate { it to Team(it) }
    }

    fun switchTurn() {
        val otherTeam = currentTeam.otherTeam()
        teams[otherTeam]!!.removeLiberoFromField()
        teams[currentTeam]!!.addLiberoToField()
        currentTeam = otherTeam
    }

    fun addPoint(teamName: TeamName) {
        if (teamName == TeamName.TEAM_A) {
            score.teamA++
        } else {
            score.teamB++
        }
    }

    fun removePoint(teamName: TeamName) {
        if (teamName == TeamName.TEAM_A) {
            score.teamA--
        } else {
            score.teamB--
        }
    }

    fun moveLiberoToField(team: TeamName, newLibero: Player?, oldLibero: Player?) {
        teams[team]!!.moveLiberoToField(newLibero, oldLibero)
    }

    fun moveToField(player: Player, position: Int) {
        teams[player.teamName]!!.moveToField(player, position)
    }

    fun removeFromField(player: Player) {
        teams[player.teamName]!!.removeFromField(player)
    }

    fun makeTurnSuccess() {
        addPoint(currentTeam)
    }

    fun removeTurnSuccess() {
        removePoint(currentTeam)
    }

    fun makeTurnFailure() {
        val otherTeam = currentTeam.otherTeam()
        addPoint(otherTeam)
        switchTurn()
        teams[currentTeam]!!.shift()
    }

    fun removeFailureTurn() {
        removePoint(currentTeam)
        teams[currentTeam]!!.unshift()
        switchTurn()
    }

    fun playerOnPosition(teamName: TeamName, position: Int): Player? {
        return teams[teamName]!!.playerOnPosition(position)
    }

    fun teamPlayers(team: TeamName): List<Player> {
        return teams[team]!!.players
    }

    fun firstFreePosition(team: TeamName): Int? {
        return teams[team]!!.firstFreePosition()
    }
}