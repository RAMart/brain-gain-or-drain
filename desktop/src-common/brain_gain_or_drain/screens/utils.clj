(ns brain-gain-or-drain.screens.utils
  (:require [play-clj.core :refer :all :exclude [audio!]]
            [brain-gain-or-drain.utils :refer :all]))

(defn show-screen!
  [& new-screens]
  (apply set-screen!
         ;; FIXME: Hard-wired dependency
         @(resolve 'brain-gain-or-drain.core/brain-gain-or-drain-game)
         new-screens))

(defn limit-timetravel!
  [screen]
  (let [current-timeline (:timeline screen)
        max-timetravel-frames (int (* 8 (game :fps)))]
    (if (< max-timetravel-frames (count current-timeline))
      (merge screen
             (update! screen :timeline
                      (subvec current-timeline
                              (* 0.25 max-timetravel-frames))))
      screen)))
