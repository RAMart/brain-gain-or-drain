(ns brain-gain-or-drain.screens.game
  (:refer-clojure :exclude [bound?])
  (:require [play-clj [core :refer :all :exclude [audio!]]
                      [g2d :refer :all]
                      [utils :as u]]
            [brain-gain-or-drain [entity :refer :all]
                                 [domain :refer :all]]))

(defn move-entity
  [delta-time logo]
  (let [delta-x (* 4 60 delta-time)]
    (assoc logo :x (-> logo :x (- delta-x)))))

(defn move-entities
  [delta-time entities]
  (mapv #(move-entity delta-time %) entities))

(defn recreate-entity
  [entity width height]
  (assoc entity
         :x width
         :y (rand-int height)))

(defscreen game-screen
  :on-show
  (fn [screen entities]
    (update! screen
             :camera (orthographic)
             :renderer (stage)
             :resources
             {
              :player (texture "brain.png")
              :logo (texture! (texture "clojure-logos.png") :split 64 64)
             })
    [])

  :on-render
  (fn [screen entities]
    (clear!)
    (letfn [(recreate-entity-on-screen [entity]
              (recreate-entity entity (width screen) (height screen)))]
      (->> entities
           (bind-entities (:resources screen))
           (move-entities (:delta-time screen))
           (when-entity gone? recreate-entity-on-screen)
           (render! screen))))

  :on-resize
  (fn [screen entities]
    (width! screen 800)))
