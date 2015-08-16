(ns brain-gain-or-drain.core
  (:refer-clojure :exclude [set-error-handler!])
  (:require [play-clj.core :refer :all]
            [brain-gain-or-drain.screens [error :refer [error-screen]]
                                         [title :refer [title-screen]]]))

(defn- set-error-handler!
  [game]
  (set-screen-wrapper!
   (fn [_ screen-fn]
     (try (screen-fn)
          (catch Exception e
            (.printStackTrace e)
            (set-screen! game error-screen))))))

(defgame brain-gain-or-drain-game
  :on-create
  (fn [this]
    (set-error-handler! this)
    (set-screen! this title-screen)))
