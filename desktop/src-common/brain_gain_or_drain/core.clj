(ns brain-gain-or-drain.core
  (:require [play-clj.core :refer :all]
            [brain-gain-or-drain.screens.error :refer [error-screen]]))

(defgame brain-gain-or-drain-game
  :on-create
  (fn [this]
    (set-screen! this error-screen)))
