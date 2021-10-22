import CronTimeEntity.AnyTime
import CronTimeEntity.Time
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MainTest(
    private val data: TestData
) {

    @Test
    fun test() {
        val result = calculateNextExecution(data.hours, data.minutes, data.task)
        Assert.assertEquals(data.expectedResult, result)
    }

    companion object {

        @Parameterized.Parameters(name = "{0}")
        @JvmStatic
        fun data(): Array<Array<Any>> {
            return arrayOf(
                // * *
                TestData(
                    task = Task(AnyTime, AnyTime, "-"),
                    hours = 10,
                    minutes = 10,
                    expectedResult = TaskNextExecutionResult(
                        hours = 10,
                        minutes = 10,
                        isNextDay = false
                    )
                ),
                // * X
                TestData(
                    task = Task(AnyTime, Time(30), "-"),
                    hours = 20,
                    minutes = 12,
                    expectedResult = TaskNextExecutionResult(
                        hours = 20,
                        minutes = 30,
                        isNextDay = false
                    )
                ),
                TestData(
                    task = Task(AnyTime, Time(30), "-"),
                    hours = 20,
                    minutes = 30,
                    expectedResult = TaskNextExecutionResult(
                        hours = 20,
                        minutes = 30,
                        isNextDay = false
                    )
                ),
                TestData(
                    task = Task(AnyTime, Time(30), "-"),
                    hours = 20,
                    minutes = 31,
                    expectedResult = TaskNextExecutionResult(
                        hours = 21,
                        minutes = 30,
                        isNextDay = false
                    )
                ),
                TestData(
                    task = Task(AnyTime, Time(30), "-"),
                    hours = 23,
                    minutes = 31,
                    expectedResult = TaskNextExecutionResult(
                        hours = 0,
                        minutes = 30,
                        isNextDay = true
                    )
                ),
                // X *
                TestData(
                    task = Task(Time(15), AnyTime, "-"),
                    hours = 15,
                    minutes = 35,
                    expectedResult = TaskNextExecutionResult(
                        hours = 15,
                        minutes = 35,
                        isNextDay = false
                    )
                ),
                TestData(
                    task = Task(Time(15), AnyTime, "-"),
                    hours = 12,
                    minutes = 35,
                    expectedResult = TaskNextExecutionResult(
                        hours = 15,
                        minutes = 0,
                        isNextDay = false
                    )
                ),
                TestData(
                    task = Task(Time(15), AnyTime, "-"),
                    hours = 17,
                    minutes = 35,
                    expectedResult = TaskNextExecutionResult(
                        hours = 15,
                        minutes = 0,
                        isNextDay = true
                    )
                ),
                // X X
                TestData(
                    task = Task(Time(5), Time(30), "-"),
                    hours = 1,
                    minutes = 35,
                    expectedResult = TaskNextExecutionResult(
                        hours = 5,
                        minutes = 30,
                        isNextDay = false
                    )
                ),
                TestData(
                    task = Task(Time(22), Time(30), "-"),
                    hours = 22,
                    minutes = 30,
                    expectedResult = TaskNextExecutionResult(
                        hours = 22,
                        minutes = 30,
                        isNextDay = false
                    )
                ),
                TestData(
                    task = Task(Time(5), Time(30), "-"),
                    hours = 10,
                    minutes = 35,
                    expectedResult = TaskNextExecutionResult(
                        hours = 5,
                        minutes = 30,
                        isNextDay = true
                    )
                )
            )
                .map { arrayOf<Any>(it) }
                .toTypedArray()
        }
    }
}

data class TestData(
    val task: Task,
    val hours: Int,
    val minutes: Int,
    val expectedResult: TaskNextExecutionResult
)