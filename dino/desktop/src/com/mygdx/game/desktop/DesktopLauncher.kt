package com.mygdx.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.mygdx.game.MyGame

fun main() {
    val config = LwjglApplicationConfiguration().apply {
        title = "Dino Run"
        width = 16 * 32
        height = 9 * 32
    }

    LwjglApplication(MyGame(), config)
}
