import CronTimeEntity.AnyTime
import CronTimeEntity.Time
import java.io.InputStream
import java.util.*

fun main(args: Array<String>) {
    val currentTime = args.getOrNull(0)
    val currentTimeWords = currentTime?.split(':')
    val hours = currentTimeWords?.getOrNull(0)?.toIntOrNull()
    val minutes = currentTimeWords?.getOrNull(1)?.toIntOrNull()
    if (hours == null || minutes == null) {
        println("Incorrect input. Please, check currentTime argument.")
        return
    }
    val config = readConfig(System.`in`)
    if (config == null) {
        println("Incorrect input. Please, check the config.")
        return
    }
    processTasks(hours, minutes, config.tasks)
}

fun readConfig(input: InputStream): Config? {
    val scanner = Scanner(input)
    val tasks = mutableListOf<Task>()
    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
        if (line.isBlank()) break

        val words = line.split(" ")
        val minutes = words.getOrNull(0)?.toCronTimeEntity()
        val hours = words.getOrNull(1)?.toCronTimeEntity()
        val command = words.getOrNull(2)
        if (minutes != null && hours != null && command != null) {
            tasks.add(Task(hours, minutes, command))
        } else {
            println("$words $minutes $hours $command")
            return null
        }
    }
    return Config(tasks)
}

fun processTasks(currentHours: Int, currentMinutes: Int, tasks: List<Task>) {
    tasks.forEach { task ->
        val result = calculateNextExecution(currentHours, currentMinutes, task)
        val day = if (result.isNextDay) "tomorrow" else "today"
        val minutes = String.format("%02d", result.minutes)
        println("${result.hours}:${minutes} $day - ${task.command}")
    }
}

fun calculateNextExecution(currentHours: Int, currentMinutes: Int, task: Task): TaskNextExecutionResult {
    val hours = task.hours
    val minutes = task.minutes

    var isNextDay = false
    var nextMinutes = 0
    var nextHours = 0
    when (hours) {
        AnyTime -> {
            when (minutes) {
                is Time -> {
                    nextMinutes = minutes.value
                    if (minutes.value < currentMinutes) {
                        nextHours = (currentHours + 1) % 24
                        isNextDay = currentHours + 1 >= 24
                    } else {
                        nextHours = currentHours
                    }
                }
                else -> {
                    nextHours = currentHours
                    nextMinutes = currentMinutes
                }
            }
        }
        is Time -> {
            when (minutes) {
                is Time -> {
                    nextHours = hours.value
                    nextMinutes = minutes.value
                    isNextDay = if (hours.value > currentHours) {
                        false
                    } else {
                        !(hours.value == currentHours && minutes.value >= currentMinutes)
                    }
                }
                else -> {
                    nextHours = hours.value
                    if (hours.value > currentHours) {
                        nextMinutes = 0
                    } else if (hours.value == currentHours) {
                        nextMinutes = currentMinutes
                    } else {
                        nextMinutes = 0
                        isNextDay = true
                    }
                }
            }
        }
    }
    return TaskNextExecutionResult(nextHours, nextMinutes, isNextDay)

}

data class Config(val tasks: List<Task>)

data class Task(
    val hours: CronTimeEntity,
    val minutes: CronTimeEntity,
    val command: String
)

sealed class CronTimeEntity {
    data class Time(val value: Int) : CronTimeEntity()
    object AnyTime : CronTimeEntity()
}

fun String.toCronTimeEntity(): CronTimeEntity? {
    val number = this.toIntOrNull()
    return when {
        number != null -> Time(number)
        this == "*" -> AnyTime
        else -> null
    }
}

data class TaskNextExecutionResult(
    val hours: Int,
    val minutes: Int,
    val isNextDay: Boolean
)
