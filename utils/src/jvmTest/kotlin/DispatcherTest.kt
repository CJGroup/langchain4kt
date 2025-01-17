import io.github.stream29.langchain4kt.utils.DispatchStrategy.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DispatcherTest {
    @Test
    fun poll() {
        val (api1, api2, api3) = (1..3).map { TestApiProvider(it) }
        val pollingDispatcher = Polling(listOf(api1, api2, api3))
        val weightedDispatcher = WeightedPolling(mapOf(api1 to 1, api2 to 2, api3 to 3))
        val record = mutableListOf<Int>()
        runBlocking {
            repeat(9) {
                pollingDispatcher.dispatch {
                    record.add(it.index)
                }
            }
            repeat(12) {
                weightedDispatcher.dispatch {
                    record.add(it.index)
                }
            }
        }
        assertEquals(listOf(1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 2, 3, 3, 3, 1, 2, 2, 3, 3, 3), record)
    }

    @Test
    fun random() {
        val (api1, api2, api3) = (1..3).map { TestApiProvider(it) }
        val pollingDispatcher = Random(listOf(api1, api2, api3))
        val weightedDispatcher = WeightedRandom(mapOf(api1 to 1, api2 to 2, api3 to 3))
        val record = mutableMapOf(1 to 0, 2 to 0, 3 to 0)
        val weightedRecord = mutableMapOf(1 to 0, 2 to 0, 3 to 0)
        runBlocking {
            repeat(3000000) {
                pollingDispatcher.dispatch {
                    record[it.index] = record[it.index]!! + 1
                }
            }
            repeat(6000000) {
                weightedDispatcher.dispatch {
                    weightedRecord[it.index] = weightedRecord[it.index]!! + 1
                }
            }
        }
        infix fun Int.about(expected: Int) =
            this in expected - this / 100..expected + this / 100
        assertTrue(record.values.all { it about 1000000 })
        assertTrue(weightedRecord.values.mapIndexed { index, it -> it about 1000000 * (index + 1) }.all { it })
    }
}