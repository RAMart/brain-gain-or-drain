(ns brain-gain-or-drain.input
  (:require [play-clj.core :refer :all]))

(defn key-input!
  []
  (cond (key-pressed? :up) :move-up
        (key-pressed? :down) :move-down
        (key-pressed? :space) :move-back-in-time
        (key-pressed? :alt-right) :travel-back-in-time))

(defn touch-input!
  []
  (when (game :touched?)
    (let [x (game :x)
          y (game :y)
          right-side-touched? (> x (/ (game :width) 2))
          lower-half-touched? (< y (/ (game :height) 2))]
      (cond (and right-side-touched? lower-half-touched?) :travel-back-in-time
            right-side-touched? :move-back-in-time
            lower-half-touched? :move-down
            :else :move-up))))

(defn game-input!
  []
  (or (key-input!)
      (touch-input!)
      :move-not))
