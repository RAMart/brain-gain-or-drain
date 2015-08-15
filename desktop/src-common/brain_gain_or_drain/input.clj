(ns brain-gain-or-drain.input
  (:require [play-clj.core :refer :all]))

(defn key-input!
  []
  (cond (key-pressed? :up) :move-up
        (key-pressed? :down) :move-down))

(defn touch-input!
  []
  (when (game :touched?)
    (let [x (game :x)
          y (game :y)
          lower-half-touched? (< y (/ (game :height) 2))]
      (if lower-half-touched?
          :move-down
          :move-up))))

(defn game-input!
  []
  (or (key-input!)
      (touch-input!)
      :move-not))
