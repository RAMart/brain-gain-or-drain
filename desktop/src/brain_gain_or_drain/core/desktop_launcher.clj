(ns brain-gain-or-drain.core.desktop-launcher
  (:require [brain-gain-or-drain.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. brain-gain-or-drain-game
                     "Brain - Gain or Drain"
                     800
                     600)
  (Keyboard/enableRepeatEvents true))
