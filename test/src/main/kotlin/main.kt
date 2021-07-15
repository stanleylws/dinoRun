import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import core.MyGame

fun main() {
    val config = LwjglApplicationConfiguration().apply {
        title = "Test"
        width = 16 * 32
        height = 9 * 32
    }

    LwjglApplication(MyGame(), config)
}
